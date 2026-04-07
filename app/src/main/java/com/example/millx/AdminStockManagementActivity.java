package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.View;

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

public class AdminStockManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StockAdapter adapter;
    private List<Stock> stockList;
    private FloatingActionButton fabAddStock;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_stock_management);

        recyclerView = findViewById(R.id.recycler_view_stocks);
        fabAddStock = findViewById(R.id.fab_add_stock);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        View btnBack = findViewById(R.id.btn_back);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        stockList = new ArrayList<>();
        adapter = new StockAdapter(this, stockList, stock -> {
            Intent intent = new Intent(AdminStockManagementActivity.this, EditStockItemActivity.class);
            intent.putExtra("stock", stock);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this::fetchStocks);
        }

        fabAddStock.setOnClickListener(v -> {
            Intent intent = new Intent(AdminStockManagementActivity.this, AddStockItemActivity.class);
            startActivity(intent);
        });

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        fetchStocks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchStocks();
    }

    private void fetchStocks() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAdminStocks().enqueue(new Callback<List<Stock>>() {
            @Override
            public void onResponse(Call<List<Stock>> call, Response<List<Stock>> response) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    stockList = response.body();
                    adapter.updateList(stockList);
                } else {
                    Toast.makeText(AdminStockManagementActivity.this, "Failed to load stocks", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<List<Stock>> call, Throwable t) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                Toast.makeText(AdminStockManagementActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}
