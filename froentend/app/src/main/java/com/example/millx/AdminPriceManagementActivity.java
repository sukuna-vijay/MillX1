package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPriceManagementActivity extends AppCompatActivity implements PriceAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private PriceAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private ApiService apiService;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_price_management);

        apiService = ApiClient.getClient().create(ApiService.class);

        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        recyclerView = findViewById(R.id.recycler_view_prices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PriceAdapter(this, productList, this);
        recyclerView.setAdapter(adapter);

        swipeRefresh = findViewById(R.id.swipe_refresh);
        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this::fetchPrices);
        }

        FloatingActionButton fabAddPrice = findViewById(R.id.fab_add_price);
        if (fabAddPrice != null) {
            fabAddPrice.setOnClickListener(v -> {
                Intent intent = new Intent(AdminPriceManagementActivity.this, AddNewProductActivity.class);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchPrices();
    }

    private void fetchPrices() {
        apiService.getAdminPrices().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    productList = response.body();
                    adapter.updateList(productList);
                } else {
                    Toast.makeText(AdminPriceManagementActivity.this, "Failed to load prices", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                Toast.makeText(AdminPriceManagementActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public void onItemClick(Product product) {
        Intent intent = new Intent(this, EditProductActivity.class);
        intent.putExtra("product", product);
        startActivity(intent);
    }
}
