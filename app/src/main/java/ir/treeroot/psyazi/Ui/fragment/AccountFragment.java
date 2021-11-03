package ir.treeroot.psyazi.Ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import ir.treeroot.psyazi.Ui.Favorite.FavoriteActivity;
import ir.treeroot.psyazi.Utils.APIClient;
import ir.treeroot.psyazi.Interface.Api;
import ir.treeroot.psyazi.R;
import ir.treeroot.psyazi.Ui.About.About;
import ir.treeroot.psyazi.Ui.Edit.EditProfile;
import ir.treeroot.psyazi.Utils.Link;
import ir.treeroot.psyazi.model.Users;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static android.app.Activity.RESULT_OK;
import static ir.treeroot.psyazi.Utils.Link.MyPref;
import static ir.treeroot.psyazi.Utils.Link.profile_img;


public class AccountFragment extends Fragment {

    CardView cardView, about,favorite;
    Api request;
    ImageView profile_loader;
    String lineEnd = "\r\n", twoHyphens = "--", boundary = "*****", username;
    int bytesAvailable, bufferSize, bytesRead, maxBufferSize = 1024 * 1024;
    byte[] buffer;
    FloatingActionButton SelectImageGallery, uploadProfile;
    Toolbar toolbar_name;
    SharedPreferences shPref;
    Thread upload_profile_thread;
    Handler uploadHandler;
    ProgressDialog pd;
    Uri mCropImageUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        init(v);

        GetData();

        return v;

    }


    private void permissionRuntimeGallery() {

        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestCameraPermission();

        } else {

            chooseFile();

        }

    }

    private void requestCameraPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {

            new AlertDialog.Builder(requireActivity())
                    .setTitle("درخواست مجوز")
                    .setMessage("برای دسترسی به گالری باید مجوز را تایید کنید")
                    .setPositiveButton("موافقم", (dialogInterface, i) -> reqPermission())
                    .setNegativeButton("لغو", (dialogInterface, i) -> dialogInterface.dismiss())
                    .create()
                    .show();

        } else {

            reqPermission();

        }

    }

    private void reqPermission() {

        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Link.READ_EXTERNAL_STORAGE_REQUEST_COD);

    }


    private void chooseFile() {

        CropImage.startPickImageActivity(requireActivity(),this);

    }

    public void init(View v) {

        cardView = v.findViewById(R.id.aliasname_cardview);
        about = v.findViewById(R.id.about);
        profile_loader = v.findViewById(R.id.profile_loader);
        SelectImageGallery = v.findViewById(R.id.floatingAction);
        uploadProfile = v.findViewById(R.id.uploadProfile);
        toolbar_name = v.findViewById(R.id.toolbar_name);
        favorite = v.findViewById(R.id.favorite);

        cardView.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), EditProfile.class)));
        about.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), About.class)));
        favorite.setOnClickListener(v1 -> startActivity(new Intent(getActivity(), FavoriteActivity.class)));

        toolbar_name.setSelected(true);

        pd = new ProgressDialog(getActivity());

        SelectImageGallery.setOnClickListener(v1 -> permissionRuntimeGallery());


        shPref = requireActivity().getSharedPreferences(MyPref, Context.MODE_PRIVATE);
        username = shPref.getString("username", "");

        uploadHandler = new Handler();
        request = APIClient.getApiClient(Link.url).create(Api.class);

    }


    private void GetData() {

        Call<Users> call = request.Get_Profile(username);

        call.enqueue(new Callback<Users>() {

            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {

                if (response.isSuccessful()) {

                    String aliasName = response.body().getAliasname();
                    String image = response.body().getImage();
                    Picasso.get()
                            .load(image).networkPolicy(NetworkPolicy.NO_CACHE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .into(profile_loader);
                    toolbar_name.setTitle(aliasName);

                }

            }

            @Override
            public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {
            }

        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {

            Uri imageUri = CropImage.getPickImageResultUri(requireActivity(), data);

            if (CropImage.isReadExternalStoragePermissionsRequired(requireActivity(), imageUri)) {

                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

            } else {

                startCropImageActivity(imageUri);

            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri uri = result.getUri();

                String filePath = uri.getPath();

                profile_loader.setImageURI(uri);

                uploadProfile.setVisibility(View.VISIBLE);

                uploadProfile.setOnClickListener(v -> {

                    pd.show();
                    pd.setContentView(R.layout.progressbar);
                    pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    upload_profile_thread = new Thread(() -> uploadFile(filePath));
                    upload_profile_thread.start();

                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Toast.makeText(getActivity(), "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();

            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            startCropImageActivity(mCropImageUri);

        } else {

            Toast.makeText(getActivity(), "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();

        }

    }

    private void startCropImageActivity(Uri imageUri) {

        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(requireActivity(),this);

    }

    private void uploadFile(String filePath) {

        File file = new File(filePath);

        try {

            FileInputStream fileInputStream = new FileInputStream(file);
            URL url = new URL(profile_img + username);

            //HttpsURLConnection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("ENCTYPE", "multipart/form-data");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            connection.setRequestProperty("uploaded_file", filePath);

            DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd + "Content-Disposition: form-data;" +
                    " name=\"uploaded_file\";filename=\"" + filePath + "\"\r\n\r\n");

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {

                dataOutputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            }

            dataOutputStream.writeBytes(lineEnd);
            dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            if (connection.getResponseCode() == 200) {

                uploadHandler.post(() -> {

                    uploadProfile.setVisibility(View.GONE);
                    pd.dismiss();
                    GetData();

                });

            }

            fileInputStream.close();
            dataOutputStream.flush();
            dataOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
