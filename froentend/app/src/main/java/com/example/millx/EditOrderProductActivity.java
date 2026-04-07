package com.example.millx;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditOrderProductActivity extends AppCompatActivity {

    private EditText etProductName, etPricePerKg, etDescription;
    private android.widget.TextView tvProductUnit;
    private ImageView imgProduct, btnEditImage;
    private double price = 0.0;
    private int productId = -1;
    private Uri imageUri;

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imageUri = uri;
                    imgProduct.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order_product);

        initViews();
        setupListeners();
        loadDataFromIntent();
    }

    private void initViews() {
        etProductName = findViewById(R.id.et_product_name);
        etDescription = findViewById(R.id.et_product_description);
        etPricePerKg = findViewById(R.id.et_price_per_kg);
        tvProductUnit = findViewById(R.id.tv_product_unit);
        imgProduct = findViewById(R.id.img_product);
        btnEditImage = findViewById(R.id.btn_edit_image);
    }

    private void setupListeners() {
        if (findViewById(R.id.btn_back) != null) {
            findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        }

/*
        findViewById(R.id.btn_remove).setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Remove Product")
                    .setMessage("Are you sure you want to permanently delete this product?")
                    .setPositiveButton("Remove", (dialog, which) -> deleteProduct())
                    .setNegativeButton("Cancel", null)
                    .show();
        });
*/

        findViewById(R.id.btn_save).setOnClickListener(v -> saveChanges());

        btnEditImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        findViewById(R.id.btn_unit_selection).setOnClickListener(v -> showUnitSelectionDialog());

        etPricePerKg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    price = Double.parseDouble(s.toString());
                } catch (NumberFormatException e) {
                    price = 0.0;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void loadDataFromIntent() {
        Intent intent = getIntent();
        productId = intent.getIntExtra("product_id", -1);
        String name = intent.getStringExtra("product_name");
        String subtitle = intent.getStringExtra("product_subtitle"); // Using subtitle as description
        price = intent.getDoubleExtra("product_price", 0.0);
        int imageResId = intent.getIntExtra("product_image", 0);

        if (name != null)
            etProductName.setText(name);
        if (subtitle != null)
            etDescription.setText(subtitle);
        etPricePerKg.setText(String.format(Locale.getDefault(), "%.2f", price));
        
        String unit = intent.getStringExtra("product_unit");
        if (unit != null) {
            tvProductUnit.setText(unit);
        } else {
            tvProductUnit.setText("Kg"); // Default
        }

        if (imageResId != 0) {
            imgProduct.setImageResource(imageResId);
        } else {
            String imageUrl = intent.getStringExtra("product_image_url");
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Determine full URL if path is relative
                String fullUrl = imageUrl.startsWith("http") ? imageUrl
                        : ApiClient.BASE_URL + imageUrl.replace("../", "");
                Glide.with(this)
                        .load(fullUrl)
                        .placeholder(R.drawable.ic_wheat_rate) // Fallback
                        .error(R.drawable.ic_wheat_rate)
                        .into(imgProduct);
            }
        }
    }

    private void saveChanges() {
        if (productId == -1) {
            Toast.makeText(this, "Error: Invalid Product ID", Toast.LENGTH_SHORT).show();
            return;
        }

        String newName = etProductName.getText().toString();
        String newDescription = etDescription.getText().toString();
        String newUnit = tvProductUnit.getText().toString();

        updateProductInDb(newName, newDescription, price, newUnit);
    }

    private void showUnitSelectionDialog() {
        String[] units = { "Kg", "Bags" };
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Select Unit");
        builder.setItems(units, (dialog, which) -> {
            tvProductUnit.setText(units[which]);
        });
        builder.show();
    }

    private void updateProductInDb(String name, String description, double price, String unit) {
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Saving changes...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(productId));
        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody pricePart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(price));
        RequestBody unitPart = RequestBody.create(MediaType.parse("text/plain"), unit);
        RequestBody descPart = RequestBody.create(MediaType.parse("text/plain"), description);

        MultipartBody.Part imagePart = null;
        if (imageUri != null) {
            File file = getFileFromUri(imageUri);
            if (file != null) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
                // PHP script expects 'image' key in $_FILES
                imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
            }
        }

        Call<ResponseBody> call = apiService.updateProduct(idPart, namePart, pricePart, unitPart, descPart, imagePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        org.json.JSONObject jsonObject = new org.json.JSONObject(responseString);
                        String status = jsonObject.optString("status");
                        String message = jsonObject.optString("message");

                        if ("success".equalsIgnoreCase(status)) {
                            Toast.makeText(EditOrderProductActivity.this, "Success: " + message, Toast.LENGTH_SHORT)
                                    .show();
                            returnResult(name, description, price);
                        } else {
                            // Backend reported an error (like upload failed)
                            Toast.makeText(EditOrderProductActivity.this, "Error: " + message, Toast.LENGTH_LONG)
                                    .show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(EditOrderProductActivity.this, "Response Parsing Error", Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(EditOrderProductActivity.this, "Request failed: " + response.message(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(EditOrderProductActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void deleteProduct() {
        if (productId == -1) {
            Toast.makeText(this, "Error: Invalid Product ID", Toast.LENGTH_SHORT).show();
            return;
        }

        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Removing product...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        ProductRequest request = new ProductRequest(productId);

        apiService.deleteProduct(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(EditOrderProductActivity.this, "Product removed successfully", Toast.LENGTH_SHORT)
                            .show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditOrderProductActivity.this, "Failed to remove: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(EditOrderProductActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void returnResult(String name, String description, double price) {
        setResult(RESULT_OK);
        finish();
    }

    private File getFileFromUri(Uri uri) {
        try {
            android.content.ContentResolver contentResolver = getContentResolver();
            android.webkit.MimeTypeMap mimeTypeMap = android.webkit.MimeTypeMap.getSingleton();
            String extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
            if (extension == null || extension.isEmpty())
                extension = "jpg"; // Default

            // Read Bitmap
            InputStream inputStream = contentResolver.openInputStream(uri);
            android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            if (bitmap == null)
                return null;

            // Resize if too large (e.g., > 1024px width)
            int maxWidth = 1024;
            if (bitmap.getWidth() > maxWidth) {
                int newHeight = (int) (bitmap.getHeight() * ((double) maxWidth / bitmap.getWidth()));
                bitmap = android.graphics.Bitmap.createScaledBitmap(bitmap, maxWidth, newHeight, true);
            }

            File file = new File(getCacheDir(), "compressed_image_" + System.currentTimeMillis() + "." + extension);
            OutputStream outputStream = new FileOutputStream(file);

            // Compress to JPEG with 80% quality
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, outputStream);

            outputStream.flush();
            outputStream.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
