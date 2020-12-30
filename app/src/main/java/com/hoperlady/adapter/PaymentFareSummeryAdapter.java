package com.hoperlady.adapter;

import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hoperlady.Pojo.PaymentFareSummeryPojo;
import com.hoperlady.R;

import java.util.ArrayList;

/**
 * Created by user88 on 12/30/2015.
 */
public class PaymentFareSummeryAdapter extends BaseAdapter {

    private ArrayList<PaymentFareSummeryPojo> data;
    private LayoutInflater mInflater;
    private AppCompatActivity context;
    private String check;

    public PaymentFareSummeryAdapter(AppCompatActivity c, ArrayList<PaymentFareSummeryPojo> d) {
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
        private TextView Tv_payment_title,Tv_payment_amount;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
      ViewHolder holder;

        if (convertView==null){
            view = mInflater.inflate(R.layout.payment_fare_summery_single, parent, false);
            holder = new ViewHolder();

            holder.Tv_payment_title = (TextView)view.findViewById(R.id.payment_fare_title_textView);
            holder.Tv_payment_amount = (TextView)view.findViewById(R.id.payment_fare_cost_textView);

            view.setTag(holder);

        }else{
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }

        holder.Tv_payment_title.setText(data.get(position).getPayment_title());
        holder.Tv_payment_amount.setText(data.get(position).getPayment_amount());
        return view;
    }
}
