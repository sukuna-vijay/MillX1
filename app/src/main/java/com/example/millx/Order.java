package com.example.millx;

import com.google.gson.annotations.SerializedName;

public class Order {
    @SerializedName("order_id")
    private int orderId;

    @SerializedName("product_name")
    private String productName;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("order_status")
    private String status;

    @SerializedName("created_at")
    private String date;

    @SerializedName("image")
    private String image;

    @SerializedName("price")
    private String price;

    @SerializedName("unit")
    private String unit;

    public int getOrderId() {
        return orderId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public String getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public String getPrice() {
        return price;
    }

    public String getUnit() {
        return unit;
    }
}
