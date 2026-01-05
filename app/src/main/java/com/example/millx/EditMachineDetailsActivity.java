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

public class EditMachineDetailsActivity extends AppCompatActivity {

    private ImageView machineImgPreview;
    private TextView tvCurrentStatus;
    private View statusDot;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String[]> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_machine_details);

        machineImgPreview = findViewById(R.id.machine_img_preview);
        tvCurrentStatus = findViewById(R.id.tv_current_status);
        statusDot = findViewById(R.id.status_dot);
        
        MaterialCardView btnChangePhoto = findViewById(R.id.btn_change_photo);
        View btnStatusDropdown = findViewById(R.id.btn_status_dropdown);
        MaterialButton btnSave = findViewById(R.id.btn_save);
        ImageView btnBack = findViewById(R.id.btn_back);
        TextView btnCancel = findViewById(R.id.btn_cancel);

        setupLaunchers();

        if (btnChangePhoto != null) {
            btnChangePhoto.setOnClickListener(v -> showImageSourceDialog());
        }

        if (btnStatusDropdown != null) {
            btnStatusDropdown.setOnClickListener(v -> showStatusSelectionDialog());
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> finish());
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    private void showStatusSelectionDialog() {
        String[] options = {"Available", "Not Available"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Machine Status");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                tvCurrentStatus.setText("Available");
                statusDot.setBackgroundResource(R.drawable.circle_green);
            } else {
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
                        machineImgPreview.setImageBitmap(imageBitmap);
                    }
                }
        );

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            machineImgPreview.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

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
                        Toast.makeText(this, "Permissions required to change machine photo", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void showImageSourceDialog() {
        String[] options = {"Camera", "Gallery"};
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
            permissionLauncher.launch(new String[]{Manifest.permission.CAMERA});
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
