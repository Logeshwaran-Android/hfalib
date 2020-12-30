package com.hoperlady.adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hoperlady.Pojo.PaymentFareSummeryPojo;
import com.hoperlady.R;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user146 on 10/4/2016.
 */
public class MyTaskDetailsFareSummeryAdapter extends RecyclerView.Adapter<MyTaskDetailsFareSummeryAdapter.ViewHolder> {

    private ArrayList<PaymentFareSummeryPojo> data;
    private LayoutInflater mInflater;
    private AppCompatActivity context;
    private String check;

    public MyTaskDetailsFareSummeryAdapter(AppCompatActivity c, ArrayList<PaymentFareSummeryPojo> d) {
        context = c;
        mInflater = LayoutInflater.from(context);
        data = d;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.payment_fare_summery_single, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String dt = data.get(position).getDt();
        holder.Tv_payment_title.setText(data.get(position).getPayment_title());
        if (dt.equalsIgnoreCase("0")) {
            String amountCaps = capitalize(data.get(position).getPayment_amount());
            holder.Tv_payment_amount.setText(amountCaps);
        } else {
            String sourceString = " <font color=" + context.getResources().getColor(R.color.appmain_color) + ">" + data.get(position).getCurrencycode() + "</font>" + data.get(position).getPayment_amount();
            holder.Tv_payment_amount.setText(Html.fromHtml(sourceString));
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView Tv_payment_title, Tv_payment_amount;

        public ViewHolder(View view) {
            super(view);
            Tv_payment_title = (TextView) view.findViewById(R.id.payment_fare_title_textView);
            Tv_payment_amount = (TextView) view.findViewById(R.id.payment_fare_cost_textView);
        }
    }

    private String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

}
