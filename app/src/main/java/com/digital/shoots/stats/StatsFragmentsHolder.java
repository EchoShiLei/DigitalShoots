package com.digital.shoots.stats;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digital.shoots.R;

public class StatsFragmentsHolder extends RecyclerView.ViewHolder {

    public  TextView nameView;

    public StatsFragmentsHolder(@NonNull View itemView) {
        super(itemView);
        nameView = itemView.findViewById(R.id.pager_name);
    }

}
