package ir.treeroot.psyazi.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.muddzdev.styleabletoast.StyleableToast;


import java.util.Objects;

import ir.treeroot.psyazi.Interface.Api;
import ir.treeroot.psyazi.MainActivity;
import ir.treeroot.psyazi.R;
import ir.treeroot.psyazi.Utils.APIClient;
import ir.treeroot.psyazi.Utils.Link;
import ir.treeroot.psyazi.model.Users;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.treeroot.psyazi.Utils.Link.MyPref;

public class signIn extends AppCompatActivity {

    TextInputEditText phoneNumber_signUp, username_signUp, password_signUp, aliasName_signUp;
    AppCompatButton signUp_btn;
    Api request;
    SharedPreferences shPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        init();

        shPrefs();

    }

    private void shPrefs() {

        shPref = getSharedPreferences(MyPref, Context.MODE_PRIVATE);

        if (shPref.contains(Link.username)) {

            startActivity(new Intent(signIn.this, MainActivity.class));
            finish();

        }


    }

    private void init() {

        username_signUp = findViewById(R.id.username_signUp);
        password_signUp = findViewById(R.id.password_signUp);
        phoneNumber_signUp = findViewById(R.id.phoneNumber_signUp);
        aliasName_signUp = findViewById(R.id.aliasName_signUp);
        signUp_btn = findViewById(R.id.signUp_btn);

        request = APIClient.getApiClient(Link.url).create(Api.class);

        signUp_btn.setOnClickListener(v -> requestSignup());
        username_signUp.addTextChangedListener(SignUpWatcher);
        password_signUp.addTextChangedListener(SignUpWatcher);
        phoneNumber_signUp.addTextChangedListener(SignUpWatcher);
        aliasName_signUp.addTextChangedListener(SignUpWatcher);

    }


    private void requestSignup() {

        ProgressDialog pd = new ProgressDialog(this);
        pd.show();
        pd.setContentView(R.layout.progressbar);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        String username = Objects.requireNonNull(username_signUp.getText()).toString();
        String password = Objects.requireNonNull(password_signUp.getText()).toString();
        String pNumber = Objects.requireNonNull(phoneNumber_signUp.getText()).toString();
        String aliasname = Objects.requireNonNull(aliasName_signUp.getText()).toString();

        Call<Users> call = request.register(username, password, pNumber, aliasname);

        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {

                assert response.body() != null;

                switch (response.body().getResponse()) {

                    case "USER_REGISTER":

                        new StyleableToast
                                .Builder(getApplicationContext())
                                .text("این اکانت قبلا ساخته شده است")
                                .textColor(Color.WHITE)
                                .backgroundColor(Color.parseColor("#FF018786"))
                                .font(R.font.isans)
                                .show();

                        break;

                    case "SUCCESS":

                        new StyleableToast
                                .Builder(getApplicationContext())
                                .text("اکانت شما با موفقیت ساخته شد")
                                .textColor(Color.WHITE)
                                .backgroundColor(Color.parseColor("#FF018786"))
                                .font(R.font.isans)
                                .show();

                        SharedPreferences.Editor sEdit = shPref.edit();
                        sEdit.putString(Link.username, username).apply();
                        startActivity(new Intent(signIn.this, MainActivity.class));
                        finish();

                        break;

                    case "WRONG":

                        new StyleableToast
                                .Builder(getApplicationContext())
                                .text("اتصال به سرور قطع شد")
                                .textColor(Color.WHITE)
                                .backgroundColor(Color.parseColor("#FFFA3C3C"))
                                .font(R.font.isans)
                                .show();

                        break;

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

    private final TextWatcher SignUpWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String usernameInput = Objects.requireNonNull(username_signUp.getText()).toString().trim();
            String passwordInput = Objects.requireNonNull(password_signUp.getText()).toString().trim();
            String phoneNumberInput = Objects.requireNonNull(phoneNumber_signUp.getText()).toString().trim();
            String aliasnameInput = Objects.requireNonNull(aliasName_signUp.getText()).toString().trim();

            signUp_btn.setEnabled(!usernameInput.isEmpty() && !passwordInput.isEmpty() && !phoneNumberInput.isEmpty() &&
                    !aliasnameInput.isEmpty() && !(usernameInput.length() < 6) && !(passwordInput.length() < 6)
                    && !(phoneNumberInput.length() < 11) && phoneNumberInput.startsWith("09") && !(aliasnameInput.length() < 3));


            if (username_signUp.length() < 6) {

                username_signUp.setError("حدائقل شش حرف الزامی است");

            }

            if (password_signUp.length() < 6) {

                password_signUp.setError("حدائقل شش حرف الزامی است");

            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    };

}