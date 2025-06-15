package com.example.inventra;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.DBConnect.ApiService;
import com.example.inventra.admin.Profile.User;
import com.example.inventra.admin.Profile.UserProfileResponse;
import com.example.inventra.admin.Profile.UploadResponse;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Profile extends Fragment {

    private TextView nameText, usernameText, emailText, createdAtText;
    private ShapeableImageView profileImage;
    LinearLayout logoutButton, languageButton, loginButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Text views
        nameText = view.findViewById(R.id.profile_name);
        usernameText = view.findViewById(R.id.profile_username);
        emailText = view.findViewById(R.id.profile_email);
        createdAtText = view.findViewById(R.id.profile_created_at);

        logoutButton = view.findViewById(R.id.logoutBtn);
        languageButton = view.findViewById(R.id.languageBtn);
        loginButton = view.findViewById(R.id.loginAndSecureBtn);

        // Profile picture and camera icon
        profileImage = view.findViewById(R.id.profile_image);

        // Click listeners for profile image & camera icon
        View.OnClickListener openPickerMenu = v -> showImagePickerBottomSheet();
        profileImage.setOnClickListener(openPickerMenu);
        // Load profile data from API

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("LoginPrefs", getContext().MODE_PRIVATE);
        int userIdInt = sharedPreferences.getInt("UserId", -1);

        if (userIdInt == -1) {
            Toast.makeText(getContext(), "User ID not found", Toast.LENGTH_SHORT).show();
        } else {
            loadUserProfile(userIdInt);
        }

        // logout button functionality
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutConfirmationDialog();
            }
        });

        return view;
    }

    private void loadUserProfile(int userId) {
        Retrofit retrofit = ApiClient.getRetrofitInstance();
        ApiService apiService = retrofit.create(ApiService.class);

        Call<UserProfileResponse> call = apiService.getUserProfile(userId);

        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call, @NonNull Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();

                    nameText.setText(user.getUsername());
                    usernameText.setText(user.getUser_type());
                    emailText.setText(user.getEmail());
                    createdAtText.setText("Created on: " + user.getCreated_at());

                    // updating image name in shared pref
                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("LoginPrefs", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("profilePicture", user.getProfile_pic());
                    editor.apply();

                    String imageUrl = ApiClient.BASE_PROFILE_URL + user.getProfile_pic();
                    Glide.with(requireContext())
                            .load(imageUrl)
                            .error(R.drawable.person_defualt_image)
                            .into(profileImage);

                    // Refresh from SharedPreferences
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).updateProfileImage();
                    }
                } else {
                    Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImagePickerBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_image_picker, null);

        TextView optionCamera = sheetView.findViewById(R.id.option_camera);
        TextView optionGallery = sheetView.findViewById(R.id.option_gallery);
        TextView optionCancel = sheetView.findViewById(R.id.option_cancel);

        optionCamera.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .cameraOnly()
                    .cropSquare()
                    .compress(512)
                    .start();
            dialog.dismiss();
        });

        optionGallery.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .galleryOnly()
                    .cropSquare()
                    .compress(512)
                    .start();
            dialog.dismiss();
        });

        optionCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(sheetView);
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            String filePath = data.getStringExtra(ImagePicker.EXTRA_FILE_PATH);
            Log.d("Data ================",filePath);

            if (filePath != null) {
                File file = new File(filePath);
                uploadImageToServer(file);
            } else {
                Toast.makeText(getContext(), "Unable to get file path", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Image selection cancelled or failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageToServer(File file) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("profile_image", file.getName(), requestFile);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("LoginPrefs", getContext().MODE_PRIVATE);
        int userIdInt = sharedPreferences.getInt("UserId", -1);

        if (userIdInt == -1) {
            Toast.makeText(getContext(), "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userIdInt));
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        Call<UploadResponse> call = apiService.uploadProfileImage(body, userId);
        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(@NonNull Call<UploadResponse> call, @NonNull Response<UploadResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(getContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    loadUserProfile(userIdInt);
                } else {
                    try {
                        String error = response.errorBody().string(); // Get server response message
                        Log.e("UPLOAD_FAILED", error);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UploadResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Upload error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences preferences = requireContext().getSharedPreferences("LoginPrefs", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                    Intent intent = new Intent(getContext(), Login.class);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
