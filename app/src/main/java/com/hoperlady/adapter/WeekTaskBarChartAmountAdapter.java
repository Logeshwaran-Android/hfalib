package com.hoperlady.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.hoperlady.Pojo.WeekTaskBarChartPojo;
import com.hoperlady.R;
import com.hoperlady.Utils.CurrencySymbolConverter;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.app.DayTaskActivity;


import java.util.ArrayList;
import java.util.HashMap;

public class WeekTaskBarChartAmountAdapter extends RecyclerView.Adapter<WeekTaskBarChartAmountAdapter.MyViewHolder> {
    private Context aContext;
    private ArrayList<WeekTaskBarChartPojo> aData;
    private SessionManager sessionManager;
    private String myCurrencySymbole = "";

    public WeekTaskBarChartAmountAdapter(Context context, ArrayList<WeekTaskBarChartPojo> arrayList) {
        aContext = context;
        aData = arrayList;
        sessionManager = new SessionManager(aContext);
        HashMap<String, String> tasker = sessionManager.getWalletDetails();
        String currencyCode = tasker.get(SessionManager.KEY_CURRENCY_CODE);
        myCurrencySymbole = CurrencySymbolConverter.getCurrencySymbol(currencyCode);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.earnings_amount_line_layout_single, parent, false);
        v.getLayoutParams().width = (int) ((getScreenWidth() - aContext.getResources().getDimension(R.dimen._30sdp)) / 7); /// THIS LINE WILL DIVIDE OUR VIEW INTO NUMBERS OF PARTS
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        String content = aData.get(position).getEarnings();
        holder.amountTV.setText(myCurrencySymbole + content);
        holder.amountTV.setSelected(true);

        if (aData.get(position).isBarLineSelected()) {
            holder.blackLineView.setVisibility(View.VISIBLE);
            holder.amountTV.setVisibility(View.VISIBLE);
        } else {
            holder.blackLineView.setVisibility(View.INVISIBLE);
            holder.amountTV.setVisibility(View.INVISIBLE);
        }

        holder.amountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!aData.get(position).getTask_count().equalsIgnoreCase("0")) {
                    Intent intent = new Intent(aContext, DayTaskActivity.class);
                    intent.putExtra("date", aData.get(position).getJob_date());
                    intent.putExtra("task_count", aData.get(position).getTask_count());
                    intent.putExtra("total_earnings", aData.get(position).getEarnings());
                    aContext.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return aData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView amountTV;
        ConstraintLayout constraintLayout;
        View blackLineView;

        public MyViewHolder(View itemView) {
            super(itemView);
            amountTV = (TextView) itemView.findViewById(R.id.amountTV);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.constraint_layout);

            blackLineView = (View) itemView.findViewById(R.id.black_line_view);
        }
    }

    public int getScreenWidth() {
        WindowManager wm = (WindowManager) aContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
}
