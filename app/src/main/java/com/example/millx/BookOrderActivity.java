package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookOrderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> cachedProductList;
    private ApiService apiService;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_order);

        SessionManager sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        recyclerView = findViewById(R.id.recycler_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MaterialCardView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        MaterialButton btnBookNow = findViewById(R.id.btn_book_now);
        if (btnBookNow != null) {
            btnBookNow.setOnClickListener(v -> bookOrders());
        }

        setupRetrofit();
        fetchProducts();
    }

    private void setupRetrofit() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    private void fetchProducts() {
        apiService.getPrices().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cachedProductList = response.body();
                    adapter = new ProductAdapter(BookOrderActivity.this, cachedProductList);
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(BookOrderActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(BookOrderActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bookOrders() {
        if (adapter == null || cachedProductList == null)
            return;

        Map<Integer, Integer> quantities = adapter.getSelectedQuantities();
        if (quantities.isEmpty()) {
            Toast.makeText(this, "Please select at least one product", Toast.LENGTH_SHORT).show();
            return;
        }

        int totalOrdersToPlace = 0;
        for (int qty : quantities.values()) {
            if (qty > 0) totalOrdersToPlace++;
        }

        if (totalOrdersToPlace == 0) {
            Toast.makeText(this, "Please select at least one product", Toast.LENGTH_SHORT).show();
            return;
        }

        final int finalTotal = totalOrdersToPlace;
        final java.util.concurrent.atomic.AtomicInteger completedOrders = new java.util.concurrent.atomic.AtomicInteger(0);

        for (Map.Entry<Integer, Integer> entry : quantities.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();

            if (quantity > 0) {
                // Find product to get price
                double price = 0;
                for (Product p : cachedProductList) {
                    if (p.getId() == productId) {
                        price = p.getPrice();
                        break;
                    }
                }
                double totalPrice = price * quantity;

                OrderRequest request = new OrderRequest(productId, quantity, totalPrice);
                apiService.createOrder(request).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (completedOrders.incrementAndGet() == finalTotal) {
                            finalizeOrders();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (completedOrders.incrementAndGet() == finalTotal) {
                            finalizeOrders();
                        }
                    }
                });
            }
        }
    }

    private void finalizeOrders() {
        ArrayList<String> orderSummary = new ArrayList<>();
        int totalItems = 0;
        Map<Integer, Integer> quantities = adapter.getSelectedQuantities();

        for (Map.Entry<Integer, Integer> entry : quantities.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();
            if (quantity > 0) {
                String productName = "Product";
                for (Product p : cachedProductList) {
                    if (p.getId() == productId) {
                        productName = p.getName();
                        break;
                    }
                }
                orderSummary.add(productName + " x " + quantity);
                totalItems += quantity;
            }
        }

        Toast.makeText(this, "Orders placed successfully!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(BookOrderActivity.this, OrderConfirmationActivity.class);
        intent.putStringArrayListExtra("order_items", orderSummary);
        intent.putExtra("total_count", totalItems);
        startActivity(intent);
        finish();
    }
}
