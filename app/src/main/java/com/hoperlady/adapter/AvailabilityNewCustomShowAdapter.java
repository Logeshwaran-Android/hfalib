package com.hoperlady.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoperlady.Pojo.MyProfileAvailabilityPojo;
import com.hoperlady.R;

import java.util.ArrayList;

public class AvailabilityNewCustomShowAdapter extends RecyclerView.Adapter<AvailabilityNewCustomShowAdapter.MyViewHolder> {

    private Context myContext;
    private ArrayList<MyProfileAvailabilityPojo> myAvailability;
    Dialog dialog;
    private boolean selectedSlot = false;

    public AvailabilityNewCustomShowAdapter(Context aContext, ArrayList<MyProfileAvailabilityPojo> aAvailabilityList) {
        this.myContext = aContext;
        this.myAvailability = aAvailabilityList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.availablity_edit_item_single, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.titleDayNameTV.setText(myAvailability.get(position).getDayNames());
        holder.addAndEditNameTV.setVisibility(View.GONE);

        StringBuffer Strtime = new StringBuffer();
        selectedSlot = false;
        for (int i = 0; i < myAvailability.get(position).getPojoArrayList().size(); i++) {
            if (myAvailability.get(position).getPojoArrayList().get(i).getSelected().equals("true")
                    || myAvailability.get(position).getPojoArrayList().get(i).getSelected().equals("1")) {
                Strtime.append(myAvailability.get(position).getPojoArrayList().get(i).getTime()).append(", ");
                holder.timeShowListTV.setText(Strtime);
                if (myAvailability.get(position).getPojoArrayList().size() > 3) {
                    selectedSlot = true;
                }
            }
            if (myAvailability.get(position).getWholeDayChoose().equals("true")) {
                holder.timeShowListTV.setText(myContext.getResources().getString(R.string.wholeday));
                selectedSlot = false;
            }
        }
        if (selectedSlot) {
            holder.viewMoreTV.setVisibility(View.VISIBLE);
        } else {
            holder.viewMoreTV.setVisibility(View.GONE);
        }

        holder.daysRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.viewMoreTV.getVisibility() == View.VISIBLE) {
                    DisplayMetrics metrics = myContext.getResources().getDisplayMetrics();
                    int screenWidth = (int) (metrics.widthPixels * 0.80);//fill only 80% of the screen
                    View view = View.inflate(myContext, R.layout.myprofile_availability_show_single, null);
                    dialog = new Dialog(myContext);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(view);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.getWindow().setLayout(screenWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    TextView cancelTv = (TextView) view.findViewById(R.id.cancelTv);
                    final TextView doNotClickTV = (TextView) view.findViewById(R.id.doNotClickTV);
                    TextView titleDayNameTV = (TextView) view.findViewById(R.id.titleDayNameTV);
                    final RecyclerView hourAvailabileRV = view.findViewById(R.id.hourAvailabileRV);
                    hourAvailabileRV.setLayoutManager(new GridLayoutManager(myContext, 3));

                    titleDayNameTV.setText(myAvailability.get(position).getDayNames());

                    cancelTv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    doNotClickTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                    ShowCustomAdapter customAdapter = new ShowCustomAdapter(myContext, myAvailability.get(position).getPojoArrayList(), position);
                    hourAvailabileRV.setAdapter(customAdapter);

                    dialog.show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return myAvailability.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titleDayNameTV, timeShowListTV, addAndEditNameTV, viewMoreTV;
        RelativeLayout daysRL;

        public MyViewHolder(View itemView) {
            super(itemView);
            titleDayNameTV = (TextView) itemView.findViewById(R.id.titleDayNameTV);
            timeShowListTV = (TextView) itemView.findViewById(R.id.timeShowListTV);
            addAndEditNameTV = (TextView) itemView.findViewById(R.id.addAndEditNameTV);
            viewMoreTV = (TextView) itemView.findViewById(R.id.viewMoreTV);
            daysRL = (RelativeLayout) itemView.findViewById(R.id.daysRL);
        }
    }

}

