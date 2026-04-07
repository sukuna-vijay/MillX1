package com.example.millx;

import com.google.gson.annotations.SerializedName;

public class ProductRequest {
    @SerializedName("id")
    private int id;

    public ProductRequest(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
