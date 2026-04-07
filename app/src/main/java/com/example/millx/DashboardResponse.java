package com.example.millx;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class DashboardResponse implements Serializable {
    @SerializedName("status")
    private String status;

    @SerializedName("data")
    private DashboardData data;

    public String getStatus() {
        return status;
    }

    public DashboardData getData() {
        return data;
    }

    public static class DashboardData implements Serializable {
        @SerializedName("active_machines")
        private int activeMachines;

        @SerializedName("inactive_machines")
        private int inactiveMachines;

        public int getActiveMachines() {
            return activeMachines;
        }

        public int getInactiveMachines() {
            return inactiveMachines;
        }
    }
}
