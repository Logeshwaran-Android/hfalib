package com.hoperlady.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hoperlady.R;

import java.util.ArrayList;


/**
 * Created by CAS63 on 6/28/2018.
 */

public class GenderAdapter extends BaseAdapter {
    private ArrayList<String> myGenderArrList;
    private Context myContext;
    private LayoutInflater myInflater;

    public GenderAdapter(Context aContext, ArrayList<String> aGndrList) {
        this.myContext = aContext;
        this.myGenderArrList = aGndrList;
        myInflater = LayoutInflater.from(aContext);
    }

    @Override
    public int getCount() {
        return myGenderArrList.size();
    }

    @Override
    public String getItem(int position) {
        return myGenderArrList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        private TextView myGenderTXT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = myInflater.inflate(R.layout.layout_inflater_gender_list_item, parent, false);
            holder = new ViewHolder();
            holder.myGenderTXT = (TextView) view.findViewById(R.id.layout_inflater_gender_TXT);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        if (myGenderArrList.get(position).equalsIgnoreCase("Select Gender")) {
            myGenderArrList.remove(position);
        } else {
            holder.myGenderTXT.setText(myGenderArrList.get(position));
        }

        return view;
    }
}