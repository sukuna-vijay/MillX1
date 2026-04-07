package com.example.millx;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewProductActivity extends AppCompatActivity {

    private ImageView imgPreview;
    private LinearLayout uploadPlaceholder;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String[]> permissionLauncher;

    private EditText editName, editPrice, editDescription, editUnit;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);

        imgPreview = findViewById(R.id.img_product_preview);
        uploadPlaceholder = findViewById(R.id.upload_placeholder);

        editName = findViewById(R.id.edit_product_name);
        editPrice = findViewById(R.id.edit_price);
        editUnit = findViewById(R.id.edit_unit);
        editDescription = findViewById(R.id.edit_description);
        // editStock is present in XML but not in DB currently. Ignoring.

        MaterialCardView btnUpload = findViewById(R.id.btn_upload_image);
        ImageView btnBack = findViewById(R.id.btn_back);
        MaterialButton btnAddProduct = findViewById(R.id.btn_add_product);

        setupLaunchers();

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnUpload != null) {
            btnUpload.setOnClickListener(v -> showImageSourceDialog());
        }

        if (btnAddProduct != null) {
            btnAddProduct.setOnClickListener(v -> addProduct());
        }
    }

    private void addProduct() {
        String name = editName.getText().toString().trim();
        String priceStr = editPrice.getText().toString().trim();
        String netWeight = editUnit.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty() || netWeight.isEmpty()) {
            Toast.makeText(this, "Name, Price, and Net Weight are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare Request
        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody pricePart = RequestBody.create(MediaType.parse("text/plain"), priceStr);
        RequestBody unitPart = RequestBody.create(MediaType.parse("text/plain"), netWeight); // Mapped to Net Weight
        RequestBody descPart = RequestBody.create(MediaType.parse("text/plain"), description);

        MultipartBody.Part imagePart = null;
        if (photoFile != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), photoFile);
            imagePart = MultipartBody.Part.createFormData("image", photoFile.getName(), requestFile);
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.addProduct(namePart, pricePart, unitPart, descPart, imagePart)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AddNewProductActivity.this, "Product added successfully", Toast.LENGTH_SHORT)
                                    .show();
                            finish();
                        } else {
                            Toast.makeText(AddNewProductActivity.this, "Failed to add product: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(AddNewProductActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    private void setupLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        showImage(imageBitmap);
                        photoFile = saveBitmapToFile(imageBitmap);
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
                            photoFile = saveUriToFile(selectedImageUri);
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
                        if (!granted)
                            allGranted = false;
                    }
                    if (allGranted)
                        showImageSourceDialog();
                    else
                        Toast.makeText(this, "Permissions required", Toast.LENGTH_SHORT).show();
                });
    }

    private void showImage(Bitmap bitmap) {
        imgPreview.setImageBitmap(bitmap);
        imgPreview.setVisibility(View.VISIBLE);
        uploadPlaceholder.setVisibility(View.GONE);
    }

    private void showImageSourceDialog() {
        String[] options = { "Camera", "Gallery" };
        new AlertDialog.Builder(this)
                .setTitle("Select Product Image")
                .setItems(options, (dialog, which) -> {
                    if (which == 0)
                        checkCameraPermissionAndOpen();
                    else
                        openGallery();
                })
                .show();
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

    private File saveBitmapToFile(Bitmap bitmap) {
        File filesDir = getFilesDir();
        File imageFile = new File(filesDir, "product_new_" + System.currentTimeMillis() + ".jpg");
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private File saveUriToFile(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File filesDir = getFilesDir();
            File imageFile = new File(filesDir, "product_new_" + System.currentTimeMillis() + ".jpg");
            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) > 0)
                    fos.write(buffer, 0, len);
                return imageFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
