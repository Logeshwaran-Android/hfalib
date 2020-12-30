package com.hoperlady.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hoperlady.Pojo.NewAvailabilityPojo;
import com.hoperlady.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewAvailabilityAdapter extends BaseAdapter {

    private ArrayList<NewAvailabilityPojo> data;
    private LayoutInflater mInflater;
    private Context context;

    private String dateFrom = "", dateTo = "";

    public NewAvailabilityAdapter(Context c, ArrayList<NewAvailabilityPojo> d) {
        context = c;
        mInflater = LayoutInflater.from(context);
        data = d;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    public class ViewHolder {
        private LinearLayout groupLinearLayout, childLinearLayout;
        private ImageView dayCheckIV, wholeDayCheckIV;
        private TextView dayName, wholeDayTV, fromTimeTV, toTimeTV;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = mInflater.inflate(R.layout.layout_inflate_availability_group_list_item, parent, false);
            holder = new ViewHolder();

            holder.groupLinearLayout = (LinearLayout) view.findViewById(R.id.groupLinearLayout);
            holder.childLinearLayout = (LinearLayout) view.findViewById(R.id.childLinearLayout);
            holder.dayCheckIV = (ImageView) view.findViewById(R.id.dayCheckIV);
            holder.wholeDayCheckIV = (ImageView) view.findViewById(R.id.wholeDayCheckIV);
            holder.dayName = (TextView) view.findViewById(R.id.dayName);
            holder.wholeDayTV = (TextView) view.findViewById(R.id.wholeDayTV);
            holder.fromTimeTV = (TextView) view.findViewById(R.id.fromTimeTV);
            holder.toTimeTV = (TextView) view.findViewById(R.id.toTimeTV);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();

        }

        if (data.get(position).getWholeday().equals("1")) {
            holder.wholeDayCheckIV.setBackgroundResource(R.drawable.checkbox);
            holder.childLinearLayout.setVisibility(View.GONE);
        } else {
            holder.wholeDayCheckIV.setBackgroundResource(R.drawable.ic_checkbox_empty);
            holder.childLinearLayout.setVisibility(View.VISIBLE);
        }

        if (data.get(position).getSelected().equals("1")) {
            holder.dayCheckIV.setBackgroundResource(R.drawable.checkbox);
            holder.wholeDayCheckIV.setVisibility(View.VISIBLE);
            holder.wholeDayTV.setVisibility(View.VISIBLE);
        } else {
            holder.dayCheckIV.setBackgroundResource(R.drawable.ic_checkbox_empty);
            holder.wholeDayCheckIV.setVisibility(View.GONE);
            holder.wholeDayTV.setVisibility(View.GONE);
        }

        holder.dayName.setText(data.get(position).getDay());

        try {
            if (!data.get(position).getStartTime().equals("0")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("hhmm");
                Date date = dateFormat.parse(data.get(position).getStartTime());
                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
                dateFrom = formatter.format(date);
            } else {
                dateFrom = "00:00 am";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            if (!data.get(position).getEndTime().equals("0")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("hhmm");
                Date date = dateFormat.parse(data.get(position).getEndTime());
                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
                dateTo = formatter.format(date);
            } else {
                dateTo = "00:00 am";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        dateFrom = data.get(position).getStartTime();
//        if (data.get(position).getStartTime().length() == 4) {
//            dateFrom = data.get(position).getStartTime();
//        } else if (data.get(position).getStartTime().length() == 3) {
//            dateFrom = "0" + data.get(position).getStartTime();
//        }
//
//        dateTo = data.get(position).getEndTime();
//        if (data.get(position).getEndTime().length() == 4) {
//            dateTo = data.get(position).getEndTime();
//        } else if (data.get(position).getEndTime().length() == 3) {
//            dateTo = "0" + data.get(position).getEndTime();
//        }

        holder.fromTimeTV.setText(dateFrom);
        holder.toTimeTV.setText(dateTo);
        return view;
    }
}