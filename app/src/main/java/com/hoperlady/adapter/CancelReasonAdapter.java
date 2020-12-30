package com.hoperlady.adapter;

import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hoperlady.Pojo.CancelReasonPojo;
import com.hoperlady.R;


import java.util.ArrayList;

/**
 * Created by user88 on 12/17/2015.
 */
public class CancelReasonAdapter extends BaseAdapter {
    private ArrayList<CancelReasonPojo> data;
    private LayoutInflater mInflater;
    private AppCompatActivity context;
    private String check;

    public CancelReasonAdapter(AppCompatActivity c, ArrayList<CancelReasonPojo> d) {
        context = c;
        mInflater = LayoutInflater.from(context);
        data = d;
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        private TextView cancel_reason;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.cancel_reason_single, parent, false);
            holder = new ViewHolder();
            holder.cancel_reason = (TextView) view.findViewById(R.id.cancel_reasonTv);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.cancel_reason.setText(data.get(position).getReason());

        return view;
    }
}
