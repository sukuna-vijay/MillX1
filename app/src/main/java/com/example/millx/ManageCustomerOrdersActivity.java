package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageCustomerOrdersActivity extends AppCompatActivity implements PriceAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private PriceAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private ApiService apiService;
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton fabAdd;

    // ActivityResultLauncher to handle Edit result
    private final ActivityResultLauncher<Intent> editProductLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Refresh the list to show updated data (Edit or Delete)
                    fetchProducts();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_customer_orders);

        initViews();
        setupRecyclerView();
        fetchProducts();
    }

    private void initViews() {
        MaterialCardView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (findViewById(R.id.btn_save) != null) {
            findViewById(R.id.btn_save).setVisibility(android.view.View.GONE);
        }

        swipeRefresh = findViewById(R.id.swipe_refresh);
        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this::fetchProducts);
        }

        fabAdd = findViewById(R.id.fab_add);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> {
                Intent intent = new Intent(ManageCustomerOrdersActivity.this, AddNewProductActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recycler_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PriceAdapter(this, productList, this);
        recyclerView.setAdapter(adapter);
    }

    private void fetchProducts() {
        apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Product>> call = apiService.getAdminPrices();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    productList = response.body();
                    adapter.updateList(productList);
                } else {
                    Toast.makeText(ManageCustomerOrdersActivity.this, "Failed to load products", Toast.LENGTH_SHORT)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                Toast.makeText(ManageCustomerOrdersActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    @Override
    public void onItemClick(Product product) {
        Intent intent = new Intent(this, EditOrderProductActivity.class);
        intent.putExtra("product_id", product.getId());
        intent.putExtra("product_name", product.getName());
        intent.putExtra("product_subtitle", product.getDescription());
        intent.putExtra("product_price", product.getPrice());
        intent.putExtra("product_image_url", product.getImage());
        intent.putExtra("product_unit", product.getUnit());
        editProductLauncher.launch(intent);
    }
}
