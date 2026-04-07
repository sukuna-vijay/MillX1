package com.example.millx;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;

public class AdminProfileActivity extends AppCompatActivity {

    private ShapeableImageView imgProfile;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String[]> permissionLauncher;

    private android.widget.EditText etName, etEmail, etPhone;
    private android.widget.TextView tvHeaderName, tvHeaderPhone;
    private Integer userId;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        // Get User ID from Session
        userId = new SessionManager(this).getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Session Expired", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        imgProfile = findViewById(R.id.img_profile);
        tvHeaderName = findViewById(R.id.admin_name);
        tvHeaderPhone = findViewById(R.id.header_admin_phone);

        etName = findViewById(R.id.et_admin_name);
        etEmail = findViewById(R.id.et_admin_email);
        etPhone = findViewById(R.id.et_admin_phone);

        MaterialCardView btnChangePic = findViewById(R.id.btn_change_profile_pic);
        MaterialButton btnSave = findViewById(R.id.btn_save);

        setupLaunchers();
        fetchProfileData();

        if (btnChangePic != null) {
            btnChangePic.setOnClickListener(v -> showImageSourceDialog());
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> updateProfile());
        }
    }

    private void fetchProfileData() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getUserProfile(userId).enqueue(new retrofit2.Callback<ProfileResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ProfileResponse> call, retrofit2.Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().status)) {
                    ProfileResponse.UserData data = response.body().data;

                    if (tvHeaderName != null)
                        tvHeaderName.setText(data.name);
                    if (tvHeaderPhone != null)
                        tvHeaderPhone.setText(data.phone);

                    if (etName != null)
                        etName.setText(data.name);
                    if (etEmail != null)
                        etEmail.setText(data.email);
                    if (etPhone != null)
                        etPhone.setText(data.phone);

                    if (data.profileImage != null && !data.profileImage.isEmpty()) {
                        String imageUrl = ApiClient.BASE_URL + data.profileImage;
                        com.bumptech.glide.Glide.with(AdminProfileActivity.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_admin_profile)
                                .error(R.drawable.ic_admin_profile)
                                .into(imgProfile);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(AdminProfileActivity.this, "Error loading profile: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Name and Phone are required", Toast.LENGTH_SHORT).show();
            return;
        }

        okhttp3.MultipartBody.Part imagePart = null;
        if (selectedImageUri != null) {
            java.io.File file = getFileFromUri(selectedImageUri);
            if (file != null) {
                okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/*"), file);
                imagePart = okhttp3.MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            }
        }

        okhttp3.RequestBody tempUserId = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"),
                String.valueOf(userId));
        okhttp3.RequestBody tempName = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), name);
        okhttp3.RequestBody tempPhone = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), phone);
        // Address is not in Admin layout, sending empty
        okhttp3.RequestBody tempAddress = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.updateUserProfile(tempUserId, tempName, tempPhone, tempAddress, imagePart)
                .enqueue(new retrofit2.Callback<ProfileResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<ProfileResponse> call,
                            retrofit2.Response<ProfileResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(AdminProfileActivity.this, response.body().message, Toast.LENGTH_SHORT)
                                    .show();
                            if ("success".equals(response.body().status)) {
                                ProfileResponse.UserData data = response.body().data;

                                // Update UI
                                tvHeaderName.setText(data.name);
                                tvHeaderPhone.setText(data.phone);

                                if (data.profileImage != null && !data.profileImage.isEmpty()) {
                                    String imageUrl = ApiClient.BASE_URL + data.profileImage;
                                    com.bumptech.glide.Glide.with(AdminProfileActivity.this)
                                            .load(imageUrl)
                                            .placeholder(R.drawable.ic_admin_profile)
                                            .error(R.drawable.ic_admin_profile)
                                            .into(imgProfile);
                                }

                                // Update Session
                                new SessionManager(AdminProfileActivity.this).updateSession(data.name, data.phone,
                                        data.profileImage);

                                // Navigate to AdminMain
                                Intent intent = new Intent(AdminProfileActivity.this, AdminMainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(AdminProfileActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ProfileResponse> call, Throwable t) {
                        Toast.makeText(AdminProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private java.io.File getFileFromUri(Uri uri) {
        try {
            java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null)
                return null;

            java.io.File tempFile = new java.io.File(getCacheDir(),
                    "admin_temp_img_" + System.currentTimeMillis() + ".jpg");
            java.io.FileOutputStream outputStream = new java.io.FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setupLaunchers() {
        // Camera Result
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        imgProfile.setImageBitmap(imageBitmap);
                        selectedImageUri = getImageUri(imageBitmap);
                    }
                });

        // Gallery Result
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            com.bumptech.glide.Glide.with(this)
                                    .load(selectedImageUri)
                                    .placeholder(R.drawable.ic_admin_profile)
                                    .error(R.drawable.ic_admin_profile)
                                    .into(imgProfile);
                        }
                    }
                });

        // Permission Result
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean allGranted = true;
                    for (Boolean granted : result.values()) {
                        if (!granted) {
                            allGranted = false;
                            break;
                        }
                    }
                    if (allGranted) {
                        showImageSourceDialog();
                    } else {
                        Toast.makeText(this, "Permissions are required to change profile picture", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private Uri getImageUri(Bitmap inImage) {
        java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void showImageSourceDialog() {
        String[] options = { "Camera", "Gallery" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                checkCameraPermissionAndOpen();
            } else {
                openGallery();
            }
        });
        builder.show();
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(new String[] { Manifest.permission.CAMERA });
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        galleryLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }
}
