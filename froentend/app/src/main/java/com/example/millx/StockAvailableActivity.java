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

public class StockAvailableActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StockAdapter adapter;
    private List<Stock> stockList;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_available);

        apiService = ApiClient.getClient().create(ApiService.class);
        recyclerView = findViewById(R.id.recycler_view_stock_available);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        stockList = new ArrayList<>();
        // Null listener so edit buttons are hidden
        adapter = new StockAdapter(this, stockList, null);
        recyclerView.setAdapter(adapter);

        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Return to the previous screen
                }
            });
        }

        fetchStocks();
    }

    private void fetchStocks() {
        apiService.getUserStocks().enqueue(new Callback<List<Stock>>() {
            @Override
            public void onResponse(Call<List<Stock>> call, Response<List<Stock>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    stockList = response.body();
                    adapter.updateList(stockList);
                } else {
                    Toast.makeText(StockAvailableActivity.this, "Failed to load stock data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Stock>> call, Throwable t) {
                Toast.makeText(StockAvailableActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
