package com.hoperlady.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hoperlady.Pojo.Experiancepojo;
import com.hoperlady.R;

import java.util.ArrayList;

public class ExperianceAdapter extends BaseAdapter {
    Context context;
    ArrayList<Experiancepojo> arrayList;
    LayoutInflater layoutInflater;
    ListView listView;
    final int MAX_WORDS = 500;
    String sAnsewer = "";

    public ExperianceAdapter(Context context, ArrayList<Experiancepojo> arrayList, ListView lst) {
        this.context = context;
        this.arrayList = arrayList;
        this.listView = lst;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.inflate_add_experiance_single, parent, false);
            holder = new ViewHolder();
            holder.about_experiance_text = (TextView) view.findViewById(R.id.about_experiance_text);
            holder.edt_about_experiance = (EditText) view.findViewById(R.id.edt_about_experiance);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        holder.about_experiance_text.setText(arrayList.get(position).getQuestion());
        holder.edt_about_experiance.setHint(arrayList.get(position).getQuestion());
        sAnsewer = holder.edt_about_experiance.getText().toString();
        arrayList.get(position).setAnswer(sAnsewer);

        holder.edt_about_experiance.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // Nothing
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String[] words = s.toString().split(" "); // Get all words
                if (count>=500) {
                    Toast.makeText(context,"Only 500 words allowed ...",Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }

    public ArrayList<String> getAnswer() {
        ArrayList<String> tst = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            View view = listView.getChildAt(i);
            EditText edt_new = (EditText) view.findViewById(R.id.edt_about_experiance);
            tst.add(edt_new.getText().toString());
        }
        return tst;
    }

    public class ViewHolder {
        TextView about_experiance_text;
        EditText edt_about_experiance;


    }


}

