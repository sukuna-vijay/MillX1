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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.io.IOException;

import com.bumptech.glide.Glide;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.database.Cursor;

public class EditMachineDetailsActivity extends AppCompatActivity {

    private ImageView machineImgPreview;
    private TextView tvCurrentStatus;
    private View statusDot;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<String[]> permissionLauncher;

    private EditText editName, editId, editMinCapacity, editMaxCapacity, editUnit, editDescription;
    private Machine machine;
    private String currentStatus = "Running";
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_machine_details);

        machineImgPreview = findViewById(R.id.machine_img_preview);
        tvCurrentStatus = findViewById(R.id.tv_current_status);
        statusDot = findViewById(R.id.status_dot);

        editName = findViewById(R.id.edit_name);
        editId = findViewById(R.id.edit_id);
        editMinCapacity = findViewById(R.id.edit_min_capacity);
        editMaxCapacity = findViewById(R.id.edit_max_capacity);
        editUnit = findViewById(R.id.edit_unit);
        editDescription = findViewById(R.id.edit_description);

        MaterialCardView btnChangePhoto = findViewById(R.id.btn_change_photo);
        View btnStatusDropdown = findViewById(R.id.btn_status_dropdown);
        MaterialButton btnSave = findViewById(R.id.btn_save);
        ImageView btnBack = findViewById(R.id.btn_back);
        ImageView btnDelete = findViewById(R.id.btn_delete);
        TextView btnCancel = findViewById(R.id.btn_cancel);

        // Receive Data
        if (getIntent().hasExtra("machine_data")) {
            machine = (Machine) getIntent().getSerializableExtra("machine_data");
            populateFields();
        }

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

        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> confirmDelete());
        }

        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> finish());
        }

        if (btnSave != null) {
            btnSave.setOnClickListener(v -> updateMachine());
        }
    }

    private void populateFields() {
        if (machine == null)
            return;

        if (editName != null)
            editName.setText(machine.getMachineName());
        if (editId != null)
            editId.setText(String.valueOf(machine.getId())); // Or use machine.getDescription() if that's what's
                                                             // intended
        if (editMinCapacity != null)
            editMinCapacity.setText(machine.getMinCapacity());
        if (editMaxCapacity != null)
            editMaxCapacity.setText(machine.getMaxCapacity());
        if (editUnit != null)
            editUnit.setText(machine.getUnit());
        if (editDescription != null)
            editDescription.setText(machine.getDescription());

        currentStatus = machine.getMachineStatus();
        if (machine.getImage() != null && !machine.getImage().isEmpty()) {
            Glide.with(this)
                    .load(ApiClient.BASE_URL + machine.getImage())
                    .placeholder(R.drawable.ic_idly_machine) // Ensure this exists or use a default
                    .error(R.drawable.ic_idly_machine)
                    .into(machineImgPreview);
        }

        updateStatusUI();
    }

    private void updateStatusUI() {
        if ("Available".equalsIgnoreCase(currentStatus) || "Running".equalsIgnoreCase(currentStatus)
                || "active".equalsIgnoreCase(currentStatus)) {
            tvCurrentStatus.setText("Available");
            statusDot.setBackgroundResource(R.drawable.circle_green);
        } else {
            tvCurrentStatus.setText("Not Available");
            statusDot.setBackgroundResource(R.drawable.circle_red);
        }
    }

    private void showStatusSelectionDialog() {
        String[] options = { "Available", "Not Available" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Machine Status");
        builder.setItems(options, (dialog, which) -> {
            if (which == 0) {
                currentStatus = "Running";
            } else {
                currentStatus = "Stopped";
            }
            updateStatusUI();
        });
        builder.show();
    }

    private void updateMachine() {
        String name = editName.getText().toString().trim();
        String minCap = editMinCapacity.getText().toString().trim();
        String maxCap = editMaxCapacity.getText().toString().trim();
        String unit = editUnit.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        int id = machine != null ? machine.getId() : -1;
        if (id == -1) {
            Toast.makeText(this, "Invalid machine ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare RequestBody
        RequestBody idPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(id));
        RequestBody namePart = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody statusPart = RequestBody.create(MediaType.parse("text/plain"), currentStatus);
        RequestBody minPart = RequestBody.create(MediaType.parse("text/plain"), minCap);
        RequestBody maxPart = RequestBody.create(MediaType.parse("text/plain"), maxCap);
        RequestBody unitPart = RequestBody.create(MediaType.parse("text/plain"), unit);
        RequestBody descPart = RequestBody.create(MediaType.parse("text/plain"), description);

        MultipartBody.Part imagePart = null;
        if (photoFile != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), photoFile);
            imagePart = MultipartBody.Part.createFormData("image", photoFile.getName(), requestFile);
        }

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.updateMachineDetails(
                idPart, namePart, statusPart, minPart, maxPart, unitPart, descPart, imagePart)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(EditMachineDetailsActivity.this, "Machine updated successfully",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(EditMachineDetailsActivity.this, "Update failed: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(EditMachineDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT)
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
                        machineImgPreview.setImageBitmap(imageBitmap);
                        // Save bitmap to file for upload
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
                            machineImgPreview.setImageBitmap(bitmap);
                            // Save URI content to file for upload
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
        File imageFile = new File(filesDir, "machine_upload_" + System.currentTimeMillis() + ".jpg");
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
            File imageFile = new File(filesDir, "machine_upload_" + System.currentTimeMillis() + ".jpg");
            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                return imageFile;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Machine")
                .setMessage("Are you sure you want to delete this machine?")
                .setPositiveButton("Delete", (dialog, which) -> deleteMachine())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteMachine() {
        int id = machine != null ? machine.getId() : -1;
        if (id == -1)
            return;

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.deleteMachine(new MachineRequest(id)).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditMachineDetailsActivity.this, "Machine deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditMachineDetailsActivity.this, "Delete failed: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(EditMachineDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
