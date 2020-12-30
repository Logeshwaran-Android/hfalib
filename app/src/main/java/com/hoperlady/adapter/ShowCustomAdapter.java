package com.hoperlady.adapter;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hoperlady.Pojo.MyProfileAvailabilityShowPojo;
import com.hoperlady.R;


import java.util.ArrayList;

public class ShowCustomAdapter extends RecyclerView.Adapter<ShowCustomAdapter.MyViewHolder> {

    private Context myChildContext;
    private ArrayList<MyProfileAvailabilityShowPojo> myChildAvailability;


    public ShowCustomAdapter(Context aContext, ArrayList<MyProfileAvailabilityShowPojo> aAvailabilityList, int groupPosition) {
        this.myChildContext = aContext;
        this.myChildAvailability = aAvailabilityList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.showrowlayout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.slotTimeTV.setText(myChildAvailability.get(position).getTime());

        if (myChildAvailability.get(position).getSelected().equals("true") || myChildAvailability.get(position).getSelected().equals("1")) {
            holder.slotTimeCV.setCardBackgroundColor(myChildContext.getResources().getColor(R.color.appmain_color));
            holder.slotTimeTV.setTextColor(myChildContext.getResources().getColor(R.color.white));
        } else {
            holder.slotTimeCV.setCardBackgroundColor(myChildContext.getResources().getColor(R.color.white_color));
            holder.slotTimeTV.setTextColor(myChildContext.getResources().getColor(R.color.black));
        }
        holder.slotTimeCV.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return myChildAvailability.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView slotTimeTV;
        LinearLayout slotTimeLL;
        CheckBox wholeDayCB;
        CardView slotTimeCV;

        public MyViewHolder(View itemView) {
            super(itemView);
            slotTimeTV = (TextView) itemView.findViewById(R.id.slotTimeTV);
            slotTimeLL = (LinearLayout) itemView.findViewById(R.id.slotTimeLL);
            wholeDayCB = (CheckBox) itemView.findViewById(R.id.wholeDayCB);
            slotTimeCV = (CardView) itemView.findViewById(R.id.slotTimeCV);
        }
    }
}

