package com.hoperlady.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hoperlady.Pojo.SavedCategoryPojo;
import com.hoperlady.R;
import com.hoperlady.app.RegisterPageFourth;


import java.util.ArrayList;

public class SavedCategoryAdapter extends BaseAdapter {
    ArrayList<SavedCategoryPojo> arrayList;
    Context context;
    LayoutInflater layoutInflater;

    public SavedCategoryAdapter(Context context, ArrayList<SavedCategoryPojo> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        layoutInflater = LayoutInflater.from(context);

    }


    @Override
    public int getCount() {
        return arrayList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.saved_category_inflate_layout, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.CategoryName = (TextView) view.findViewById(R.id.Category_TV);
            viewHolder.subCategory_TV = (TextView) view.findViewById(R.id.subCategory_TV);
            viewHolder.DeleteCategory = (ImageView) view.findViewById(R.id.DeleteCategory);
            viewHolder.EditCategory = (ImageView) view.findViewById(R.id.EditCategory);
            view.setTag(viewHolder);

        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.CategoryName.setText(arrayList.get(position).getCategoryName());
        viewHolder.subCategory_TV.setText(arrayList.get(position).getSubCategoryName());
        viewHolder.DeleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterPageFourth.UpdateListview(position, context);
            }
        });
        viewHolder.EditCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterPageFourth.EditCategory(position,context);
            }
        });

        return view;
    }

    public class ViewHolder {
        private TextView CategoryName, subCategory_TV;
        private ImageView DeleteCategory, EditCategory;
    }
}
