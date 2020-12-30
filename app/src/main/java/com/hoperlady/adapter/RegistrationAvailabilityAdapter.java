package com.hoperlady.adapter;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;

import com.hoperlady.R;
import com.hoperlady.Utils.SpacesItemDecoration;

import com.hoperlady.Pojo.RegistrastionAvailabilityChildPojo;

import java.util.ArrayList;

import core.Widgets.CustomTextView;

public class RegistrationAvailabilityAdapter extends BaseExpandableListAdapter {
    private Context myContext;
    private String[] daysNameList;
    private CustomAdapter customAdapter;
    private ArrayList<RegistrastionAvailabilityChildPojo> slotArrayList;
    private ArrayList<RegistrastionAvailabilityChildPojo> slotFilterArrayList;
    private Refresh refresh;

    public RegistrationAvailabilityAdapter(Context aContext, String[] daysNameList, ArrayList<RegistrastionAvailabilityChildPojo> slotArrayList, Refresh refresh) {
        this.myContext = aContext;
        this.daysNameList = daysNameList;
        this.slotArrayList = slotArrayList;
        this.refresh = refresh;
    }

    public interface Refresh {
        void Update(int Position, RegistrastionAvailabilityChildPojo pojo);
    }

    @Override
    public int getGroupCount() {
        return daysNameList.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return daysNameList[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) myContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.registration_availability_single, null);
        }

        CustomTextView aDaysNameTXT = (CustomTextView) convertView.findViewById(R.id.daysNameTV);
        ImageView aArrowIMG = (ImageView) convertView.findViewById(R.id.sideArrowIV);

        aDaysNameTXT.setText(daysNameList[groupPosition]);
        if (isExpanded) {
            aArrowIMG.setRotation(180);
        } else {
            aArrowIMG.setRotation(0);
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.registration_availability_childe_single, null);
        }

        RecyclerView hourAvailabileRV = (RecyclerView) convertView.findViewById(R.id.hourAvailabileRV);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(myContext, 4);
        hourAvailabileRV.setLayoutManager(gridLayoutManager);

        int spacingInPixels = myContext.getResources().getDimensionPixelSize(R.dimen.available_hours_space);
        hourAvailabileRV.addItemDecoration(new SpacesItemDecoration(spacingInPixels));

        /*customAdapter = new CustomAdapter(myContext, myAvailability.get(groupPosition).getPojoArrayList(), groupPosition, childPosition);
        hourAvailabileRV.setAdapter(customAdapter);*/

        slotFilterArrayList = new ArrayList<>();
        for (int l = 0; l < slotArrayList.size(); l++) {
            if (slotArrayList.get(l).getDay().equals(daysNameList[groupPosition])) {
                RegistrastionAvailabilityChildPojo registrastionAvailabilityChildPojo = new RegistrastionAvailabilityChildPojo();
                registrastionAvailabilityChildPojo.setTime(slotArrayList.get(l).getTime());
                registrastionAvailabilityChildPojo.setDay(slotArrayList.get(l).getDay());
                registrastionAvailabilityChildPojo.setSelected(slotArrayList.get(l).isSelected());
                registrastionAvailabilityChildPojo.setSlot(slotArrayList.get(l).getSlot());
                registrastionAvailabilityChildPojo.setParentIndex(slotArrayList.get(l).getParentIndex());
                slotFilterArrayList.add(registrastionAvailabilityChildPojo);
            }
        }

//        customAdapter = new CustomAdapter(myContext, daysNameList[groupPosition], slotFilterArrayList, groupPosition, new CustomAdapter.Refresh() {
//            @Override
//            public void Update(int Position, RegistrastionAvailabilityChildPojo pojo) {
//                slotFilterArrayList.set(Position, pojo);
//                refresh.Update(pojo.getParentIndex(), pojo);
//            }
//        });
//        hourAvailabileRV.setAdapter(customAdapter);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}



