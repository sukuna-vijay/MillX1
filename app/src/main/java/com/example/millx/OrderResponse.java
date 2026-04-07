package com.example.millx;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("orders")
    private List<Order> orders;

    @SerializedName("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public String getMessage() {
        return message;
    }
}
