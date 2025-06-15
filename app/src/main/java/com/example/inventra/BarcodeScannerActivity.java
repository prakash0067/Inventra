package com.example.inventra;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.inventra.DBConnect.ApiClient;
import com.example.inventra.DBConnect.ApiService;
import com.example.inventra.admin.BarcodeScan.ScannedProductResponse;
import com.example.inventra.admin.BarcodeScan.ProductScan;
import com.example.inventra.admin.ProductBottomSheetFragment;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarcodeScannerActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ImageCapture imageCapture;
    private ProcessCameraProvider cameraProvider;
    private Button scanButton;
    private String barcodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        // Enable edge-to-edge drawing
        EdgeToEdge.enable(this);

        // Set status bar color and icon visibility
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent)); // Transparent for edge-to-edge
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable back button and set title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("BarCode Scanner");
        }

        previewView = findViewById(R.id.preview_view);
        scanButton = findViewById(R.id.scan_button);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1001);
        } else {
            startCamera();
        }

        scanButton.setOnClickListener(v -> captureImage());
    }

    private void startCamera() {
        ProcessCameraProvider.getInstance(this).addListener(() -> {
            try {
                cameraProvider = ProcessCameraProvider.getInstance(this).get();
                Preview preview = new Preview.Builder().build();
                imageCapture = new ImageCapture.Builder().build();

                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void captureImage() {
        imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                Bitmap bitmap = imageToBitmap(image);
                scanBarcode(bitmap);
                image.close();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Toast.makeText(BarcodeScannerActivity.this, "Capture failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Bitmap imageToBitmap(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void scanBarcode(Bitmap bitmap) {
        InputImage inputImage = InputImage.fromBitmap(bitmap, 0);
        BarcodeScanner scanner = BarcodeScanning.getClient();

        scanner.process(inputImage)
                .addOnSuccessListener(barcodes -> {
                    if (!barcodes.isEmpty()) {
                        barcodeText = barcodes.get(0).getDisplayValue();
                        Log.d("====== Barcode Text: ",barcodeText);

                        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
                        Call<ScannedProductResponse> call = apiService.getProductByBarcode(barcodeText);

                        call.enqueue(new Callback<ScannedProductResponse>() {
                            @Override
                            public void onResponse(Call<ScannedProductResponse> call, Response<ScannedProductResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    ScannedProductResponse productResponse = response.body();
                                    if (productResponse.isSuccess()) {
                                        ProductScan product = productResponse.getProduct();

                                        // Show bottom sheet with product
                                        ProductBottomSheetFragment.newInstance(product)
                                                .show(getSupportFragmentManager(), "product_bottom_sheet");

                                        stopCamera(); // Stop after showing result
                                    } else {
                                        Toast.makeText(BarcodeScannerActivity.this, productResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(BarcodeScannerActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ScannedProductResponse> call, Throwable t) {
                                Toast.makeText(BarcodeScannerActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(BarcodeScannerActivity.this, "No barcode detected", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(BarcodeScannerActivity.this, "Barcode scan failed", Toast.LENGTH_SHORT).show());
    }

    private void stopCamera() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Close this activity and return to the previous one
        return true;
    }

    @Override
    public void onBackPressed() {
        stopCamera(); // Optional: stop camera cleanly
        super.onBackPressed(); // Then continue with normal back navigation
    }

}
