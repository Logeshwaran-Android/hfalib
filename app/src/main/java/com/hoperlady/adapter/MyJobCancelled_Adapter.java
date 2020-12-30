package com.hoperlady.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hoperlady.Pojo.MyJobCancelledPojo;
import com.hoperlady.R;

import java.util.ArrayList;

/**
 * Created by user88 on 12/12/2015.
 */
public class MyJobCancelled_Adapter extends BaseAdapter {

    private ArrayList<MyJobCancelledPojo> data;
    private LayoutInflater mInflater;
    private Context context;

    public MyJobCancelled_Adapter(Context c, ArrayList<MyJobCancelledPojo> d) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        ViewHolder holder;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.myjob_cancelled_new_single, parent, false);
            holder = new ViewHolder();

            holder.cancelledjob_bookingtime_Tv = (TextView) view.findViewById(R.id.myjob_cancelled_bookingtime_textView);
            holder.cancelledjob_location_Tv = (TextView) view.findViewById(R.id.myjob_cancelled_locationTv);
            holder.cancelledjob_orderidTv = (TextView) view.findViewById(R.id.myjob_cancelled_orderid);
            holder.cancelledjob_username = (TextView) view.findViewById(R.id.myjob_cancelled_username);
            holder.cancelledjob_category = (TextView) view.findViewById(R.id.myjob_cancelled_category);
//            holder.cancelledjob_profile_img = (RoundedImageView) view.findViewById(R.id.myjob_cancelled_profileimg);
            holder.Tv_cancelled_job_status = (TextView) view.findViewById(R.id.cancelled_Job_status);
//            holder.myjob_cancelled_map_icon = view.findViewById(R.id.myjob_cancelled_map_icon);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.Tv_cancelled_job_status.setText(data.get(position).getJobcancelled_status());
        holder.cancelledjob_bookingtime_Tv.setText(data.get(position).getJobcancelled_date());
        holder.cancelledjob_category.setText(data.get(position).getJobcancelled_categorys());
        holder.cancelledjob_username.setText(data.get(position).getJobcancelled_user_name());
        holder.cancelledjob_orderidTv.setText(data.get(position).getOrder_id());
//        Picasso.with(context).load(String.valueOf(data.get(position).getJobcancelled_user_image())).placeholder(R.drawable.nouserimg).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.cancelledjob_profile_img);

        if (data.get(position).getAddress() != null) {
            if (data.get(position).getAddress().equalsIgnoreCase("")) {
//                holder.myjob_cancelled_map_icon.setVisibility(View.GONE);
                holder.cancelledjob_location_Tv.setText("");
            } else {
                holder.cancelledjob_location_Tv.setText(data.get(position).getAddress());
//                holder.myjob_cancelled_map_icon.setVisibility(View.VISIBLE);
            }
        } else {
//            holder.myjob_cancelled_map_icon.setVisibility(View.GONE);
            holder.cancelledjob_location_Tv.setText("");
        }

        return view;
    }

    public class ViewHolder {
        private TextView cancelledjob_location_Tv, cancelledjob_bookingtime_Tv, Tv_cancelled_job_status;
        private TextView cancelledjob_orderidTv, cancelledjob_username, cancelledjob_category;
//        private RoundedImageView cancelledjob_profile_img;
//        private ImageView myjob_cancelled_map_icon;
    }
}
