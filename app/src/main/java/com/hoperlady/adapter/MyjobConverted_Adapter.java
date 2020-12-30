package com.hoperlady.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hoperlady.Pojo.MyjobConverted_Pojo;
import com.hoperlady.R;

import java.util.ArrayList;

/**
 * Created by user88 on 12/12/2015.
 */
public class MyjobConverted_Adapter extends BaseAdapter {


    private ArrayList<MyjobConverted_Pojo> data;
    private LayoutInflater mInflater;
    private Context context;

    public MyjobConverted_Adapter(Context c, ArrayList<MyjobConverted_Pojo> d) {
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
            view = mInflater.inflate(R.layout.myjob_converted_new_single, parent, false);
            holder = new ViewHolder();

            holder.myjobconverted_bookingtimeTv = (TextView) view.findViewById(R.id.myjob_converted_bookingtime_textView);
            holder.myjobconverted_location_Tv = (TextView) view.findViewById(R.id.myjob_converted_locationTv);
            holder.myjobconverted_orderidTv = (TextView) view.findViewById(R.id.myjob_converted_orderid);
            holder.myjobconverted_username = (TextView) view.findViewById(R.id.myjob_converted_username);
            holder.myjobconverted_category = (TextView) view.findViewById(R.id.myjob_converted_category);
//            holder.myjobconverted_profile_img = (RoundedImageView) view.findViewById(R.id.myjob_converted_profileimg);
            holder.Tv_job_status = (TextView) view.findViewById(R.id.converted_Job_status);

//            holder.myjob_converted_map_icon = view.findViewById(R.id.myjob_converted_map_icon);

            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.Tv_job_status.setText(data.get(position).getConvertedjob_status());
        holder.myjobconverted_bookingtimeTv.setText(data.get(position).getConverted_date());
        holder.myjobconverted_category.setText(data.get(position).getConverted_category());
        holder.myjobconverted_username.setText(data.get(position).getConverted_user_name());
        holder.myjobconverted_orderidTv.setText(data.get(position).getOrder_id());

        if (data.get(position).getAddress() != null) {
            if (data.get(position).getAddress().equalsIgnoreCase("")) {
//                holder.myjob_converted_map_icon.setVisibility(View.GONE);
                holder.myjobconverted_location_Tv.setText("");
            } else {
                holder.myjobconverted_location_Tv.setText(data.get(position).getAddress());
//                holder.myjob_converted_map_icon.setVisibility(View.VISIBLE);
            }
        } else {
//            holder.myjob_converted_map_icon.setVisibility(View.GONE);
            holder.myjobconverted_location_Tv.setText("");
        }

//        Picasso.with(context).load(String.valueOf(data.get(position).getConverted_user_image())).placeholder(R.drawable.nouserimg).memoryPolicy(MemoryPolicy.NO_CACHE).into(holder.myjobconverted_profile_img);

        return view;
    }

    public class ViewHolder {
        private TextView myjobconverted_location_Tv, myjobconverted_bookingtimeTv, Tv_job_status;
        private TextView myjobconverted_orderidTv, myjobconverted_username, myjobconverted_category;
//        private RoundedImageView myjobconverted_profile_img;
//        private ImageView myjob_converted_map_icon;
    }
}
