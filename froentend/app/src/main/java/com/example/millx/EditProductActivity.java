package com.example.millx;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
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

public class EditProductActivity extends AppCompatActivity {

    private ImageView imgProductPreview;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String[]> permissionLauncher;

    private EditText editName, editDescription, editPrice;
    private android.widget.TextView tvHeaderTitle;
    private Product product;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        imgProductPreview = findViewById(R.id.img_product_preview);
        tvHeaderTitle = findViewById(R.id.tv_header_title);
        editName = findViewById(R.id.edit_product_name);
        editDescription = findViewById(R.id.edit_description);
        editPrice = findViewById(R.id.edit_price);

        MaterialCardView btnEditImage = findViewById(R.id.btn_edit_image);
        ImageView btnBack = findViewById(R.id.btn_back);
        MaterialButton btnSave = findViewById(R.id.btn_save);
        ImageView btnDelete = findViewById(R.id.btn_delete);

        // Receive Data
        if (getIntent().hasExtra("product")) {
            product = (Product) getIntent().getSerializableExtra("product");
            populateFields();
        }

        setupLaunchers();

        if (btnEditImage != null) {
            btnEditImage.setOnClickListener(v -> showImageSourceDialog());
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> confirmDelete());
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveProduct());
        }
    }

    private void populateFields() {
        if (product == null)
            return;

        if (tvHeaderTitle != null) {
            tvHeaderTitle.setText("Edit " + product.getName());
        }

        editName.setText(product.getName());
        editDescription.setText(product.getDescription());
        editPrice.setText(String.valueOf(product.getPrice()));

        if (product.getImage() != null && !product.getImage().isEmpty()) {
            String fullUrl = ApiClient.BASE_URL + product.getImage().replace("../", "");
            Glide.with(this)
                    .load(fullUrl)
                    .placeholder(R.drawable.ic_wheat_rate)
                    .error(R.drawable.ic_wheat_rate)
                    .into(imgProductPreview);
        }
    }

    private void saveProduct() {
        String name = editName.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String priceStr = editPrice.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Name and Price are required", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = product != null ? product.getId() : -1;
        if (id == -1)
            return;

        // Prepare Request
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(id));
        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody pricePart = RequestBody.create(MediaType.parse("text/plain"), priceStr);
        // Default unit to what it was or "kg"
        String unitVal = (product.getUnit() != null) ? product.getUnit() : "kg";
        RequestBody unitPart = RequestBody.create(MediaType.parse("text/plain"), unitVal);
        RequestBody descPart = RequestBody.create(MediaType.parse("text/plain"), description);

        MultipartBody.Part imagePart = null;
        if (photoFile != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), photoFile);
            imagePart = MultipartBody.Part.createFormData("image", photoFile.getName(), requestFile);
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.updateProduct(idPart, namePart, pricePart, unitPart, descPart, imagePart)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(EditProductActivity.this, "Product updated", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(EditProductActivity.this, "Update failed: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(EditProductActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton("Delete", (dialog, which) -> deleteProduct())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteProduct() {
        int id = product != null ? product.getId() : -1;
        if (id == -1)
            return;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.deleteProduct(new ProductRequest(id)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditProductActivity.this, "Product deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditProductActivity.this, "Delete failed: " + response.code(), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EditProductActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                        imgProductPreview.setImageBitmap(imageBitmap);
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
                            imgProductPreview.setImageBitmap(bitmap);
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

    private void showImageSourceDialog() {
        String[] options = { "Camera", "Gallery" };
        new AlertDialog.Builder(this)
                .setTitle("Select Image Source")
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
        File imageFile = new File(filesDir, "product_upload_" + System.currentTimeMillis() + ".jpg");
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
            File imageFile = new File(filesDir, "product_upload_" + System.currentTimeMillis() + ".jpg");
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
