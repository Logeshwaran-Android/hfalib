package com.hoperlady.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.countrycodepicker.Country;
import com.hoperlady.R;


import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

public class RegisterListAdapter extends BaseAdapter {
    private Context context;
    List<Country> countries;
    LayoutInflater inflater;



    public RegisterListAdapter( Context context, List<Country> countries) {
        this.context=context;
        this.countries=countries;
        inflater=LayoutInflater.from(context);
    }

    public class ViewHolder
    {

            private TextView Code_TV;
            private ImageView Code_IV;

    }

    @Override
    public int getCount() {
        return countries.size();
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
        if(convertView==null)
        {
            view = inflater.inflate(R.layout.row,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.Code_TV = (TextView) view.findViewById(com.countrycodepicker.R.id.row_title);
            viewHolder.Code_IV = (ImageView) view.findViewById(com.countrycodepicker.R.id.row_icon);
            viewHolder.Code_IV.setVisibility(View.VISIBLE);
            view.setTag(viewHolder);

        }
        else
        {
            view=convertView;
            viewHolder=(ViewHolder)   view.getTag();

        }
        String drawableName = "flag_"
                + countries.get(position).getCode().toLowerCase(Locale.ENGLISH);
        viewHolder.Code_IV.setImageResource(getResId(drawableName));
        viewHolder.Code_TV.setText(countries.get(position).getName()+" ("+countries.get(position).getDialCode()+")");

        return view;
    }
    private int getResId(String drawableName) {

        try {
            Class<com.countrycodepicker.R.drawable> res = com.countrycodepicker.R.drawable.class;
            Field field = res.getField(drawableName);
            int drawableId = field.getInt(null);
            return drawableId;
        } catch (Exception e) {
            Log.e("CountryCodePicker", "Failure to get drawable id.", e);
        }
        return -1;
    }
}
