package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class CustomerProductOrdersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminOrderAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvError;
    private EditText etSearch;
    private ApiService apiService;
    private List<AdminOrder> fullOrderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_product_orders);

        apiService = ApiClient.getClient().create(ApiService.class);

        initViews();
        fetchOrders();
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        recyclerView = findViewById(R.id.rv_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminOrderAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        swipeRefresh = findViewById(R.id.swipe_refresh);
        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this::fetchOrders);
        }

        progressBar = findViewById(R.id.progress_bar);
        tvError = findViewById(R.id.tv_error);
        etSearch = findViewById(R.id.et_search);

        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filterOrders(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        FloatingActionButton fabAddOrder = findViewById(R.id.fab_add_order);
        if (fabAddOrder != null) {
            fabAddOrder.setOnClickListener(v -> {
                Intent intent = new Intent(CustomerProductOrdersActivity.this, ManageCustomerOrdersActivity.class);
                startActivity(intent);
            });
        }
    }

    private void fetchOrders() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (tvError != null) tvError.setVisibility(View.GONE);

        apiService.getAdminOrders().enqueue(new Callback<AdminOrderResponse>() {
            @Override
            public void onResponse(Call<AdminOrderResponse> call, Response<AdminOrderResponse> response) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    fullOrderList = response.body().getOrders();
                    adapter.updateList(fullOrderList);
                } else {
                    if (tvError != null) tvError.setVisibility(View.VISIBLE);
                    Toast.makeText(CustomerProductOrdersActivity.this, "Failed to load orders", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminOrderResponse> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if (tvError != null) tvError.setVisibility(View.VISIBLE);
                Toast.makeText(CustomerProductOrdersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterOrders(String query) {
        List<AdminOrder> filtered = new ArrayList<>();
        for (AdminOrder order : fullOrderList) {
            if (order.getUserName().toLowerCase().contains(query.toLowerCase()) ||
                String.valueOf(order.getOrderId()).contains(query)) {
                filtered.add(order);
            }
        }
        adapter.updateList(filtered);
    }
}
