package com.example.millx;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AdminOrderResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("orders")
    private List<AdminOrder> orders;

    public String getStatus() {
        return status;
    }

    public List<AdminOrder> getOrders() {
        return orders;
    }
}
