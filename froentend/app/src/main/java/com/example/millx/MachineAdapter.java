package com.example.millx;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MachineAdapter extends RecyclerView.Adapter<MachineAdapter.MachineViewHolder> {

    private Context context;
    private boolean isAdmin;
    private List<Machine> machineList;
    private List<Machine> machineListFull;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Machine machine);
    }

    public MachineAdapter(Context context, List<Machine> machineList, OnItemClickListener listener, boolean isAdmin) {
        this.context = context;
        this.machineList = machineList;
        this.machineListFull = new java.util.ArrayList<>(machineList);
        this.listener = listener;
        this.isAdmin = isAdmin;
    }

    // Overloaded constructor for backward compatibility (default to true/admin) or
    // update existing calls
    public MachineAdapter(Context context, List<Machine> machineList, OnItemClickListener listener) {
        this(context, machineList, listener, true);
    }

    public void updateList(List<Machine> list) {
        this.machineList = list;
        this.machineListFull = new java.util.ArrayList<>(list);
        notifyDataSetChanged();
    }

    public void filter(String text) {
        machineList.clear();
        if (text.isEmpty()) {
            machineList.addAll(machineListFull);
        } else {
            text = text.toLowerCase();
            for (Machine item : machineListFull) {
                if (item.getMachineName().toLowerCase().contains(text) ||
                        item.getMachineStatus().toLowerCase().contains(text)) {
                    machineList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MachineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_machine, parent, false);
        return new MachineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MachineViewHolder holder, int position) {
        Machine machine = machineList.get(position);

        // Name
        String name = machine.getMachineName();
        if (name == null || name.trim().isEmpty()) {
            name = "Unnamed Machine";
        }
        holder.tvMachineName.setText(name);

        // Capacity
        String min = machine.getMinCapacity() != null ? machine.getMinCapacity() : "0";
        String max = machine.getMaxCapacity() != null ? machine.getMaxCapacity() : "0";
        String unit = machine.getUnit() != null ? machine.getUnit() : "kg";
        holder.tvCapacity.setText(String.format("%s-%s %s per hour", min, max, unit));

        // Status Logic
        String status = machine.getMachineStatus();
        if ("Running".equalsIgnoreCase(status) || "active".equalsIgnoreCase(status)) {
            holder.tvStatusBadge.setText("Available");
            holder.tvStatusBadge.setTextColor(Color.parseColor("#4F7E1C")); // Green
            holder.tvStatusBadge.setBackgroundColor(Color.parseColor("#E0EAD9")); // Light Green
            holder.progressCapacity.setProgress(70);
            holder.progressCapacity
                    .setProgressTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4F7E1C")));
        } else {
            holder.tvStatusBadge.setText("Not Available");
            holder.tvStatusBadge.setTextColor(Color.parseColor("#E53E3E")); // Red
            holder.tvStatusBadge.setBackgroundColor(Color.parseColor("#FFEBEE")); // Light Red
            holder.progressCapacity.setProgress(0);
            holder.progressCapacity
                    .setProgressTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#E53E3E")));
        }

        // Edit Button Visibility
        if (isAdmin) {
            holder.btnEditMachine.setVisibility(View.VISIBLE);
            holder.ivArrow.setVisibility(View.GONE);
            holder.btnEditMachine.setOnClickListener(v -> listener.onItemClick(machine));
        } else {
            holder.btnEditMachine.setVisibility(View.GONE);
            holder.ivArrow.setVisibility(View.VISIBLE);
        }

        // Load Image
        String imagePath = machine.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            String cleanPath = imagePath.replace("../", "");
            String fullUrl = ApiClient.BASE_URL + cleanPath;

            com.bumptech.glide.Glide.with(context)
                    .load(fullUrl)
                    .placeholder(R.drawable.ic_speed)
                    .error(R.drawable.ic_speed)
                    .into(holder.ivMachineImage);
        } else {
            // holder.ivMachineImage.setImageResource(R.drawable.ic_speed); // Default
        }
    }

    @Override
    public int getItemCount() {
        return machineList.size();
    }

    public static class MachineViewHolder extends RecyclerView.ViewHolder {
        TextView tvMachineName, tvStatusBadge, tvCapacity;
        View btnEditMachine;
        ImageView ivArrow;
        android.widget.ProgressBar progressCapacity;
        ImageView ivMachineImage;

        public MachineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMachineName = itemView.findViewById(R.id.tv_machine_name);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
            tvCapacity = itemView.findViewById(R.id.tv_capacity);
            btnEditMachine = itemView.findViewById(R.id.btn_edit_machine);
            ivArrow = itemView.findViewById(R.id.iv_arrow);
            progressCapacity = itemView.findViewById(R.id.progress_capacity);
            ivMachineImage = itemView.findViewById(R.id.iv_machine_image);
        }
    }
}
