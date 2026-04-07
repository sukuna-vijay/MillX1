package com.example.millx;

import com.google.gson.annotations.SerializedName;

public class AdminOrder {
    @SerializedName("order_id")
    private int orderId;

    @SerializedName("user_name")
    private String userName;

    @SerializedName("user_phone")
    private String userPhone;

    @SerializedName("product_name")
    private String productName;

    @SerializedName("image")
    private String image;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("order_status")
    private String status;

    @SerializedName("created_at")
    private String date;
    
    @SerializedName("price")
    private String price;

    @SerializedName("unit")
    private String unit;

    public int getOrderId() {
        return orderId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getProductName() {
        return productName;
    }

    public String getImage() {
        return image;
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

    public String getPrice() {
        return price;
    }

    public String getUnit() {
        return unit;
    }
}
