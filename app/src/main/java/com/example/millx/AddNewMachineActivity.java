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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.IOException;

public class AddNewMachineActivity extends AppCompatActivity {

    private ImageView imgPreview;
    private LinearLayout uploadPlaceholder;
    private TextView tvCurrentStatus;
    private View statusDot;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String[]> permissionLauncher;

    private android.widget.EditText editName, editId, editCapacity;
    private android.widget.TextView editUnit;
    private String currentStatus = "active";
    private Bitmap selectedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_machine);

        imgPreview = findViewById(R.id.img_machine_preview);
        uploadPlaceholder = findViewById(R.id.upload_placeholder);
        tvCurrentStatus = findViewById(R.id.tv_current_status);
        statusDot = findViewById(R.id.status_dot);

        editName = findViewById(R.id.edit_name);
        editId = findViewById(R.id.edit_id); // This might be Description or internal ID in future, treating as free
        editCapacity = findViewById(R.id.edit_capacity);
        editUnit = findViewById(R.id.txt_unit);

        MaterialCardView btnUpload = findViewById(R.id.btn_upload_image);
        View btnStatusDropdown = findViewById(R.id.btn_status_dropdown);
        ImageView btnBack = findViewById(R.id.btn_back);
        MaterialButton btnAddMachine = findViewById(R.id.btn_add_machine);

        setupLaunchers();

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnUpload != null) {
            btnUpload.setOnClickListener(v -> showImageSourceDialog());
        }

        if (btnStatusDropdown != null) {
            btnStatusDropdown.setOnClickListener(v -> showStatusSelectionDialog());
        }

        if (btnAddMachine != null) {
            btnAddMachine.setOnClickListener(v -> addMachine());
        }
    }

    private void addMachine() {
        String name = editName.getText().toString().trim();
        String capacityStr = editCapacity.getText().toString().trim();
        String description = editId.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Machine Name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        String min = "0";
        String max = "0";
        if (capacityStr.contains("-")) {
            String[] parts = capacityStr.split("-");
            if (parts.length > 0)
                min = parts[0].trim();
            if (parts.length > 1)
                max = parts[1].trim();
        } else {
            max = capacityStr;
        }

        String unit = "KG/HR";
        if (editUnit != null)
            unit = editUnit.getText().toString();

        // Prepare Multipart Parts
        okhttp3.RequestBody namePart = createPartFromString(name);
        okhttp3.RequestBody statusPart = createPartFromString(currentStatus);
        okhttp3.RequestBody minPart = createPartFromString(min);
        okhttp3.RequestBody maxPart = createPartFromString(max);
        okhttp3.RequestBody unitPart = createPartFromString(unit);
        okhttp3.RequestBody descPart = createPartFromString(description);

        okhttp3.MultipartBody.Part imagePart = null;
        if (selectedBitmap != null) {
            imagePart = prepareFilePart("image", selectedBitmap);
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.addMachine(namePart, statusPart, minPart, maxPart, unitPart, descPart, imagePart)
                .enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
                    @Override
                    public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call,
                            retrofit2.Response<okhttp3.ResponseBody> response) {
                        if (response.isSuccessful()) {
                            try {
                                String resp = response.body().string(); // Read simple success message
                                Toast.makeText(AddNewMachineActivity.this, "Machine added: " + resp, Toast.LENGTH_LONG)
                                        .show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            finish();
                        } else {
                            Toast.makeText(AddNewMachineActivity.this, "Failed: " + response.code(), Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                        Toast.makeText(AddNewMachineActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    @androidx.annotation.NonNull
    private okhttp3.RequestBody createPartFromString(String descriptionString) {
        return okhttp3.RequestBody.create(okhttp3.MultipartBody.FORM, descriptionString);
    }

    private okhttp3.MultipartBody.Part prepareFilePart(String partName, Bitmap bitmap) {
        try {
            java.io.File filesDir = getApplicationContext().getFilesDir();
            java.io.File file = new java.io.File(filesDir, "machine_image.jpg");

            java.io.OutputStream os = new java.io.FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, os);
            os.flush();
            os.close();

            okhttp3.RequestBody requestFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), file);
            return okhttp3.MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void showStatusSelectionDialog() {
        String[] options = { "Available", "Not Available" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Machine Status");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                currentStatus = "Running";
                tvCurrentStatus.setText("Available");
                statusDot.setBackgroundResource(R.drawable.circle_green);
            } else {
                currentStatus = "Stopped";
                tvCurrentStatus.setText("Not Available");
                statusDot.setBackgroundResource(R.drawable.circle_red);
            }
        });
        builder.show();
    }

    private void setupLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        showImage(imageBitmap);
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),
                                    selectedImageUri);
                            showImage(bitmap);
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
                        Toast.makeText(this, "Permissions required to upload machine photo", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showImage(Bitmap bitmap) {
        imgPreview.setImageBitmap(bitmap);
        imgPreview.setVisibility(View.VISIBLE);
        uploadPlaceholder.setVisibility(View.GONE);
    }

    private void showImageSourceDialog() {
        String[] options = { "Camera", "Gallery" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Machine Image");
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
