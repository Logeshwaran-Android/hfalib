package com.hoperlady.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hoperlady.Pojo.MyProfileAvailabilityEditPojo;
import com.hoperlady.Pojo.UpdateAvailabilityEditPojo;
import com.hoperlady.R;

import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class EditCustomAdapter extends RecyclerView.Adapter<EditCustomAdapter.MyViewHolder> {

    private Context myChildContext;
    private ArrayList<MyProfileAvailabilityEditPojo> myChildAvailability;
    private String DayName;
    private int iGroupPosition = 0;
    private Refresh refresh;
    private boolean IsSelected = false;

    private ArrayList<UpdateAvailabilityEditPojo> arrayListCount = new ArrayList<UpdateAvailabilityEditPojo>();

    public EditCustomAdapter(Context aContext, ArrayList<MyProfileAvailabilityEditPojo> aAvailabilityList, int groupPosition, Refresh refresh) {
        this.myChildContext = aContext;
        this.myChildAvailability = aAvailabilityList;
        this.refresh = refresh;
        this.iGroupPosition = groupPosition;
    }

    public interface Refresh {
        void Update(ArrayList<UpdateAvailabilityEditPojo> arrayListCount);
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

        arrayListCount = new ArrayList<UpdateAvailabilityEditPojo>();
        if (myChildAvailability.size() > 0) {
            for (int i = 0; i < myChildAvailability.size(); i++) {
                UpdateAvailabilityEditPojo uPojo = new UpdateAvailabilityEditPojo();
                uPojo.setCountPosition(i);
                uPojo.setSlotPosition(myChildAvailability.get(i).getSelected().equals("true"));

                MyProfileAvailabilityEditPojo registrastionAvailabilityChildPojo = new MyProfileAvailabilityEditPojo();
                registrastionAvailabilityChildPojo.setTime(myChildAvailability.get(i).getTime());
                registrastionAvailabilityChildPojo.setDay(myChildAvailability.get(i).getDay());
                registrastionAvailabilityChildPojo.setSlot(myChildAvailability.get(i).getSlot());
                registrastionAvailabilityChildPojo.setSelected(myChildAvailability.get(i).getSelected());

                uPojo.setMyProfileAvailabilityPojo(registrastionAvailabilityChildPojo);
                arrayListCount.add(uPojo);
            }
        }

        if (myChildAvailability.get(position).getSelected().equals("true")) {
            holder.slotTimeCV.setCardBackgroundColor(myChildContext.getResources().getColor(R.color.appmain_color));
            holder.slotTimeTV.setTextColor(myChildContext.getResources().getColor(R.color.white));
        } else {
            holder.slotTimeCV.setCardBackgroundColor(myChildContext.getResources().getColor(R.color.white_color));
            holder.slotTimeTV.setTextColor(myChildContext.getResources().getColor(R.color.black));
        }

        holder.slotTimeCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myChildAvailability.get(position).getSelected().equals("true")) {
                    holder.slotTimeCV.setCardBackgroundColor(myChildContext.getResources().getColor(R.color.white_color));
                    holder.slotTimeTV.setTextColor(myChildContext.getResources().getColor(R.color.black));
                    IsSelected = false;
                } else {
                    IsSelected = true;
                    holder.slotTimeCV.setCardBackgroundColor(myChildContext.getResources().getColor(R.color.appmain_color));
                    holder.slotTimeTV.setTextColor(myChildContext.getResources().getColor(R.color.white));
                }

                UpdateAvailabilityEditPojo uPojo = new UpdateAvailabilityEditPojo();
                uPojo.setCountPosition(position);
                uPojo.setSlotPosition(false);

                MyProfileAvailabilityEditPojo registrastionAvailabilityChildPojo = new MyProfileAvailabilityEditPojo();
                registrastionAvailabilityChildPojo.setTime(myChildAvailability.get(position).getTime());
                registrastionAvailabilityChildPojo.setDay(myChildAvailability.get(position).getDay());
                registrastionAvailabilityChildPojo.setSlot(myChildAvailability.get(position).getSlot());
                registrastionAvailabilityChildPojo.setSelected(IsSelected + "");

                uPojo.setMyProfileAvailabilityPojo(registrastionAvailabilityChildPojo);

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

