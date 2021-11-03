package ir.treeroot.psyazi.Ui.register;

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

import ir.treeroot.psyazi.Utils.APIClient;
import ir.treeroot.psyazi.Interface.Api;
import ir.treeroot.psyazi.Utils.Link;
import ir.treeroot.psyazi.Ui.MainActivity;
import ir.treeroot.psyazi.R;
import ir.treeroot.psyazi.model.Users;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static ir.treeroot.psyazi.Utils.Link.MyPref;

public class login extends AppCompatActivity {

    AppCompatButton login_btn,signUp_btn_loginPage;
    TextInputEditText username_login, password_login;
    Api request;
    SharedPreferences shPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        shPrefs();


    }

    private void shPrefs() {

        shPref = getSharedPreferences(MyPref, Context.MODE_PRIVATE);

        if (shPref.contains(Link.username)) {

            startActivity(new Intent(login.this, MainActivity.class));
            finish();

        }

    }


    public void init() {

        username_login = findViewById(R.id.username_login);
        password_login = findViewById(R.id.password_login);
        login_btn = findViewById(R.id.login_btn);
        signUp_btn_loginPage=findViewById(R.id.signUp_btn_loginPage);

        request = APIClient.getApiClient(Link.url).create(Api.class);

        login_btn.setOnClickListener(v -> requestLogin());
        username_login.addTextChangedListener(loginWatcher);
        password_login.addTextChangedListener(loginWatcher);
        signUp_btn_loginPage.setOnClickListener(v -> startActivity(new Intent(login.this,signIn.class)) );

    }

    private void requestLogin() {

        ProgressDialog pd = new ProgressDialog(this);
        pd.show();
        pd.setContentView(R.layout.progressbar);
        pd.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        String username = Objects.requireNonNull(username_login.getText()).toString();
        String password = Objects.requireNonNull(password_login.getText()).toString();

        Call<Users> call = request.loginAccount(username, password);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {

                assert response.body() != null;

                if (response.body().getResponse().equals("USER_LOGIN")) {

                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text("خوش آمدید")
                            .textColor(Color.WHITE)
                            .backgroundColor(Color.parseColor("#FF018786"))
                            .font(R.font.isans)
                            .show();

                    SharedPreferences.Editor sEdit = shPref.edit();
                    sEdit.putString(Link.username, username).apply();

                    startActivity(new Intent(login.this, MainActivity.class));
                    finish();

                } else if (response.body().getResponse().equals("NO_ACCOUNT")) {

                    new StyleableToast
                            .Builder(getApplicationContext())
                            .text("نام کاربری یا گذرواژه اشتباه است")
                            .textColor(Color.WHITE)
                            .backgroundColor(Color.parseColor("#FF018786"))
                            .font(R.font.isans)
                            .show();

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
                Toast.makeText(login.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });

    }


    private final TextWatcher loginWatcher = new TextWatcher() {


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String usernameInput = Objects.requireNonNull(username_login.getText()).toString().trim();
            String passwordInput = Objects.requireNonNull(password_login.getText()).toString().trim();

            login_btn.setEnabled(!usernameInput.isEmpty() && !passwordInput.isEmpty()
                    && !(usernameInput.length() < 6) && !(passwordInput.length() < 6));
            if (username_login.length() < 6) {
                username_login.setError("حدائقل شش حرف الزامی است");
            }

            if (password_login.length() < 6) {
                password_login.setError("حدائقل شش حرف الزامی است");
            }

        }


        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}