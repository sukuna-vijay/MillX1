package com.example.millx;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

public class ProfileActivity extends AppCompatActivity {

    private ShapeableImageView profileImg;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String[]> permissionLauncher;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefresh;

    private com.google.android.material.textfield.TextInputEditText etName;
    private android.widget.EditText etNamePlain, etEmailPlain, etPhonePlain, etAddressPlain;
    private android.widget.TextView tvHeaderName, tvHeaderPhone;
    private Uri selectedImageUri;
    private Integer userId; // Get this from SharedPrefs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Assuming User ID is saved in Shared Preferences, e.g., 1 for now
        // TODO: Replace with actual User ID retrieval logic
        userId = getSharedPreferences("MillXPrefs", MODE_PRIVATE).getInt("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        profileImg = findViewById(R.id.profile_img);
        MaterialCardView btnChangePic = findViewById(R.id.btn_change_profile_pic);
        MaterialButton btnSubmit = findViewById(R.id.btn_submit);

        tvHeaderName = findViewById(R.id.tv_header_name);
        tvHeaderPhone = findViewById(R.id.tv_header_phone);

        etNamePlain = findViewById(R.id.et_name);
        etEmailPlain = findViewById(R.id.et_email);
        etPhonePlain = findViewById(R.id.et_phone);
        etAddressPlain = findViewById(R.id.et_address);

        swipeRefresh = findViewById(R.id.swipe_refresh);

        setupLaunchers();
        fetchProfileData();

        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this::fetchProfileData);
            // Optionally customize colors
            swipeRefresh.setColorSchemeColors(android.graphics.Color.parseColor("#4F7E1C"));
        }

        if (btnChangePic != null) {
            btnChangePic.setOnClickListener(v -> showImageSourceDialog());
        }

        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> updateProfile());
        }
    }

    private void fetchProfileData() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getUserProfile(userId).enqueue(new retrofit2.Callback<ProfileResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ProfileResponse> call, retrofit2.Response<ProfileResponse> response) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().status)) {
                    ProfileResponse.UserData data = response.body().data;

                    tvHeaderName.setText(data.name);
                    tvHeaderPhone.setText(data.phone);

                    etNamePlain.setText(data.name);
                    etEmailPlain.setText(data.email);
                    etPhonePlain.setText(data.phone);
                    etAddressPlain.setText(data.address);

                    if (data.profileImage != null && !data.profileImage.isEmpty()) {
                        String imageUrl = ApiClient.BASE_URL + data.profileImage;
                        com.bumptech.glide.Glide.with(ProfileActivity.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_user_profile)
                                .error(R.drawable.ic_user_profile)
                                .into(profileImg);
                    } else {
                        profileImg.setImageResource(R.drawable.ic_user_profile);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ProfileResponse> call, Throwable t) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String name = etNamePlain.getText().toString().trim();
        String phone = etPhonePlain.getText().toString().trim();
        String address = etAddressPlain.getText().toString().trim();

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
        okhttp3.RequestBody tempAddress = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), address);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.updateUserProfile(tempUserId, tempName, tempPhone, tempAddress, imagePart)
                .enqueue(new retrofit2.Callback<ProfileResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<ProfileResponse> call,
                            retrofit2.Response<ProfileResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(ProfileActivity.this, response.body().message, Toast.LENGTH_SHORT).show();
                            if ("success".equals(response.body().status) && response.body().data != null) {
                                // Update UI immediately with new data
                                ProfileResponse.UserData data = response.body().data;

                                tvHeaderName.setText(data.name);
                                tvHeaderPhone.setText(data.phone);

                                etNamePlain.setText(data.name);
                                etPhonePlain.setText(data.phone);
                                etAddressPlain.setText(data.address);

                                // Update image if available
                                if (data.profileImage != null && !data.profileImage.isEmpty()) {
                                    String imageUrl = ApiClient.BASE_URL + data.profileImage;
                                    com.bumptech.glide.Glide.with(ProfileActivity.this)
                                            .load(imageUrl)
                                            .placeholder(R.drawable.ic_user_profile)
                                            .error(R.drawable.ic_user_profile)
                                            .into(profileImg);
                                }

                                // Update Session Name, Phone, Image so Home/Menu screen reflects it
                                new SessionManager(ProfileActivity.this).updateSession(data.name, data.phone,
                                        data.profileImage);
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ProfileResponse> call, Throwable t) {
                        Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private java.io.File getFileFromUri(Uri uri) {
        try {
            java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null)
                return null;

            java.io.File tempFile = new java.io.File(getCacheDir(),
                    "temp_upload_image_" + System.currentTimeMillis() + ".jpg");
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
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        profileImg.setImageBitmap(imageBitmap);
                        // Convert Bitmap to Uri or File if needed for upload
                        selectedImageUri = getImageUri(imageBitmap);
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                                    selectedImageUri);
                            profileImg.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

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
                        Toast.makeText(this, "Permissions required to change profile picture", Toast.LENGTH_SHORT)
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
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }
}
