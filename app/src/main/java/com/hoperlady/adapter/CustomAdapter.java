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

import com.hoperlady.Pojo.RegistrastionAvailabilityChildPojo;
import com.hoperlady.Pojo.UpdateAvailabilityPojo;
import com.hoperlady.R;


import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context myChildContext;
    private ArrayList<RegistrastionAvailabilityChildPojo> myChildAvailability;
    private String DayName;
    private int iGroupPosition = 0;
    private Refresh refresh;
    private boolean IsSelected = false;

    private ArrayList<UpdateAvailabilityPojo> arrayListCount = new ArrayList<UpdateAvailabilityPojo>();

    public CustomAdapter(Context aContext, ArrayList<RegistrastionAvailabilityChildPojo> aAvailabilityList, int groupPosition, Refresh refresh) {
        this.myChildContext = aContext;
        this.myChildAvailability = aAvailabilityList;
        this.refresh = refresh;
        this.iGroupPosition = groupPosition;
    }

    public interface Refresh {
        void Update(ArrayList<UpdateAvailabilityPojo> arrayListCount);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.slotTimeTV.setText(myChildAvailability.get(position).getTime());

        if (myChildAvailability.get(position).isSelected()) {
            holder.slotTimeCV.setCardBackgroundColor(myChildContext.getResources().getColor(R.color.appmain_color));
            holder.slotTimeTV.setTextColor(myChildContext.getResources().getColor(R.color.white));
        } else {
            holder.slotTimeCV.setCardBackgroundColor(myChildContext.getResources().getColor(R.color.white_color));
            holder.slotTimeTV.setTextColor(myChildContext.getResources().getColor(R.color.black));
        }

        holder.slotTimeCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myChildAvailability.get(position).isSelected()) {

                    holder.slotTimeCV.setCardBackgroundColor(myChildContext.getResources().getColor(R.color.white_color));
                    holder.slotTimeTV.setTextColor(myChildContext.getResources().getColor(R.color.black));
                    IsSelected = false;

                } else {
                    IsSelected = true;
                    holder.slotTimeCV.setCardBackgroundColor(myChildContext.getResources().getColor(R.color.appmain_color));
                    holder.slotTimeTV.setTextColor(myChildContext.getResources().getColor(R.color.white));
                }

                UpdateAvailabilityPojo uPojo = new UpdateAvailabilityPojo();
                uPojo.setCountPosition(position);
                uPojo.setSlotPosition(false);

                RegistrastionAvailabilityChildPojo registrastionAvailabilityChildPojo = new RegistrastionAvailabilityChildPojo();
                registrastionAvailabilityChildPojo.setTime(myChildAvailability.get(position).getTime());
                registrastionAvailabilityChildPojo.setSlot(myChildAvailability.get(position).getSlot());
                registrastionAvailabilityChildPojo.setDay(myChildAvailability.get(position).getDay());
                registrastionAvailabilityChildPojo.setSelected(IsSelected);

                uPojo.setPojo(registrastionAvailabilityChildPojo);

                arrayListCount.add(uPojo);
                refresh.Update(arrayListCount);
            }
        });
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

