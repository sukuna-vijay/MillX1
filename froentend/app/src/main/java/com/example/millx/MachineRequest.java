package com.example.millx;

import com.google.gson.annotations.SerializedName;

public class MachineRequest {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("status")
    private String status;

    @SerializedName("min")
    private String min;

    @SerializedName("max")
    private String max;

    @SerializedName("unit")
    private String unit;

    @SerializedName("description")
    private String description;

    // Constructor for Add (Simple)
    public MachineRequest(String name, String status) {
        this.name = name;
        this.status = status;
    }

    // Constructor for Add (Full)
    public MachineRequest(String name, String status, String min, String max, String unit, String description) {
        this.name = name;
        this.status = status;
        this.min = min;
        this.max = max;
        this.unit = unit;
        this.description = description;
    }

    // Constructor for Update Details
    public MachineRequest(int id, String name, String min, String max, String unit, String description) {
        this.id = id;
        this.name = name;
        this.min = min;
        this.max = max;
        this.unit = unit;
        this.description = description;
    }

    // Constructor for Status Update
    public MachineRequest(int id, String status) {
        this.id = id;
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Constructor for Delete
    public MachineRequest(int id) {
        this.id = id;
    }
}
