package com.hoperlady.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hoperlady.Pojo.AvailDayPojo;
import com.hoperlady.Pojo.Availability_selecting_pojo;
import com.hoperlady.R;

import java.util.ArrayList;

public class RegisterAvailabiltyAdapterCopy extends BaseAdapter {
    LayoutInflater inflater;
    ArrayList<AvailDayPojo> myMainArr;
    ArrayList<Availability_selecting_pojo> selectingArray = null;
    private Context context;

    public RegisterAvailabiltyAdapterCopy(Context context, ArrayList<AvailDayPojo> aMainArr) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.myMainArr = aMainArr;
    }

//    public RegisterAvailabilty_Adapter(ArrayList<Availability_selecting_pojo> selected_pojo) {
//        inflater = LayoutInflater.from(context);
//        selectingArray = selected_pojo;
//
//    }

    @Override
    public int getCount() {
        return myMainArr.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = inflater.inflate(R.layout.availability_child_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.Days_txt = (TextView) view.findViewById(R.id.Days_txt);
            viewHolder.Selected_txt = (TextView) view.findViewById(R.id.Selected_txt);
//            viewHolder.myCircBgIMG = (LinearLayout) view.findViewById(R.id.availability_inflate_layout_main_circ_LAY);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.Days_txt.setText(myMainArr.get(position).getDayName());
        System.out.println("-----position---" + position);
//        if (myMainArr != null) {
//            if (myMainArr.get(position).getDaySelected().equals("1")) {
//                viewHolder.myCircBgIMG.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.avail_background_selected));
//                viewHolder.Selected_txt.setText(context.getResources().getString(R.string.text_selected));
//                viewHolder.Selected_txt.setTextColor(Color.parseColor("#FFFFFF"));
//            } else {
//                viewHolder.myCircBgIMG.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.avail_background));
//                viewHolder.Selected_txt.setText(context.getResources().getString(R.string.text_not_selected));
//                viewHolder.Selected_txt.setTextColor(Color.parseColor("#F61317"));
//            }
//
//        }

        return view;
    }

    public class ViewHolder {
        private TextView Days_txt, Selected_txt;
        private LinearLayout myCircBgIMG;
    }
}