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

public class AdminMachineManagementActivity extends AppCompatActivity {

    private RecyclerView rvMachines;
    private MachineAdapter machineAdapter;
    private List<Machine> machineList = new ArrayList<>();

    private TextView tvError, tvMachineCount;
    private ProgressBar progressBar;
    private EditText etSearch;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_machine_management);

        // Find Views
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null)
            btnBack.setOnClickListener(v -> finish());

        tvError = findViewById(R.id.tv_error);
        progressBar = findViewById(R.id.progress_bar);
        tvMachineCount = findViewById(R.id.tv_machine_count);
        etSearch = findViewById(R.id.et_search);
        rvMachines = findViewById(R.id.rv_machines);
        swipeRefresh = findViewById(R.id.swipe_refresh);

        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this::fetchMachines);
        }

        FloatingActionButton fabAddMachine = findViewById(R.id.fab_add_machine);
        if (fabAddMachine != null) {
            fabAddMachine.setOnClickListener(v -> {
                Intent intent = new Intent(AdminMachineManagementActivity.this, AddNewMachineActivity.class);
                startActivity(intent);
            });
        }

        // Setup RecyclerView
        rvMachines.setLayoutManager(new LinearLayoutManager(this));
        machineAdapter = new MachineAdapter(this, machineList, machine -> {
            Intent intent = new Intent(AdminMachineManagementActivity.this, EditMachineDetailsActivity.class);
            intent.putExtra("machine_data", machine);
            startActivity(intent);
        });
        rvMachines.setAdapter(machineAdapter);

        // Search Logic
        if (etSearch != null) {
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (machineAdapter != null) {
                        machineAdapter.filter(s.toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchMachines();
    }

    private void fetchMachines() {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
        if (tvError != null)
            tvError.setVisibility(View.GONE);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAdminMachines().enqueue(new Callback<List<Machine>>() {
            @Override
            public void onResponse(Call<List<Machine>> call, Response<List<Machine>> response) {
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
                if (swipeRefresh != null)
                    swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Machine> fetchedList = response.body();

                    // Update Adapter
                    machineAdapter.updateList(fetchedList);

                    // Update Badge
                    if (tvMachineCount != null) {
                        tvMachineCount.setText(fetchedList.size() + " Machines");
                    }

                    if (fetchedList.isEmpty()) {
                        if (tvError != null) {
                            tvError.setText("No machines found. Showing Demo Machine.");
                            tvError.setVisibility(View.VISIBLE);
                        }
                        // Add Demo if empty
                        addDemoMachine();
                    }

                    // Apply Search if text exists
                    if (etSearch != null && etSearch.getText().length() > 0) {
                        machineAdapter.filter(etSearch.getText().toString());
                    }

                } else {
                    Toast.makeText(AdminMachineManagementActivity.this, "Response Failed: " + response.code(),
                            Toast.LENGTH_LONG).show();
                    if (tvError != null) {
                        tvError.setText("Failed to load: " + response.message());
                        tvError.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Machine>> call, Throwable t) {
                if (progressBar != null)
                    progressBar.setVisibility(View.GONE);
                if (swipeRefresh != null)
                    swipeRefresh.setRefreshing(false);
                Toast.makeText(AdminMachineManagementActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG)
                        .show();

                if (tvError != null) {
                    tvError.setText("Error: " + t.getMessage() + "\nShowing Demo Machine");
                    tvError.setVisibility(View.VISIBLE);
                }

                // Add Demo on Failure
                addDemoMachine();
            }
        });
    }

    private void addDemoMachine() {
        List<Machine> demoList = new ArrayList<>();
        Machine dummy = new Machine();
        dummy.setMachineName("Demo Machine (Standard)");
        dummy.setMachineStatus("Running");
        dummy.setUnit("kg");
        dummy.setMinCapacity("5");
        dummy.setMaxCapacity("10");
        dummy.setId(999);
        demoList.add(dummy);

        machineAdapter.updateList(demoList);
        if (tvMachineCount != null)
            tvMachineCount.setText("1 Demo");
    }
}
