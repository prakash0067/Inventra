package com.example.inventra;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.DBConnect.ApiService;
import com.example.inventra.DBConnect.CategoryResponse2;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {

    private TextInputEditText emailInput, passwordInput;
    private MaterialButton button;
    private Button forgot_password;
    // Hardcoded credentials for example

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailInput = (TextInputEditText) findViewById(R.id.email_input);
        passwordInput = (TextInputEditText) findViewById(R.id.passwordInput);
        button = findViewById(R.id.login_button);
        forgot_password = findViewById(R.id.forgot_pwd);

        // Clear previous errors
        emailInput.setError(null);
        passwordInput.setError(null);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setEnabled(false);
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                if (email.isEmpty()) {
                    emailInput.setError("Email is required");
                    button.setEnabled(true);
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    emailInput.setError("Enter a valid email address");
                    button.setEnabled(true);
                } else if (password.isEmpty()) {
                    passwordInput.setError("Password is required");
                    button.setEnabled(true);
                } else if (password.length() < 6 || password.length() > 12) {
                    passwordInput.setError("Password must be 6 to 12 characters");
                    button.setEnabled(true);
                } else {
                    // call PHP API via Retrofit for user's authentication
                    ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
                    Call<UserResponse> call = apiService.loginUser(email,password);
                    call.enqueue(new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                UserResponse apiUser = response.body();

                                // 1. Save in SharedPreferences
                                saveLoginData(new User(apiUser.getUserId(), apiUser.getName(), email, password, apiUser.getUserType(), apiUser.getProfilePic()));

                                // 2. Move to main screen
                                navigateToMainActivity();
                            } else {
                                Toast.makeText(Login.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                            }

                            button.setEnabled(true);
                        }

                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            Toast.makeText(Login.this, "API error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            button.setEnabled(true);
                        }
                    });
                }
            }
        });
    }

    private void saveLoginData(User user) {
        SharedPreferences sharedPref = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("UserEmailId", user.getEmail());
        editor.putString("UserPassword", user.getPassword());
        editor.putString("UserType", user.getUserType());
        editor.putInt("UserId", user.getUserId());
        editor.putString("UserName", user.getUsername());
        editor.putString("profilePicture", user.getProfile_pic());
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}