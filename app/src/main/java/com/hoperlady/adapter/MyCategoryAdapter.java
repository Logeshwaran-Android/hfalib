package com.hoperlady.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hoperlady.Pojo.ProviderCategory;
import com.hoperlady.Utils.CurrencySymbolConverter;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user145 on 10/4/2017.
 */
public class MyCategoryAdapter extends RecyclerView.Adapter<MyCategoryAdapter.ViewHolder> {

    private ArrayList<ProviderCategory> data;
    private Context context;
    private SessionManager sessionManager;
    private int count = 0;

    public MyCategoryAdapter(Context context, ArrayList<ProviderCategory> data) {
        this.context = context;
        this.data = data;
        sessionManager = new SessionManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_single, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HashMap<String, String> aAmountMap = sessionManager.getWalletDetails();
        String aCurrencyCode = aAmountMap.get(SessionManager.KEY_CURRENCY_CODE);
        final String myCurrencySymbol = CurrencySymbolConverter.getCurrencySymbol(aCurrencyCode);

        if (data.size() > position) {
            holder.categoryNameTv.setText(data.get(position).getCategory_name());
            holder.categoryNameTv.setSelected(true);
            holder.categoryCurrencyTv.setText(myCurrencySymbol);
            holder.categoryCostTv.setText(data.get(position).getHourly_rate());
            Picasso.with(context).load(data.get(position).getCategoryIcon()).placeholder(R.drawable.nouserimg).into(holder.categoriesIconIV);
            if (data.get(position).getCategoryType().equals("1")) {
                holder.categoryTypeTv.setText("");
            } else {
                holder.categoryTypeTv.setText("/hr");
            }

            if (count == 0) {
                holder.background_curve_RL.getBackground().setColorFilter(context.getResources().getColor(R.color.category_bg_item1_color), PorterDuff.Mode.SRC_ATOP);
                holder.designLL.setBackgroundResource(R.drawable.category_curve_bg);
                holder.design2LL.setBackgroundResource(R.drawable.category_bg_design);
                count = 1;
            } else if (count == 1) {
                holder.background_curve_RL.getBackground().setColorFilter(context.getResources().getColor(R.color.category_bg_item2_color), PorterDuff.Mode.SRC_ATOP);
                holder.designLL.setBackgroundResource(R.drawable.category_curve_bg);
                holder.design2LL.setBackgroundResource(R.drawable.categorytwo_bg_design);
                count = 2;
            } else if (count == 2) {
                holder.background_curve_RL.getBackground().setColorFilter(context.getResources().getColor(R.color.category_bg_item3_color), PorterDuff.Mode.SRC_ATOP);
                holder.designLL.setBackgroundResource(R.drawable.category_curve_bg);
                count = 0;
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryNameTv;
        private TextView categoryCurrencyTv, categoryCostTv, categoryTypeTv;
        private RelativeLayout background_curve_RL;
        private LinearLayout designLL, design2LL;
        private ImageView categoriesIconIV;

        public ViewHolder(View itemView) {
            super(itemView);
            categoryNameTv = (TextView) itemView.findViewById(R.id.categoryNameTv);

            categoryCostTv = (TextView) itemView.findViewById(R.id.categoryCostTv);
            categoryCurrencyTv = (TextView) itemView.findViewById(R.id.categoryCurrencyTv);
            categoryTypeTv = (TextView) itemView.findViewById(R.id.categoryTypeTv);

            background_curve_RL = (RelativeLayout) itemView.findViewById(R.id.background_curve_RL);
            designLL = (LinearLayout) itemView.findViewById(R.id.designLL);
            design2LL = (LinearLayout) itemView.findViewById(R.id.design2LL);

            categoriesIconIV = (ImageView) itemView.findViewById(R.id.categoriesIconIV);
        }
    }
}