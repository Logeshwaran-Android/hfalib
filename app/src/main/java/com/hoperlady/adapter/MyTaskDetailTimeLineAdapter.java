package com.hoperlady.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hoperlady.Pojo.WorkFlow_Pojo;
import com.hoperlady.R;

import java.util.ArrayList;

/**
 * Casperon Technology on 1/21/2016.
 */
public class MyTaskDetailTimeLineAdapter extends RecyclerView.Adapter<MyTaskDetailTimeLineAdapter.ViewHolder> {

    private ArrayList<WorkFlow_Pojo> data;
    private LayoutInflater mInflater;
    private Context context;

    public MyTaskDetailTimeLineAdapter(Context c, ArrayList<WorkFlow_Pojo> d) {
        context = c;
        mInflater = LayoutInflater.from(context);
        data = d;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.myjobs_detail_time_line_single, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.status.setText(data.get(position).getJob_title());
        holder.date.setText(data.get(position).getJob_date() + "\n" + data.get(position).getJob_time());
        if (data.size() == position + 1)
            holder.verticalLineView.setVisibility(View.GONE);
        else
            holder.verticalLineView.setVisibility(View.VISIBLE);

        if (position == 0)
            holder.verticalTempView.setVisibility(View.INVISIBLE);
        else
            holder.verticalTempView.setVisibility(View.VISIBLE);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView status, date;
        private View verticalLineView, verticalTempView;

        public ViewHolder(View view) {
            super(view);
            status = (TextView) view.findViewById(R.id.myJob_detail_status_single_status_textView);
            date = (TextView) view.findViewById(R.id.myJob_detail_status_single_date_textView);
            verticalLineView = (View) view.findViewById(R.id.verticalLineView);
            verticalTempView = (View) view.findViewById(R.id.verticalTempView);
        }
    }
}


