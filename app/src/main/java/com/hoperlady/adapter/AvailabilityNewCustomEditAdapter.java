package com.hoperlady.adapter;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hoperlady.Pojo.AvailabileArrayPojo;
import com.hoperlady.Pojo.EditAvailabileArrayPojo;
import com.hoperlady.Pojo.MyProfileEditAvailabilityPojo;
import com.hoperlady.Pojo.UpdateAvailabilityEditPojo;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.R;

import java.util.ArrayList;

public class AvailabilityNewCustomEditAdapter extends RecyclerView.Adapter<AvailabilityNewCustomEditAdapter.MyViewHolder> {

    private AppCompatActivity myContext;
    private ArrayList<MyProfileEditAvailabilityPojo> myAvailability;
    private boolean IsSelected = false;
    private Refresh refresh;
    private boolean wholeDaySelect = false;
    private SessionManager sessionManager;

    Dialog dialog;

    private ArrayList<UpdateAvailabilityEditPojo> arrayUpdateListCount = new ArrayList<UpdateAvailabilityEditPojo>();

    public AvailabilityNewCustomEditAdapter(AppCompatActivity aContext, ArrayList<MyProfileEditAvailabilityPojo> aAvailabilityList, Refresh refresh) {
        this.myContext = aContext;
        this.myAvailability = aAvailabilityList;
        this.refresh = refresh;
        sessionManager = new SessionManager(myContext);
    }

    public interface Refresh {
        void Update(ArrayList<MyProfileEditAvailabilityPojo> aAvailabilityList);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.availablity_list_single, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.titleDayNameTV.setText(myAvailability.get(position).getDayNames());
        myAvailability.get(position).setSelectedDay("false");
        holder.timeShowListTV.setText(myContext.getResources().getString(R.string.availability_new_custom_page_adapter_not_selected_availability_single));
        for (int i = 0; i < myAvailability.get(position).getPojoArrayList().size(); i++) {
            if (myAvailability.get(position).getPojoArrayList().get(i).getSelected().equals("true")) {
                holder.addAndEditNameTV.setText(myContext.getResources().getString(R.string.availability_new_custom_page_adapter_edit_single));
                myAvailability.get(position).setSelectedDay("true");
            }
            if (myAvailability.get(position).getWholeDayChoose().equals("true")) {
                holder.addAndEditNameTV.setText(myContext.getResources().getString(R.string.availability_new_custom_page_adapter_edit_single));
                myAvailability.get(position).setSelectedDay("true");
            }
        }

        StringBuffer Strtime = new StringBuffer();
        for (int i = 0; i < myAvailability.get(position).getPojoArrayList().size(); i++) {
            if (myAvailability.get(position).getPojoArrayList().get(i).getSelected().equals("true")) {
                Strtime.append(myAvailability.get(position).getPojoArrayList().get(i).getTime()).append(", ");
                holder.timeShowListTV.setText(Strtime);
            }
            if (myAvailability.get(position).getWholeDayChoose().equals("true")) {
                holder.timeShowListTV.setText(myContext.getResources().getString(R.string.wholeday));
            }
        }

        holder.daysRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayUpdateListCount = new ArrayList<UpdateAvailabilityEditPojo>();
                DisplayMetrics metrics = myContext.getResources().getDisplayMetrics();
                int screenWidth = (int) (metrics.widthPixels * 0.80);//fill only 80% of the screen
                View view = View.inflate(myContext, R.layout.edit_availability_childe_single, null);
                dialog = new Dialog(myContext);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(view);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.setOnKeyListener(new Dialog.OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface arg0, int keyCode,
                                         KeyEvent event) {
                        // TODO Auto-generated method stub
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            if (dialog != null) {
                                if (dialog.isShowing()) {
                                    if (sessionManager.getEditAvailablityDays() != null) {
                                        if (!sessionManager.getEditAvailablityDays().equals("")) {
                                            Gson gson = new Gson();
                                            String availabileStringPojo = sessionManager.getEditAvailablityDays();
                                            EditAvailabileArrayPojo mPojo = gson.fromJson(availabileStringPojo, EditAvailabileArrayPojo.class);
                                            System.out.println("available----------" + gson.fromJson(availabileStringPojo, AvailabileArrayPojo.class));
                                            myAvailability = mPojo.getPojoArrayList();
                                            refresh.Update(myAvailability);
                                        }
                                    }
                                    dialog.dismiss();
                                }
                            }
                        }
                        return true;
                    }
                });

                Window dialogWindow = dialog.getWindow();
                WindowManager.LayoutParams lp = dialogWindow.getAttributes();
                lp.y = 0;
                lp.x = 0;
                dialogWindow.setGravity(Gravity.BOTTOM);
                dialogWindow.setAttributes(lp);

                TextView titleDays = (TextView) view.findViewById(R.id.titleDays);
                TextView doneTv = (TextView) view.findViewById(R.id.doneTv);
                final TextView doNotClickTV = (TextView) view.findViewById(R.id.doNotClickTV);
                final ImageView wholeDayCB = (ImageView) view.findViewById(R.id.wholeDayCB);
                final LinearLayout available_view = (LinearLayout) view.findViewById(R.id.available_view);
                RelativeLayout backPressRL = (RelativeLayout) view.findViewById(R.id.register_header_back_layout);
                final RecyclerView hourAvailabileRV = view.findViewById(R.id.hourAvailabileRV);
                hourAvailabileRV.setLayoutManager(new GridLayoutManager(myContext, 3));

                titleDays.setText(myAvailability.get(holder.getAdapterPosition()).getDayNames());
                backPressRL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();

                        if (sessionManager.getEditAvailablityDays() != null) {
                            if (!sessionManager.getEditAvailablityDays().equals("")) {
                                Gson gson = new Gson();
                                String availabileStringPojo = sessionManager.getEditAvailablityDays();
                                EditAvailabileArrayPojo mPojo = gson.fromJson(availabileStringPojo, EditAvailabileArrayPojo.class);
                                System.out.println("available----------" + gson.fromJson(availabileStringPojo, AvailabileArrayPojo.class));
                                myAvailability = mPojo.getPojoArrayList();
                                refresh.Update(myAvailability);
                            }
                        }
                    }
                });

                doNotClickTV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                if (myAvailability.get(position).getWholeDayChoose().equals("true")) {
                    wholeDayCB.setImageDrawable(myContext.getResources().getDrawable(R.drawable.merge_tick_and_circle));
                    doNotClickTV.setVisibility(View.GONE);
                    hourAvailabileRV.setVisibility(View.GONE);
                    available_view.setVisibility(View.GONE);
                    wholeDaySelect = true;
                } else {
                    wholeDayCB.setImageDrawable(myContext.getResources().getDrawable(R.drawable.wholday_uncheck));
                    doNotClickTV.setVisibility(View.GONE);
                    hourAvailabileRV.setVisibility(View.VISIBLE);
                    available_view.setVisibility(View.VISIBLE);
                    wholeDaySelect = false;
                }

                wholeDayCB.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!wholeDaySelect) {
                            wholeDayCB.setImageDrawable(myContext.getResources().getDrawable(R.drawable.merge_tick_and_circle));
                            doNotClickTV.setVisibility(View.GONE);
                            hourAvailabileRV.setVisibility(View.GONE);
                            available_view.setVisibility(View.GONE);
                            wholeDaySelect = true;
                        } else {
                            wholeDayCB.setImageDrawable(myContext.getResources().getDrawable(R.drawable.wholday_uncheck));
                            doNotClickTV.setVisibility(View.GONE);
                            hourAvailabileRV.setVisibility(View.VISIBLE);
                            available_view.setVisibility(View.VISIBLE);
                            wholeDaySelect = false;
                        }
                    }
                });

                doneTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!wholeDaySelect) {
                            if (arrayUpdateListCount.size() > 0) {
                                for (int i = 0; i < arrayUpdateListCount.size(); i++) {
                                    myAvailability.get(position).getPojoArrayList().set(arrayUpdateListCount.get(i).getCountPosition(), arrayUpdateListCount.get(i).getMyProfileAvailabilityPojo());
                                    notifyDataSetChanged();
                                    refresh.Update(myAvailability);

                                    EditAvailabileArrayPojo pojo = new EditAvailabileArrayPojo();
                                    pojo.setPojoArrayList(myAvailability);
                                    sessionManager.setEditAvailablityDays(pojo);
                                }
                            }
                        }
                        myAvailability.get(position).setWholeDayChoose(wholeDaySelect + "");
                        notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                EditAvailabileArrayPojo pojo = new EditAvailabileArrayPojo();
                pojo.setPojoArrayList(myAvailability);
                sessionManager.setEditAvailablityDays(pojo);

                EditCustomAdapter customAdapter = new EditCustomAdapter(myContext, myAvailability.get(position).getPojoArrayList(), position, new EditCustomAdapter.Refresh() {
                    @Override
                    public void Update(ArrayList<UpdateAvailabilityEditPojo> arrayListCount) {
                        arrayUpdateListCount = new ArrayList<UpdateAvailabilityEditPojo>();
                        arrayUpdateListCount = arrayListCount;

                        if (arrayUpdateListCount.size() > 0) {
                            for (int i = 0; i < arrayUpdateListCount.size(); i++) {
                                myAvailability.get(position).getPojoArrayList().set(arrayUpdateListCount.get(i).getCountPosition(), arrayUpdateListCount.get(i).getMyProfileAvailabilityPojo());
                                notifyDataSetChanged();
                                refresh.Update(myAvailability);
                            }
                        }
                    }
                });
                hourAvailabileRV.setAdapter(customAdapter);

                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return myAvailability.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titleDayNameTV, timeShowListTV, addAndEditNameTV;
        RelativeLayout daysRL;

        public MyViewHolder(View itemView) {
            super(itemView);
            titleDayNameTV = (TextView) itemView.findViewById(R.id.titleDayNameTV);
            timeShowListTV = (TextView) itemView.findViewById(R.id.timeShowListTV);
            addAndEditNameTV = (TextView) itemView.findViewById(R.id.addAndEditNameTV);
            daysRL = (RelativeLayout) itemView.findViewById(R.id.daysRL);
        }
    }

}

