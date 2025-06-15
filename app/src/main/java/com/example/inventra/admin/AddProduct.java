package com.example.inventra.admin;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.DBConnect.ApiService;
import com.example.inventra.DBConnect.CategoryResponse2;
import com.example.inventra.DBConnect.ProductResponse;
import com.example.inventra.FetchBarcode;
import com.example.inventra.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddProduct extends Fragment {

    private AutoCompleteTextView spinnerCategory;
    private List<Category> categoryList = new ArrayList<>();
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    private TextInputEditText etProductName, etProductDescription, etPrice, etQuantity, etBarcode;
    private MaterialButton btnSubmitProduct;
    private ActivityResultLauncher<Intent> barcodeLauncher;

    private Uri imageUri;
    private File imageFile;
    ImageView productImage, updateProductImg;

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    productImage.setImageURI(imageUri);
                } else {
                    Toast.makeText(getContext(), "Camera capture cancelled", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imageFile = getFileFromUri(imageUri);
                    if (imageFile != null) {
                        imageFile = compressImage(imageFile);
                        productImage.setImageURI(imageUri);
                    } else {
                        Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Gallery selection cancelled", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        etProductName = view.findViewById(R.id.etProductName);
        etProductDescription = view.findViewById(R.id.etProductDescription);
        etPrice = view.findViewById(R.id.etPrice);
        etQuantity = view.findViewById(R.id.etQuantity);
        etBarcode = view.findViewById(R.id.etBarcode);
        btnSubmitProduct = view.findViewById(R.id.btnSubmitProduct);

        productImage = view.findViewById(R.id.productImage);
        updateProductImg = view.findViewById(R.id.UpdateProductImg);

        updateProductImg.setOnClickListener(v -> {
            showImagePickerBottomSheet();
        });
        fetchCategories();

        barcodeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String scannedBarcode = result.getData().getStringExtra("scanned_barcode");
                        etBarcode.setText(scannedBarcode);
                    }
                }
        );

        btnSubmitProduct.setOnClickListener(v -> {
            String productName = etProductName.getText().toString().trim();
            String categoryName = spinnerCategory.getText().toString().trim();
            String productDescription = etProductDescription.getText().toString().trim();
            String price = etPrice.getText().toString().trim();
            String quantity = etQuantity.getText().toString().trim();
            String barcode = etBarcode.getText().toString().trim();

            if (productName.isEmpty() || categoryName.isEmpty() || productDescription.isEmpty() ||
                    price.isEmpty() || quantity.isEmpty() || barcode.isEmpty()) {
                Toast.makeText(getContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            Category selectedCategory = getCategoryByName(categoryName);
            if (selectedCategory == null) {
                Toast.makeText(getContext(), "Invalid category selected!", Toast.LENGTH_SHORT).show();
                return;
            }

            double priceValue;
            int quantityValue;
            try {
                priceValue = Double.parseDouble(price);
                quantityValue = Integer.parseInt(quantity);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Price and quantity must be valid numbers!", Toast.LENGTH_SHORT).show();
                return;
            }

            Product product = new Product(selectedCategory.getId(), 0, quantityValue, priceValue, productName, productDescription, barcode);
            uploadProductWithImage(product);
        });

        TextInputLayout layoutBarcode = view.findViewById(R.id.layoutBarcode);

        layoutBarcode.setEndIconOnClickListener(v -> {
            // barcode scanner code
            Intent intent = new Intent(getContext(), FetchBarcode.class);
            barcodeLauncher.launch(intent);
        });


        return view;
    }

    private void uploadProductWithImage(Product product) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        // Step 1: Create product
        Gson gson = new Gson();
        String productJson = gson.toJson(product);
        RequestBody requestBodyProduct = RequestBody.create(MediaType.parse("application/json"), productJson);

        Call<ProductResponse> createProductCall = apiService.createProduct(requestBodyProduct);
        createProductCall.enqueue(new Callback<ProductResponse>() {
            @Override
            public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProductResponse productResponse = response.body();
                    Log.d("API Response", "Create Product Response: " + productResponse.toString());
                    if ("success".equals(productResponse.getStatus())) {
                        // Step 2: Upload image if available
                        String productId = String.valueOf(productResponse.getProductId());
                        Log.d("Product Id ========",productId);
                        if (imageFile != null && imageFile.exists()) {
                            RequestBody requestBodyProductId = RequestBody.create(MediaType.parse("text/plain"), productId);
                            RequestBody requestBodyImage = RequestBody.create(MediaType.parse("image/*"), imageFile);
                            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestBodyImage);

                            Call<ProductResponse> uploadImageCall = apiService.uploadImage(requestBodyProductId, imagePart);
                            uploadImageCall.enqueue(new Callback<ProductResponse>() {
                                @Override
                                public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        ProductResponse imageResponse = response.body();
                                        Log.d("API Response", "Upload Image Response: " + imageResponse.toString());
                                        if ("success".equals(imageResponse.getStatus())) {
                                            Toast.makeText(getContext(), "Product and image added successfully!", Toast.LENGTH_SHORT).show();
                                            clearForm();
                                        } else {
                                            Toast.makeText(getContext(), "Product added, but failed to upload image: " + imageResponse.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(getContext(), "Product added, but failed to upload image!", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ProductResponse> call, Throwable t) {
                                    Toast.makeText(getContext(), "Product added, but image upload error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), "Product added successfully! No image selected.", Toast.LENGTH_SHORT).show();
                            clearForm();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to add product: " + productResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to add product! Response not successful", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ProductResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchCategories() {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        Call<CategoryResponse2> call = apiService.getCategories();
        call.enqueue(new Callback<CategoryResponse2>() {
            @Override
            public void onResponse(Call<CategoryResponse2> call, Response<CategoryResponse2> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CategoryResponse2 categoryResponse = response.body();
                    if (categoryResponse.getCategories() != null && !categoryResponse.getCategories().isEmpty()) {
                        categoryList = categoryResponse.getCategories();
                        List<String> categoryNames = new ArrayList<>();
                        for (Category category : categoryList) {
                            if (category.getName() != null && !category.getName().isEmpty()) {
                                categoryNames.add(category.getName());
                            }
                        }
                        if (!categoryNames.isEmpty()) {
                            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, categoryNames);
                            spinnerCategory.setAdapter(categoryAdapter);
                            spinnerCategory.setKeyListener(null); // Disable typing
                            spinnerCategory.setOnClickListener(v -> spinnerCategory.showDropDown());
                        } else {
                            Toast.makeText(getContext(), "No valid categories available", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "No categories available", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse2> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Category getCategoryByName(String categoryName) {
        for (Category category : categoryList) {
            if (category.getName().equals(categoryName)) {
                return category;
            }
        }
        return null;
    }

    private boolean hasCamera() {
        PackageManager pm = requireContext().getPackageManager();
        boolean hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
        Log.d("AddProduct", "Device has camera: " + hasCamera);
        return hasCamera;
    }

    private void showImagePickerBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_image_picker, null);
        bottomSheetDialog.setContentView(sheetView);

        View cameraOption = sheetView.findViewById(R.id.option_camera);
        if (!hasCamera()) {
            cameraOption.setVisibility(View.GONE); // Hide camera option if no camera
        } else {
            cameraOption.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                openCamera();
            });
        }

        sheetView.findViewById(R.id.option_gallery).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            openGallery();
        });

        sheetView.findViewById(R.id.option_cancel).setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }

    private void openCamera() {
        if (!hasCamera()) {
            Log.e("AddProduct", "Device has no camera");
            Toast.makeText(getContext(), "This device has no camera", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
            Toast.makeText(getContext(), "Camera permission required", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getActivity() != null) {
            PackageManager packageManager = getActivity().getPackageManager();
            if (intent.resolveActivity(packageManager) != null) {
                try {
                    imageFile = createImageFile();
                    if (imageFile == null) {
                        Toast.makeText(getContext(), "Failed to create image file", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    imageUri = FileProvider.getUriForFile(getContext(), requireContext().getPackageName() + ".provider", imageFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    cameraLauncher.launch(intent);
                } catch (Exception e) {
                    Log.e("AddProduct", "Error opening camera: " + e.getMessage(), e);
                    Toast.makeText(getContext(), "Error opening camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("AddProduct", "No camera app available. Intent: " + intent.toString());
                List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                Log.e("AddProduct", "Available activities: " + activities.size());
                for (ResolveInfo info : activities) {
                    Log.e("AddProduct", "Activity: " + info.activityInfo.packageName);
                }
                Toast.makeText(getContext(), "No camera app available", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("AddProduct", "Activity is null");
            Toast.makeText(getContext(), "Unable to access activity", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ?
                Manifest.permission.READ_MEDIA_IMAGES : Manifest.permission.READ_EXTERNAL_STORAGE;
        if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{permission}, REQUEST_GALLERY);
            Toast.makeText(getContext(), "Storage permission required", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_CAMERA) {
                openCamera();
            } else if (requestCode == REQUEST_GALLERY) {
                openGallery();
            }
        } else {
            Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() {
        try {
            String filename = "IMG_" + System.currentTimeMillis() + ".jpg";
            File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (storageDir == null || !storageDir.exists()) {
                Log.e("AddProduct", "Storage directory not available");
                Toast.makeText(getContext(), "Storage directory not available", Toast.LENGTH_SHORT).show();
                return null;
            }
            File image = new File(storageDir, filename);
            if (!image.createNewFile()) {
                Log.e("AddProduct", "Failed to create image file");
                Toast.makeText(getContext(), "Failed to create image file", Toast.LENGTH_SHORT).show();
                return null;
            }
            Log.d("AddProduct", "Image file created: " + image.getAbsolutePath());
            return image;
        } catch (IOException e) {
            Log.e("AddProduct", "Error creating image file: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error creating image file", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private File getFileFromUri(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            File tempFile = createImageFile();
            if (tempFile == null) {
                return null;
            }
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            Log.d("AddProduct", "Image file copied from URI: " + tempFile.getAbsolutePath());
            return tempFile;
        } catch (IOException e) {
            Log.e("AddProduct", "Error processing image: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Failed to process image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private File compressImage(File file) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            File compressedFile = createImageFile();
            if (compressedFile == null) {
                return file;
            }
            FileOutputStream outputStream = new FileOutputStream(compressedFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream); // 80% quality
            outputStream.flush();
            outputStream.close();
            Log.d("AddProduct", "Image compressed: " + compressedFile.getAbsolutePath());
            return compressedFile;
        } catch (IOException e) {
            Log.e("AddProduct", "Error compressing image: " + e.getMessage(), e);
            return file;
        }
    }

    private void clearForm() {
        etProductName.setText("");
        spinnerCategory.setText("");
        etProductDescription.setText("");
        etPrice.setText("");
        etQuantity.setText("");
        productImage.setImageResource(R.drawable.baseline_image_search_24);
        etBarcode.setText("");
        imageFile = null;
        imageUri = null;
    }
}