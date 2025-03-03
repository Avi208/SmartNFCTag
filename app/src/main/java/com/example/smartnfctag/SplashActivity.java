package com.example.smartnfctag;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000; // 3 seconds delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Delay and then check login state to navigate appropriately
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Example: Check login status using SharedPreferences
                SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

                if (isLoggedIn) {
                    // If user is already logged in, go to HomeActivity
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                } else {
                    // Otherwise, navigate to LoginActivity
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                }
                finish();
            }
        }, SPLASH_DELAY);
    }
}