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
import android.widget.TextView;
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

public class EditStockItemActivity extends AppCompatActivity {

    private ImageView imgPreview;
    private TextView tvUnit;
    private EditText editName, editQty;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String[]> permissionLauncher;
    private Stock stock;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stock_item);

        imgPreview = findViewById(R.id.img_stock_preview);
        tvUnit = findViewById(R.id.tv_unit);
        editName = findViewById(R.id.edit_stock_name);
        editQty = findViewById(R.id.edit_stock_qty);

        MaterialCardView btnEditImage = findViewById(R.id.btn_edit_image);
        View btnBack = findViewById(R.id.btn_back);
        MaterialButton btnSave = findViewById(R.id.btn_save);
        View btnUnitDropdown = findViewById(R.id.btn_unit_dropdown);
        View btnDelete = findViewById(R.id.btn_delete);

        if (getIntent().hasExtra("stock")) {
            stock = (Stock) getIntent().getSerializableExtra("stock");
            populateFields();
        }

        setupLaunchers();

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnEditImage != null) {
            btnEditImage.setOnClickListener(v -> showImageSourceDialog());
        }

        if (btnUnitDropdown != null) {
            btnUnitDropdown.setOnClickListener(v -> showUnitSelectionDialog());
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveChanges());
        }

        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> confirmDelete());
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Stock")
                .setMessage("Are you sure you want to delete this stock item?")
                .setPositiveButton("Delete", (dialog, which) -> deleteStock())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteStock() {
        if (stock == null)
            return;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        StockRequest request = new StockRequest(stock.getId());
        apiService.deleteStock(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditStockItemActivity.this, "Stock Deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditStockItemActivity.this, "Delete Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EditStockItemActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFields() {
        if (stock == null)
            return;

        editName.setText(stock.getName());
        editQty.setText(String.valueOf(stock.getQuantity()));
        tvUnit.setText(stock.getUnit());

        if (stock.getImage() != null && !stock.getImage().isEmpty()) {
            String fullUrl = ApiClient.BASE_URL + stock.getImage().replace("../", "");
            Glide.with(this)
                    .load(fullUrl)
                    .placeholder(R.drawable.ic_wheat_rate) // Fallback
                    .error(R.drawable.ic_wheat_rate)
                    .into(imgPreview);
        }
    }

    private void saveChanges() {
        if (stock == null)
            return;

        String name = editName.getText().toString().trim();
        String qtyStr = editQty.getText().toString().trim();
        String unit = tvUnit.getText().toString().trim();

        if (name.isEmpty() || qtyStr.isEmpty()) {
            Toast.makeText(this, "Name and Quantity are required", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(stock.getId()));
        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody qtyPart = RequestBody.create(MediaType.parse("text/plain"), qtyStr);
        RequestBody unitPart = RequestBody.create(MediaType.parse("text/plain"), unit);

        MultipartBody.Part imagePart = null;
        if (photoFile != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), photoFile);
            imagePart = MultipartBody.Part.createFormData("image", photoFile.getName(), requestFile);
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.updateStock(idPart, namePart, qtyPart, unitPart, imagePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditStockItemActivity.this, "Stock Updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditStockItemActivity.this, "Update Failed: " + response.code(), Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EditStockItemActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUnitSelectionDialog() {
        String[] units = { "Kg", "Bags" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Unit");
        builder.setItems(units, (dialog, which) -> {
            tvUnit.setText(units[which]);
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
                        imgPreview.setImageBitmap(imageBitmap);
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
                            imgPreview.setImageBitmap(bitmap);
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
                        if (!granted) {
                            allGranted = false;
                            break;
                        }
                    }
                    if (allGranted) {
                        showImageSourceDialog();
                    } else {
                        Toast.makeText(this, "Permissions required", Toast.LENGTH_SHORT).show();
                    }
                });
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

    private File saveBitmapToFile(Bitmap bitmap) {
        File filesDir = getFilesDir();
        File imageFile = new File(filesDir, "stock_update_" + System.currentTimeMillis() + ".jpg");
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
            File imageFile = new File(filesDir, "stock_update_" + System.currentTimeMillis() + ".jpg");
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
