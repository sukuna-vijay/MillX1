package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersActivity extends AppCompatActivity implements OrderAdapter.OnOrderCancelListener {

    private RecyclerView recyclerView;
    private OrderAdapter adapter;
    private ApiService apiService;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        recyclerView = findViewById(R.id.recycler_orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        swipeRefresh = findViewById(R.id.swipe_refresh);
        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this::fetchOrders);
        }

        setupRetrofit();
        fetchOrders();
    }

    private void setupRetrofit() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    private void fetchOrders() {
        apiService.getOrderStatus().enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        adapter = new OrderAdapter(MyOrdersActivity.this, response.body().getOrders(), MyOrdersActivity.this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        String msg = response.body().getMessage();
                        Toast.makeText(MyOrdersActivity.this, msg != null ? msg : "No orders found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MyOrdersActivity.this, "Failed to load orders: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                Toast.makeText(MyOrdersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCancelClick(Order order) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cancel Order")
                .setMessage("Are you sure you want to cancel this order?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    performCancellation(order.getOrderId());
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void performCancellation(int orderId) {
        java.util.Map<String, Integer> body = new java.util.HashMap<>();
        body.put("order_id", orderId);

        apiService.cancelOrder(body).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MyOrdersActivity.this, "Order cancelled successfully", Toast.LENGTH_SHORT).show();
                    fetchOrders(); // Refresh list
                } else {
                    Toast.makeText(MyOrdersActivity.this, "Failed to cancel order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                Toast.makeText(MyOrdersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
