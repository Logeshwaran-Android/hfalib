package com.hoperlady.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hoperlady.R;
import com.pkmmte.view.CircularImageView;
import com.hoperlady.Pojo.Category_Pojo;


import java.util.ArrayList;


public class Category_Adapter extends BaseAdapter {
    ArrayList<Category_Pojo> Category_ArrayList;
    Context context;
    LayoutInflater layoutInflater;

    public Category_Adapter(Context context, ArrayList<Category_Pojo> Category_ArrayList) {
        this.context = context;
        this.Category_ArrayList = Category_ArrayList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return Category_ArrayList.size();
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
            view = layoutInflater.inflate(R.layout.category_inflate_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.Category_Image = (CircularImageView) view.findViewById(R.id.Category_round_imageView);

            viewHolder.Category_Name = (TextView) view.findViewById(R.id.Category_TV);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.Category_Name.setText(Category_ArrayList.get(position).getCategoryName());
        //Picasso.with(context).load(Category_ArrayList.get(position).getCategory_Image()).error(R.drawable.placeholder_icon).placeholder(R.drawable.placeholder_icon).memoryPolicy(MemoryPolicy.NO_CACHE).into(viewHolder.Category_Image);

        return view;

    }


    public class ViewHolder {
        TextView Category_Name;
        CircularImageView Category_Image;
    }
}
