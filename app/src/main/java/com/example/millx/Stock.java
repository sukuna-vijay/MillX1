package com.example.millx;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Stock implements Serializable {
    @SerializedName("product_id")
    private int id; // Backend uses product_id

    @SerializedName("product_name")
    private String name;

    @SerializedName("product_quantity")
    private double quantity;

    @SerializedName("unit")
    private String unit;

    @SerializedName("image")
    private String image;

    public Stock(int id, String name, double quantity, String unit, String image) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getImage() {
        return image;
    }
}
