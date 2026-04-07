package com.example.millx;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminMenuActivity extends AppCompatActivity {

    private ShapeableImageView profileImg;
    private TextView tvActive, tvInactive;
    private SwipeRefreshLayout swipeRefresh;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        profileImg = findViewById(R.id.profile_image);
        MaterialCardView btnChangePic = findViewById(R.id.btn_change_profile_pic);
        swipeRefresh = findViewById(R.id.swipe_refresh);

        apiService = ApiClient.getClient().create(ApiService.class);

        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this::fetchDashboardData);
        }

        updateUI(); // Initial load

        if (btnChangePic != null) {
            btnChangePic.setOnClickListener(v -> {
                Intent intent = new Intent(AdminMenuActivity.this, AdminProfileActivity.class);
                startActivity(intent);
            });
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
                new SessionManager(AdminMenuActivity.this).logoutUser();

                Intent intent = new Intent(AdminMenuActivity.this, RoleSelectionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        SessionManager session = new SessionManager(this);
        String name = session.getUserName();
        String phone = session.getUserPhone();
        String image = session.getUserImage();

        TextView tvName = findViewById(R.id.tv_admin_menu_name);
        TextView tvPhone = findViewById(R.id.tv_admin_menu_phone);

        if (tvName != null)
            tvName.setText(name);
        if (tvPhone != null)
            tvPhone.setText(phone);

        if (profileImg != null && image != null && !image.isEmpty()) {
            com.bumptech.glide.Glide.with(this)
                    .load(image)
                    .placeholder(R.drawable.ic_admin_profile)
                    .error(R.drawable.ic_admin_profile)
                    .into(profileImg);
        }

        fetchDashboardData();
    }

    private void fetchDashboardData() {
        tvActive = findViewById(R.id.tv_active_machines);
        tvInactive = findViewById(R.id.tv_inactive_machines);

        apiService.getAdminDashboard().enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null
                        && "success".equals(response.body().getStatus())) {
                    DashboardResponse.DashboardData data = response.body().getData();
                    if (data != null) {
                        if (tvActive != null)
                            tvActive.setText(String.valueOf(data.getActiveMachines()));
                        if (tvInactive != null)
                            tvInactive.setText(String.valueOf(data.getInactiveMachines()));
                    }
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                Toast.makeText(AdminMenuActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
