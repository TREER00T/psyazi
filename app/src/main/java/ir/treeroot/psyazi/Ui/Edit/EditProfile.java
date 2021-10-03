package ir.treeroot.psyazi.Ui.Edit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.muddzdev.styleabletoast.StyleableToast;


import ir.treeroot.psyazi.Utils.APIClient;
import ir.treeroot.psyazi.Interface.Api;
import ir.treeroot.psyazi.R;
import ir.treeroot.psyazi.Utils.Link;
import ir.treeroot.psyazi.model.Users;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.treeroot.psyazi.Utils.Link.MyPref;

public class EditProfile extends AppCompatActivity {

    ImageView checked;
    EditText aliasname;
    Api request;
    SharedPreferences shPref;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        init();

        GetName();

    }

    public void init() {

        checked = findViewById(R.id.check_name);
        aliasname = findViewById(R.id.fNameAndLName);

        checked.setOnClickListener(v -> RequestFromServer());
        findViewById(R.id.back_name).setOnClickListener(v -> finish());
        shPref = getSharedPreferences(MyPref, Context.MODE_PRIVATE);
        username = shPref.getString("username", "");

        request = APIClient.getApiClient(Link.url).create(Api.class);

    }

    private void GetName() {

        Call<Users> call = request.getAliasName(username);

        call.enqueue(new Callback<Users>() {

            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {

                if (response.isSuccessful()) {

                    assert response.body() != null;
                    String aliasName = response.body().getAliasname();
                    aliasname.setText(aliasName);

                }

            }

            @Override
            public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {
            }

        });

    }

    private void RequestFromServer() {

        String aliasName = aliasname.getText().toString();

        if (aliasName.isEmpty()) {

            aliasname.setError("لطفا فیلد خالی را پر کنید");

        } else {

            ProgressDialog pd = new ProgressDialog(this);
            pd.show();
            pd.setContentView(R.layout.progressbar);
            pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            Call<Users> call = request.aliasName(username, aliasName);

            call.enqueue(new Callback<Users>() {

                @Override
                public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {

                    assert response.body() != null;

                    if (response.body().getResponse().equals("SUCCESS")) {

                        finish();

                    }

                    pd.dismiss();

                }

                @Override
                public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {

                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text("خطا در اتصال سرور")
                            .textColor(Color.WHITE)
                            .backgroundColor(Color.parseColor("#FFFA3C3C"))
                            .font(R.font.isans)
                            .show();

                }

            });

        }

    }

}