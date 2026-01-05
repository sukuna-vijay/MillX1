package com.example.millx;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;

public class AdminMenuActivity extends AppCompatActivity {

    private ShapeableImageView profileImg;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String[]> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        profileImg = findViewById(R.id.profile_image);
        MaterialCardView btnChangePic = findViewById(R.id.btn_change_profile_pic);
        
        setupLaunchers();

        if (btnChangePic != null) {
            btnChangePic.setOnClickListener(v -> showImageSourceDialog());
        }

        MaterialCardView menuHome = findViewById(R.id.menu_home);
        if (menuHome != null) {
            menuHome.setOnClickListener(v -> finish());
        }

        MaterialCardView menuMachine = findViewById(R.id.menu_machine);
        if (menuMachine != null) {
            menuMachine.setOnClickListener(v -> {
                Intent intent = new Intent(AdminMenuActivity.this, AdminMachineManagementActivity.class);
                startActivity(intent);
            });
        }

        MaterialCardView menuStock = findViewById(R.id.menu_stock);
        if (menuStock != null) {
            menuStock.setOnClickListener(v -> {
                Intent intent = new Intent(AdminMenuActivity.this, AdminStockManagementActivity.class);
                startActivity(intent);
            });
        }

        MaterialCardView menuPrice = findViewById(R.id.menu_price);
        if (menuPrice != null) {
            menuPrice.setOnClickListener(v -> {
                Intent intent = new Intent(AdminMenuActivity.this, AdminPriceManagementActivity.class);
                startActivity(intent);
            });
        }

        MaterialCardView menuOrders = findViewById(R.id.menu_orders);
        if (menuOrders != null) {
            menuOrders.setOnClickListener(v -> {
                Intent intent = new Intent(AdminMenuActivity.this, CustomerProductOrdersActivity.class);
                startActivity(intent);
            });
        }

        MaterialCardView menuFeedback = findViewById(R.id.menu_feedback);
        if (menuFeedback != null) {
            menuFeedback.setOnClickListener(v -> {
                Intent intent = new Intent(AdminMenuActivity.this, UserFeedbackActivity.class);
                startActivity(intent);
            });
        }

        MaterialCardView menuLogout = findViewById(R.id.menu_logout);
        if (menuLogout != null) {
            menuLogout.setOnClickListener(v -> {
                SharedPreferences sharedPreferences = getSharedPreferences("MillXPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(AdminMenuActivity.this, RoleSelectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
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
                        profileImg.setImageBitmap(imageBitmap);
                    }
                }
        );

        // Gallery Result
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                            profileImg.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

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
                        Toast.makeText(this, "Permissions are required to change profile picture", Toast.LENGTH_SHORT).show();
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
