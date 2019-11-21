package com.example.tbscan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    public static String USER_NAME = "user_name";
    public static String LOGIN_STATUS = "login_status";

    public static String TB_PREF = "tb_preference";

    boolean isLoggedIn;
    String user;

    SharedPreferences sharedPreferences;

    ActionBar actionBar;
    TextInputEditText usernameTxtInLay, passwordTxtInLay;
    Button login;
    ProgressDialog loginProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // hide actionbar
        actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.hide();


        // set up sharedPreference and get stored values
        sharedPreferences = getSharedPreferences(TB_PREF, MODE_PRIVATE);
        user = sharedPreferences.getString(USER_NAME, "");
        isLoggedIn = sharedPreferences.getBoolean(LOGIN_STATUS, false);


        if (isLoggedIn) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra(USER_NAME, user);
            startActivity(intent);
            finish();
        }

        usernameTxtInLay = findViewById(R.id.username);
        usernameTxtInLay.setText(user);
        passwordTxtInLay = findViewById(R.id.password);

        login = findViewById(R.id.btn_login);
        login.setOnClickListener((v -> {
            showProgressDialog();
            attemptLogin(usernameTxtInLay.getText(), passwordTxtInLay.getText());
        }));
    }

    private void showProgressDialog(){
        loginProgressDialog = new ProgressDialog(this);
        loginProgressDialog.setMessage("Logging in");
        loginProgressDialog.setCancelable(false);
        loginProgressDialog.show();
    }

    private void dismissProgressDialog(){
        loginProgressDialog.cancel();
    }

    private void attemptLogin(Editable usernameEd, Editable passwordEd){
        //TODO: Login actions //this is only a placeholder code
        new Handler().postDelayed(() -> {
            dismissProgressDialog();

            String username = Objects.requireNonNull(usernameEd).toString().trim();
            String password = Objects.requireNonNull(passwordEd).toString().trim();

            if (username.equals("ADMIN") && password.equals("admin")) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                saveUserSharedPreference(username);
            } else {
                Toast.makeText(this, "Username or password incorrect", Toast.LENGTH_LONG).show();
            }

        }, 1500);
    }

    private void saveUserSharedPreference(String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME, username);
        editor.putBoolean(LOGIN_STATUS, true);
        editor.apply();
    }
}