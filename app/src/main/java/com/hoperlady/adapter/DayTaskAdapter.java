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

import com.hoperlady.Pojo.DayTaskPojo;
import com.hoperlady.R;
import com.hoperlady.app.MyTaskDetails;


import java.util.ArrayList;

public class DayTaskAdapter extends RecyclerView.Adapter<DayTaskAdapter.MyViewHolder> {
    private ArrayList<DayTaskPojo> aData;
    private Context aContext;
    private String myCurrencySymbol = "";

    public DayTaskAdapter(Context context, ArrayList<DayTaskPojo> arrayList, String currencySYM) {
        aData = arrayList;
        aContext = context;
        myCurrencySymbol = currencySYM;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_task_layout_single, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.bookingIdTV.setText(aData.get(position).getBooking_id());
        holder.categoryNameTV.setText(aData.get(position).getCategory_name());
        holder.dateTimeTV.setText(/*aData.get(position).getDate() + ", " + */aData.get(position).getTime());
        holder.addressTV.setText(aData.get(position).getAddress());
        holder.amountTV.setText(aData.get(position).getAmount());
        holder.currencySymbolTV.setText(myCurrencySymbol);

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(aContext, MyTaskDetails.class);
                i.putExtra("JobId", aData.get(position).getBooking_id());
                aContext.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return aData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView bookingIdTV, categoryNameTV, dateTimeTV, addressTV, amountTV, currencySymbolTV;
        CardView cardview;

        public MyViewHolder(View itemView) {
            super(itemView);
            bookingIdTV = itemView.findViewById(R.id.bookingIdTV);
            categoryNameTV = itemView.findViewById(R.id.categoryNameTV);
            dateTimeTV = itemView.findViewById(R.id.dateTimeTV);
            addressTV = itemView.findViewById(R.id.addressTV);
            amountTV = itemView.findViewById(R.id.amountTV);
            currencySymbolTV = itemView.findViewById(R.id.currencySymbolTV);
            cardview = itemView.findViewById(R.id.cardview);
        }
    }
}
