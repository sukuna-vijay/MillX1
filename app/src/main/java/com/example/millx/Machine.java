package com.example.millx;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Machine implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("machine_name")
    private String machineName;

    @SerializedName("machine_status")
    private String machineStatus;

    @SerializedName("min_capacity")
    private String minCapacity;

    @SerializedName("max_capacity")
    private String maxCapacity;

    @SerializedName("unit")
    private String unit;

    @SerializedName("description")
    private String description;

    @SerializedName("image")
    private String image;

    // Getters and Setters
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getMachineStatus() {
        return machineStatus;
    }

    public void setMachineStatus(String machineStatus) {
        this.machineStatus = machineStatus;
    }

    public String getMinCapacity() {
        return minCapacity;
    }

    public void setMinCapacity(String minCapacity) {
        this.minCapacity = minCapacity;
    }

    public String getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(String maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
