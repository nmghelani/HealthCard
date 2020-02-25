package com.shrewd.healthcard.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.shrewd.healthcard.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.ViewHolder> {

    private final Context mContext;
    private final LayoutInflater inflater;

    public AnalysisAdapter(Context mContext){
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_analysis,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (position) {
            case 0:
                holder.bcPatient.setVisibility(View.GONE);
                holder.pcPatient.setVisibility(View.VISIBLE);
                holder.lcPatient.setVisibility(View.GONE);
                break;
            case 1:
                holder.bcPatient.setVisibility(View.VISIBLE);
                holder.pcPatient.setVisibility(View.GONE);
                holder.lcPatient.setVisibility(View.GONE);
                break;
            case 2:
                holder.bcPatient.setVisibility(View.GONE);
                holder.pcPatient.setVisibility(View.GONE);
                holder.lcPatient.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private final PieChart pcPatient;
        private final LineChart lcPatient;
        private final BarChart bcPatient;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pcPatient = itemView.findViewById(R.id.pcPatient);
            lcPatient = itemView.findViewById(R.id.lcPatient);
            bcPatient = itemView.findViewById(R.id.bcPatient);
        }
    }
}
