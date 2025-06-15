package com.example.inventra;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;

public class Loading extends AppCompatActivity {

    private static final long DISPLAY_TIME_MS = 1500; // 1.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView title = findViewById(R.id.appTitle);

        // Fade + Scale animation
        title.setScaleX(0.8f);
        title.setScaleY(0.8f);
        title.setAlpha(0f);
        title.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(700)
                .setStartDelay(100)
                .start();

        new Handler().postDelayed(() -> {
            SharedPreferences preferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
            String userType = preferences.getString("UserType", "");

            Intent intent = isLoggedIn ?
                    new Intent(Loading.this, MainActivity.class)
                    : new Intent(Loading.this, Login.class);

            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, DISPLAY_TIME_MS);
    }
}
