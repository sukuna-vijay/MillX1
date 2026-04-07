package com.example.millx;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MachineStatusActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MachineAdapter adapter;
    private List<Machine> machineList;
    private TextView tvMachineCount;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_status);

        // Header Back Button
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        tvMachineCount = findViewById(R.id.tv_machine_count);
        etSearch = findViewById(R.id.et_search);

        // RecyclerView Setup
        recyclerView = findViewById(R.id.recycler_view_machines);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        machineList = new ArrayList<>();
        // Initialize adapter with isAdmin = false to hide edit buttons
        adapter = new MachineAdapter(this, machineList, machine -> {
            // Optional: Handle item click if needed (e.g. view details)
            // For now, do nothing as per requirement "detail mattum show aaganum"
        }, false);
        recyclerView.setAdapter(adapter);

        // Search Functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Load Data
        fetchMachines();
    }

    private void fetchMachines() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Machine>> call = apiService.getUserMachines();

        call.enqueue(new Callback<List<Machine>>() {
            @Override
            public void onResponse(Call<List<Machine>> call, Response<List<Machine>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    machineList = response.body();
                    adapter.updateList(machineList);
                    tvMachineCount.setText(machineList.size() + " Machines");
                } else {
                    Toast.makeText(MachineStatusActivity.this, "Failed to load machines", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Machine>> call, Throwable t) {
                Toast.makeText(MachineStatusActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
