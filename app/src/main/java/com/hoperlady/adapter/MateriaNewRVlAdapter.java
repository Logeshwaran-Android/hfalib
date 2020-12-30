package com.hoperlady.adapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.hoperlady.Pojo.Addmaterialpojo;
import com.hoperlady.R;

import java.util.ArrayList;

public class MateriaNewRVlAdapter extends RecyclerView.Adapter<MateriaNewRVlAdapter.MyViewHolder> {

    private AppCompatActivity myContext;
    private ArrayList<Addmaterialpojo> data;
    private String myCurrencySymbol = "";
    private RefreshDeleteArray aRefresh;
    private boolean isPerformClickedName = false;
    private boolean isPerformClickedCost = false;

    public MateriaNewRVlAdapter(AppCompatActivity aContext, ArrayList<Addmaterialpojo> addmaterialpojos, String mycurrencysymbol, RefreshDeleteArray refresh) {
        this.myContext = aContext;
        this.data = addmaterialpojos;
        this.myCurrencySymbol = mycurrencysymbol;
        this.aRefresh = refresh;
    }

    public interface RefreshDeleteArray {
        void Update(int position);

        void UpdateEditName(int position, String name, boolean isPerformClickedName);

        void UpdateEditCost(int position, String cost, boolean isPerformClickedCost);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.material_items_layout_single, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.toolCurrencySymbolTV.setText(myCurrencySymbol);
        holder.deleteMeterialTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aRefresh.Update(position);
            }
        });

        if (data.size() > 0) {
            String edit_tool = data.get(position).getToolname();
            String edit_cost = data.get(position).getToolcost();
            if (edit_tool.equalsIgnoreCase("") && edit_cost.equalsIgnoreCase("")) {
                edit_tool = "";
                edit_cost = "";
            }
            try {
                if (data.get(position).getValuesPosition() == position) {
                    isPerformClickedName = false;
                    isPerformClickedCost = false;
                    holder.toolNameET.setText(edit_tool);
                    holder.toolNameET.setCursorVisible(true);
                    holder.toolCostET.setText(edit_cost);
                    holder.toolCostET.setCursorVisible(true);
                    holder.toolNameET.requestFocus();
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        holder.toolNameET.addTextChangedListener(new Textchange(position, holder));
        holder.toolCostET.addTextChangedListener(new Costchange(position, holder));


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView deleteMeterialTV, toolCurrencySymbolTV;
        EditText toolNameET, toolCostET;

        public MyViewHolder(View itemView) {
            super(itemView);
            deleteMeterialTV = (TextView) itemView.findViewById(R.id.deleteMeterialTV);
            toolCurrencySymbolTV = (TextView) itemView.findViewById(R.id.toolCurrencySymbolTV);
            toolNameET = (EditText) itemView.findViewById(R.id.toolNameET);
            toolCostET = (EditText) itemView.findViewById(R.id.toolCostET);
        }
    }

    private class Textchange implements TextWatcher {

        int position = 0;
        MyViewHolder holder;

        public Textchange(int aPosition, MyViewHolder holder) {
            position = aPosition;
            this.holder = holder;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String toolname = holder.toolNameET.getText().toString();
//            item_add.get(position).setToolname(toolname);
            aRefresh.UpdateEditName(position, toolname, isPerformClickedName);
        }
    }

    private class Costchange implements TextWatcher {
        int position = 0;
        MyViewHolder holder;

        public Costchange(int aPosition, MyViewHolder holder) {
            position = aPosition;
            this.holder = holder;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String toolcost = holder.toolCostET.getText().toString();
//            item_add.get(position).setToolcost(toolcost);
            aRefresh.UpdateEditCost(position, toolcost, isPerformClickedCost);
        }
    }


}
