package com.example.millx;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentPricesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PriceAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_prices);

        apiService = ApiClient.getClient().create(ApiService.class);

        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        recyclerView = findViewById(R.id.recycler_view_current_prices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initial empty adapter
        adapter = new PriceAdapter(this, new ArrayList<>(), null);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchPrices();
    }

    private void fetchPrices() {
        apiService.getPrices().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList = response.body();
                    // Re-create adapter or update it.
                    // PriceAdapter has updateList method? Let's check step 17. Yes it does.
                    if (adapter == null) {
                        adapter = new PriceAdapter(CurrentPricesActivity.this, productList, null);
                        recyclerView.setAdapter(adapter);
                    } else {
                        adapter.updateList(productList);
                    }
                } else {
                    Toast.makeText(CurrentPricesActivity.this, "Failed to load prices", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(CurrentPricesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
