package com.hoperlady.adapter;

import android.content.Context;
import android.graphics.Point;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.constraintlayout.widget.Guideline;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.hoperlady.Pojo.WeekTaskBarChartPojo;
import com.hoperlady.R;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class WeekTaskBarChartAdapter extends RecyclerView.Adapter<WeekTaskBarChartAdapter.MyViewHolder> {
    Context aContext;
    ArrayList<WeekTaskBarChartPojo> aData;
    Float guideLineSpaceValue;
    boolean isSelected = false;
    Refresh aRefresh;
    String aMaxValue = "";

    public WeekTaskBarChartAdapter(Context context, ArrayList<WeekTaskBarChartPojo> arrayList, String max, Refresh refresh) {
        aContext = context;
        aData = arrayList;
        aRefresh = refresh;
        aMaxValue = max;
    }

    public interface Refresh {
        void UpdateSelected();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.earnings_line_layout_single, parent, false);
        v.getLayoutParams().width = (int) ((getScreenWidth() - aContext.getResources().getDimension(R.dimen._30sdp)) / 7); /// THIS LINE WILL DIVIDE OUR VIEW INTO NUMBERS OF PARTS
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        Calendar c = Calendar.getInstance();
        c.get(Calendar.MONTH);
        String date = aData.get(position).getJob_date();
        holder.monthNameTV.setText(String.format(Locale.US, "%tB", c));
        holder.dayTv.setText(date.split("-")[2]);
        String amount = aData.get(position).getEarnings();
        float dividValues = (Float.valueOf(aMaxValue) / 4.0f);
        guideLineSpaceValue = 0.9f - Float.valueOf(amount) / (Float.valueOf(aMaxValue) + dividValues);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(holder.constraintLayout);
        constraintSet.setGuidelinePercent(R.id.height_guideline, guideLineSpaceValue);
        constraintSet.connect(R.id.seperator_view2, ConstraintSet.TOP, R.id.height_guideline, ConstraintSet.TOP, 0);
        constraintSet.applyTo(holder.constraintLayout);

        holder.view_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("*********************onBindViewHolder" + position);

                aData.get(position).setBarLineSelected(true);
                for (int i = 0; i < aData.size(); i++) {
                    if (i != position) {
                        aData.get(i).setBarLineSelected(false);
                    }
                }
                aRefresh.UpdateSelected();

                notifyDataSetChanged();
            }
        });
        if (aData.get(position).isBarLineSelected()) {
            holder.blackLineView.setVisibility(View.VISIBLE);
            holder.view_test.setBackground(aContext.getResources().getDrawable(R.drawable.earning_lines));
        } else {
            holder.blackLineView.setVisibility(View.GONE);
            holder.view_test.setBackground(aContext.getResources().getDrawable(R.drawable.earning_lines_gray));
        }
    }

    @Override
    public int getItemCount() {
        return aData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView dayTv, monthNameTV;
        ConstraintLayout constraintLayout;
        View seperatorView, view_test, blackLineView;
        Guideline height_guideLine;

        public MyViewHolder(View itemView) {
            super(itemView);
            dayTv = (TextView) itemView.findViewById(R.id.day_tv);
            monthNameTV = (TextView) itemView.findViewById(R.id.monthNameTV);
            constraintLayout = (ConstraintLayout) itemView.findViewById(R.id.constraint_layout);
            seperatorView = (View) itemView.findViewById(R.id.seperator_view);
            view_test = (View) itemView.findViewById(R.id.seperator_view2);
            blackLineView = (View) itemView.findViewById(R.id.black_line_view);
            height_guideLine = (Guideline) itemView.findViewById(R.id.height_guideline);
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
