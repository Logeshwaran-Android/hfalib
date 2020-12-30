package com.hoperlady.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hoperlady.Pojo.WeekTaskBarChartPojo;
import com.hoperlady.R;
import com.hoperlady.app.DayTaskActivity;

import java.util.ArrayList;

public class WeeklyTaskAdapter extends RecyclerView.Adapter<WeeklyTaskAdapter.MyViewHolder> {

    private Context aContext;
    private ArrayList<WeekTaskBarChartPojo> aData;
    private String aCurrencySYMBOLE = "";

    public WeeklyTaskAdapter(Context context, ArrayList<WeekTaskBarChartPojo> arrayList, String currencySymbole) {
        aContext = context;
        aData = arrayList;
        aCurrencySYMBOLE = currencySymbole;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.week_task_layout_single, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.taskDateTV.setText(aData.get(position).getJob_date());
        holder.currencySymbolTV.setText(aCurrencySYMBOLE);
        holder.amountTV.setText(aData.get(position).getEarnings());
        holder.taskCount.setText(aData.get(position).getTask_count());

        holder.weekTaskCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!aData.get(position).getTask_count().equalsIgnoreCase("0")) {
                    Intent intent = new Intent(aContext, DayTaskActivity.class);
                    intent.putExtra("date", aData.get(position).getJob_date());
                    intent.putExtra("task_count", aData.get(position).getTask_count());
                    intent.putExtra("total_earnings", aData.get(position).getEarnings());
                    aContext.startActivity(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return aData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView weekTaskCV;
        TextView taskDateTV, currencySymbolTV, amountTV, taskCount;

        public MyViewHolder(View itemView) {
            super(itemView);
            weekTaskCV = (CardView) itemView.findViewById(R.id.weekTaskCV);
            taskDateTV = (TextView) itemView.findViewById(R.id.taskDateTV);
            currencySymbolTV = (TextView) itemView.findViewById(R.id.currencySymbolTV);
            amountTV = (TextView) itemView.findViewById(R.id.amountTV);
            taskCount = (TextView) itemView.findViewById(R.id.taskCount);
        }
    }
}
