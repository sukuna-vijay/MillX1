package com.example.millx;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QualityReportActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 101;
    private PreviewView viewFinder;
    private ImageView ivCapturedPreview;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private String productName;
    private int productImageRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quality_report);

        viewFinder = findViewById(R.id.viewFinder);
        ivCapturedPreview = findViewById(R.id.iv_captured_preview);
        ImageView btnBack = findViewById(R.id.btn_back);
        ImageView ivProductThumb = findViewById(R.id.iv_product_thumb);
        TextView tvTitle = findViewById(R.id.tv_title);
        
        View btnShutter = findViewById(R.id.btn_shutter);
        View btnGalleryOverlay = findViewById(R.id.btn_overlay_gallery);
        View btnFlash = findViewById(R.id.btn_flash);
        MaterialButton btnSubmitReport = findViewById(R.id.btn_submit_report);

        // Get data from intent
        productName = getIntent().getStringExtra("product_name");
        productImageRes = getIntent().getIntExtra("product_image", R.drawable.ic_rice_rate);

        if (productName != null) {
            tvTitle.setText("Quality Check for " + productName);
        }
        
        if (ivProductThumb != null) {
            ivProductThumb.setImageResource(productImageRes);
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Initialize Gallery Launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        showSelectedImage(selectedImageUri);
                    }
                }
        );

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }

        cameraExecutor = Executors.newSingleThreadExecutor();

        // Shutter Button Logic
        if (btnShutter != null) {
            btnShutter.setOnClickListener(v -> takePhoto());
        }

        // Gallery Button Logic
        if (btnGalleryOverlay != null) {
            btnGalleryOverlay.setOnClickListener(v -> openGallery());
        }

        // Submit Button Logic -> Navigate to Result Page
        if (btnSubmitReport != null) {
            btnSubmitReport.setOnClickListener(v -> {
                Intent intent = new Intent(QualityReportActivity.this, QualityResultActivity.class);
                intent.putExtra("product_name", productName);
                intent.putExtra("product_image", productImageRes);
                
                // Pass the captured image if available
                if (ivCapturedPreview.getVisibility() == View.VISIBLE && ivCapturedPreview.getDrawable() != null) {
                    Bitmap bitmap = ((BitmapDrawable) ivCapturedPreview.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent.putExtra("captured_image", byteArray);
                }
                
                startActivity(intent);
            });
        }
        
        if (btnFlash != null) {
            btnFlash.setOnClickListener(v -> Toast.makeText(this, "Flash feature not implemented", Toast.LENGTH_SHORT).show());
        }
    }

    private void showSelectedImage(Uri uri) {
        ivCapturedPreview.setImageURI(uri);
        ivCapturedPreview.setVisibility(View.VISIBLE);
        viewFinder.setVisibility(View.GONE);
        findViewById(R.id.overlay_view).setVisibility(View.GONE);
        findViewById(R.id.scan_frame).setVisibility(View.GONE);
        findViewById(R.id.hint_pill).setVisibility(View.GONE);
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        imageCapture.takePicture(cameraExecutor, new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                Bitmap bitmap = imageToBitmap(image);
                runOnUiThread(() -> {
                    ivCapturedPreview.setImageBitmap(bitmap);
                    ivCapturedPreview.setVisibility(View.VISIBLE);
                    viewFinder.setVisibility(View.GONE);
                    findViewById(R.id.overlay_view).setVisibility(View.GONE);
                    findViewById(R.id.scan_frame).setVisibility(View.GONE);
                    findViewById(R.id.hint_pill).setVisibility(View.GONE);
                });
                image.close();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                runOnUiThread(() -> Toast.makeText(QualityReportActivity.this, "Capture failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private Bitmap imageToBitmap(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}
