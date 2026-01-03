package com.example.millx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminMachineManagementActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_machine_management);

        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        FloatingActionButton fabAddMachine = findViewById(R.id.fab_add_machine);
        if (fabAddMachine != null) {
            fabAddMachine.setOnClickListener(v -> {
                Intent intent = new Intent(AdminMachineManagementActivity.this, AddNewMachineActivity.class);
                startActivity(intent);
            });
        }

        // Set up click listeners for all Edit buttons
        setupEditButtons();
    }

    private void setupEditButtons() {
        int[] editButtonIds = {
                R.id.btn_edit1, R.id.btn_edit2, R.id.btn_edit3,
                R.id.btn_edit4, R.id.btn_edit5, R.id.btn_edit6
        };

        for (int id : editButtonIds) {
            MaterialButton btnEdit = findViewById(id);
            if (btnEdit != null) {
                btnEdit.setOnClickListener(v -> {
                    Intent intent = new Intent(AdminMachineManagementActivity.this, EditMachineDetailsActivity.class);
                    startActivity(intent);
                });
            }
        }
    }
}
