package com.example.tbscan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // The layout is to display app info
        setContentView(R.layout.splash_screen);

        // Start main activity
        loadLoginPageWithDelay();

    }

    private void loadLoginPageWithDelay(){
        new Handler().postDelayed(
                ()-> {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    finish();
                }
                ,1525
        );
    }
}
