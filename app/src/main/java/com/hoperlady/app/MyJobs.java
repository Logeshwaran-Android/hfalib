package com.hoperlady.app;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ParseException;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.hoperlady.Fragment.MyJob_Cancelled_Fragment;
import com.hoperlady.Fragment.MyJob_Converted_Fragment;
import com.hoperlady.Fragment.MyJob_OnGoing_Fragment;
import com.hoperlady.R;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.hockeyapp.ActionBarActivityHockeyApp;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import core.Dialog.LoadingDialog;
import core.Dialog.PkDialog;
import core.Dialog.PkLoadingDialog;
import core.Volley.ServiceRequest;
import core.socket.ChatMessageService;
import core.socket.SocketHandler;

/**
 * Created by user88 on 12/11/2015.
 */
public class MyJobs extends ActionBarActivityHockeyApp {

    public static AppCompatActivity Myjobs_Activity;
    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
    boolean select_filter = false;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private RelativeLayout layout_myjob_back;
    private Dialog sort_dialog;
    private boolean isReasonAvailable = false;
    private ImageView Img_filter;
    private Context context;
    private RelativeLayout sorting_cancel, sorting_apply, sortingDate, sortingname, Ascending_orderby, Descending_Orderby;
    private ImageView sorting_checkename, sorting_checkeddate, sorting_checkedthree, sorting_ascendingimg, sorting_descinngimg;
    private String Sselected_sorting = "";
    private String Sselected_ordrby = "";
    private RelativeLayout today_booking, recent_booking, upcoming_booking;
    private ImageView today_booking_image, recent_booking_image, upcoming_booking_image;
    private String Filter_booking_type = "0";
    private String Filter_type = "no";
    private String Filter_completed = "0";
    private String Filter_cancelled = "0";
    private ServiceRequest mRequest;
    private SessionManager session;
    private RelativeLayout Rl_layoyt_datefrom, Rl_layout_to;
    private TextView Tv_fromdaate, Tv_todate;
    private String seleceteddate = "1";
    private CaldroidFragment dialogCaldroidFragment;
    // Setup CaldroidListener
    final CaldroidListener caldroidListener = new CaldroidListener() {
        @Override
        public void onSelectDate(Date date, View view) {
            dialogCaldroidFragment.dismiss();
            // Tvto_date.setText(getResources().getString(R.string.appointment_label_select_time));

            if (seleceteddate.equalsIgnoreCase("1")) {
                Tv_fromdaate.setText(formatter.format(date));
            } else {
                Tv_todate.setText(formatter.format(date));
            }
        }

        @Override
        public void onChangeMonth(int month, int year) {
            String text = "month: " + month + " year: " + year;
        }

        @Override
        public void onLongClickDate(Date date, View view) {
        }

        @Override
        public void onCaldroidViewCreated() {
        }
    };
    private Context mContext;
    private PkLoadingDialog mLoadingDialog;
    private String provider_id = "";
    private String Stype = "2";
    private LoadingDialog dialog;
    private RelativeLayout Rl_layout_filter;
    private SocketHandler socketHandler;
    private String page_selected = "1";
    private String status_page = "";
    //    private DrawerLayout drawer_layout;
    private RelativeLayout drawer;
    private Bundle savedInstanceState;
    private RefreshReceiver finishReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myjobs);
        Myjobs_Activity = MyJobs.this;
        context = MyJobs.this;
        if (MyJobs_OnGoingDetailPage.job_page_activity != null) {
            MyJobs_OnGoingDetailPage.job_page_activity.finish();
        }
        Intent i = getIntent();
        status_page = i.getExtras().getString("status");
        System.out.println("Status_page : " + status_page);
        socketHandler = SocketHandler.getInstance(this);
//        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer = (RelativeLayout) findViewById(R.id.drawer);
//        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer_layout, R.string.app_name, R.string.app_name);
//        drawer_layout.setDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        layout_myjob_back = (RelativeLayout) findViewById(R.id.layout_back_myjob);
        setupViewPager(viewPager);
        Rl_layout_filter = (RelativeLayout) findViewById(R.id.filter_layout);
        session = new SessionManager(MyJobs.this);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        finishReceiver = new RefreshReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.avail.finish");
        registerReceiver(finishReceiver, intentFilter);

        HashMap<String, String> user = session.getUserDetails();
        provider_id = user.get(SessionManager.KEY_PROVIDERID);


        layout_myjob_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        Rl_layout_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // chooseSortingImage(savedInstanceState);
                Filterinitialize();
            }
        });


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                // Check if this is the page you want.
                System.out.println("----------view pager position---------------" + position);
                if (position == 0) {
                    Stype = "2";
                    page_selected = "1";
                } else if (position == 1) {
                    Stype = "4";
                    page_selected = "2";
                } else if (position == 2) {
                    Stype = "5";
                    page_selected = "3";
                }
            }
        });
    }

    private void Filterinitialize() {
        final TextView name_text = (TextView) findViewById(R.id.name_text);
        final TextView date_text = (TextView) findViewById(R.id.date_text);
        TextView today_booking_text = (TextView) findViewById(R.id.today_booking_text);
        TextView upcoming_booking_text = (TextView) findViewById(R.id.upcoming_booking_text);
        TextView recent_booking_text = (TextView) findViewById(R.id.recent_booking_text);
        sorting_cancel = (RelativeLayout) findViewById(R.id.cancel_sorting_clearlayout);
        sorting_apply = (RelativeLayout) findViewById(R.id.sorting_apply_layout);
        sortingDate = (RelativeLayout) findViewById(R.id.subcategories_sorting_date_layout);
        Ascending_orderby = (RelativeLayout) findViewById(R.id.subcategories_sorting_ascending_layout);
        Descending_Orderby = (RelativeLayout) findViewById(R.id.subcategories_sorting_descending_layout);
        sortingname = (RelativeLayout) findViewById(R.id.subcategories_sortingname_layout);
        today_booking = (RelativeLayout) findViewById(R.id.today_booking);
        recent_booking = (RelativeLayout) findViewById(R.id.recent_booking);
        upcoming_booking = (RelativeLayout) findViewById(R.id.upcoming_booking);

        Tv_fromdaate = (TextView) findViewById(R.id.from_date_select_textView_myjobs);
        Tv_todate = (TextView) findViewById(R.id.todate_select_textViewmyjobs);
        Rl_layoyt_datefrom = (RelativeLayout) findViewById(R.id.myjooobsfrom_page_date_select_layout);
        Rl_layout_to = (RelativeLayout) findViewById(R.id.myjobstodate_select_layout);

        sorting_checkeddate = (ImageView) findViewById(R.id.sorting_checkedate);
        // sorting_checkedthree=(ImageView)sort_dialog.findViewById(R.id.checkedthree);
        sorting_checkename = (ImageView) findViewById(R.id.subcategories_sorting_checkename);
        sorting_ascendingimg = (ImageView) findViewById(R.id.subcategories_ascendingsorting_ascending);
        sorting_descinngimg = (ImageView) findViewById(R.id.checkeddescending);
        today_booking_image = (ImageView) findViewById(R.id.today_booking_image);
        recent_booking_image = (ImageView) findViewById(R.id.recent_booking_image);
        upcoming_booking_image = (ImageView) findViewById(R.id.upcoming_booking_image);

        sorting_checkename.setVisibility(View.GONE);
        sorting_checkeddate.setVisibility(View.GONE);
        sorting_ascendingimg.setVisibility(View.GONE);
        sorting_descinngimg.setVisibility(View.GONE);
        today_booking_image.setVisibility(View.GONE);
        recent_booking_image.setVisibility(View.GONE);
        upcoming_booking_image.setVisibility(View.GONE);

        today_booking.setEnabled(true);
        recent_booking.setEnabled(true);
        upcoming_booking.setEnabled(true);
        sortingname.setEnabled(true);
        sortingDate.setEnabled(true);
        Rl_layoyt_datefrom.setEnabled(true);
        Rl_layout_to.setEnabled(true);
        today_booking.setEnabled(true);
        recent_booking.setEnabled(true);
        upcoming_booking.setEnabled(true);

        Filter_type = "No";

        if (page_selected.equalsIgnoreCase("2") || page_selected.equalsIgnoreCase("3")) {
            today_booking.setEnabled(false);
            recent_booking.setEnabled(false);
            upcoming_booking.setEnabled(false);
            today_booking_text.setTextColor(Color.parseColor("#DCDCDC"));
            recent_booking_text.setTextColor(Color.parseColor("#DCDCDC"));
            upcoming_booking_text.setTextColor(Color.parseColor("#DCDCDC"));

        }
//        drawer_layout.openDrawer(drawer);

        sorting_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                drawer_layout.closeDrawer(drawer);
            }
        });

        Rl_layoyt_datefrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                seleceteddate = "1";
                datePicker(savedInstanceState);
            }
        });


        Rl_layout_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleceteddate = "2";
                datePicker(savedInstanceState);
            }
        });

        sortingname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sorting_checkename.setVisibility(View.VISIBLE);
                sorting_checkeddate.setVisibility(View.GONE);

                Sselected_sorting = "name";
            }
        });

        sortingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sorting_checkename.setVisibility(View.GONE);
                sorting_checkeddate.setVisibility(View.VISIBLE);

                Sselected_sorting = "date";
            }
        });

        Ascending_orderby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sorting_descinngimg.setVisibility(View.GONE);
                sorting_ascendingimg.setVisibility(View.VISIBLE);

                Sselected_ordrby = "-1";
            }
        });


        Descending_Orderby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sorting_descinngimg.setVisibility(View.VISIBLE);
                sorting_ascendingimg.setVisibility(View.GONE);

                Sselected_ordrby = "1";
            }
        });
        today_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                today_booking_image.setVisibility(View.VISIBLE);
                recent_booking_image.setVisibility(View.GONE);
                upcoming_booking_image.setVisibility(View.GONE);
                Filter_booking_type = "1";
                Filter_type = "today";
                sortingname.setEnabled(false);
                sortingDate.setEnabled(false);
                Rl_layoyt_datefrom.setEnabled(false);
                Rl_layout_to.setEnabled(false);
                sorting_checkename.setVisibility(View.GONE);
                sorting_checkeddate.setVisibility(View.GONE);
                name_text.setTextColor(Color.parseColor("#DCDCDC"));
                date_text.setTextColor(Color.parseColor("#DCDCDC"));
                Tv_fromdaate.setTextColor(Color.parseColor("#DCDCDC"));
                Tv_todate.setTextColor(Color.parseColor("#DCDCDC"));
                select_filter = true;
            }
        });
        recent_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recent_booking_image.setVisibility(View.VISIBLE);
                today_booking_image.setVisibility(View.GONE);
                upcoming_booking_image.setVisibility(View.GONE);
                Filter_booking_type = "1";
                Filter_type = "recent";
                sortingname.setEnabled(false);
                sortingDate.setEnabled(false);
                Rl_layoyt_datefrom.setEnabled(false);
                Rl_layout_to.setEnabled(false);
                sorting_checkename.setVisibility(View.GONE);
                sorting_checkeddate.setVisibility(View.GONE);
                name_text.setTextColor(Color.parseColor("#DCDCDC"));
                date_text.setTextColor(Color.parseColor("#DCDCDC"));
                Tv_fromdaate.setTextColor(Color.parseColor("#DCDCDC"));
                Tv_todate.setTextColor(Color.parseColor("#DCDCDC"));
                select_filter = true;
            }
        });
        upcoming_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upcoming_booking_image.setVisibility(View.VISIBLE);
                today_booking_image.setVisibility(View.GONE);
                recent_booking_image.setVisibility(View.GONE);
                Filter_booking_type = "1";
                Filter_type = "upcoming";
                sortingname.setEnabled(false);
                sortingDate.setEnabled(false);
                Rl_layoyt_datefrom.setEnabled(false);
                Rl_layout_to.setEnabled(false);
                sorting_checkename.setVisibility(View.GONE);
                sorting_checkeddate.setVisibility(View.GONE);
                name_text.setTextColor(Color.parseColor("#DCDCDC"));
                date_text.setTextColor(Color.parseColor("#DCDCDC"));
                Tv_fromdaate.setTextColor(Color.parseColor("#DCDCDC"));
                Tv_todate.setTextColor(Color.parseColor("#DCDCDC"));
                select_filter = true;
            }
        });

        sorting_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!select_filter) {
                    if (Tv_fromdaate.getText().toString().equalsIgnoreCase("From") || Tv_todate.getText().toString().equalsIgnoreCase("To")) {
                        Alert("Sorry", "Please Enter the From and To Date");
                    } else {
                        sortedMethode();
                    }
                } else {
                    sortedMethode();
                    select_filter = false;
                }
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyJob_Converted_Fragment(), getResources().getString(R.string.my_jobs_Completed));
        adapter.addFragment(new MyJob_OnGoing_Fragment(), getResources().getString(R.string.my_jobs_Open));
        adapter.addFragment(new MyJob_Cancelled_Fragment(), getResources().getString(R.string.my_jobs_Cancelled));
        viewPager.setAdapter(adapter);
        if (status_page.equalsIgnoreCase("cancelled")) {
            viewPager.setCurrentItem(3);
        }
    }

    //--------------Alert Method-----------
    private void Alert(String title, String message) {
        final PkDialog mDialog = new PkDialog(MyJobs.this);
        mDialog.setDialogTitle(title);
        mDialog.setDialogMessage(message);
        mDialog.setPositiveButton(getResources().getString(R.string.action_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void chooseSortingImage(final Bundle savedInstanceState) {
        sort_dialog = new Dialog(MyJobs.this);
        sort_dialog.getWindow();
        sort_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sort_dialog.setContentView(R.layout.sorting_layout);
        sort_dialog.setCanceledOnTouchOutside(true);
        sort_dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_photo_Picker;
        sort_dialog.show();
        sort_dialog.getWindow().setGravity(Gravity.CENTER);
        final TextView name_text = (TextView) sort_dialog.findViewById(R.id.name_text);
        final TextView date_text = (TextView) sort_dialog.findViewById(R.id.date_text);
        TextView today_booking_text = (TextView) sort_dialog.findViewById(R.id.today_booking_text);
        TextView upcoming_booking_text = (TextView) sort_dialog.findViewById(R.id.upcoming_booking_text);
        TextView recent_booking_text = (TextView) sort_dialog.findViewById(R.id.recent_booking_text);
        sorting_cancel = (RelativeLayout) sort_dialog.findViewById(R.id.cancel_sorting_clearlayout);
        sorting_apply = (RelativeLayout) sort_dialog.findViewById(R.id.sorting_apply_layout);
        sortingDate = (RelativeLayout) sort_dialog.findViewById(R.id.subcategories_sorting_date_layout);
        Ascending_orderby = (RelativeLayout) sort_dialog.findViewById(R.id.subcategories_sorting_ascending_layout);
        Descending_Orderby = (RelativeLayout) sort_dialog.findViewById(R.id.subcategories_sorting_descending_layout);
        sortingname = (RelativeLayout) sort_dialog.findViewById(R.id.subcategories_sortingname_layout);
        today_booking = (RelativeLayout) sort_dialog.findViewById(R.id.today_booking);
        recent_booking = (RelativeLayout) sort_dialog.findViewById(R.id.recent_booking);
        upcoming_booking = (RelativeLayout) sort_dialog.findViewById(R.id.upcoming_booking);

        Tv_fromdaate = (TextView) sort_dialog.findViewById(R.id.from_date_select_textView_myjobs);
        Tv_todate = (TextView) sort_dialog.findViewById(R.id.todate_select_textViewmyjobs);
        Rl_layoyt_datefrom = (RelativeLayout) sort_dialog.findViewById(R.id.myjooobsfrom_page_date_select_layout);
        Rl_layout_to = (RelativeLayout) sort_dialog.findViewById(R.id.myjobstodate_select_layout);


        sorting_checkeddate = (ImageView) sort_dialog.findViewById(R.id.sorting_checkedate);
        // sorting_checkedthree=(ImageView)sort_dialog.findViewById(R.id.checkedthree);
        sorting_checkename = (ImageView) sort_dialog.findViewById(R.id.subcategories_sorting_checkename);
        sorting_ascendingimg = (ImageView) sort_dialog.findViewById(R.id.subcategories_ascendingsorting_ascending);
        sorting_descinngimg = (ImageView) sort_dialog.findViewById(R.id.checkeddescending);
        today_booking_image = (ImageView) sort_dialog.findViewById(R.id.today_booking_image);
        recent_booking_image = (ImageView) sort_dialog.findViewById(R.id.recent_booking_image);
        upcoming_booking_image = (ImageView) sort_dialog.findViewById(R.id.upcoming_booking_image);

        Filter_type = "No";

        if (page_selected.equalsIgnoreCase("2") || page_selected.equalsIgnoreCase("3")) {
            today_booking.setEnabled(false);
            recent_booking.setEnabled(false);
            upcoming_booking.setEnabled(false);
            today_booking_text.setTextColor(Color.parseColor("#DCDCDC"));
            recent_booking_text.setTextColor(Color.parseColor("#DCDCDC"));
            upcoming_booking_text.setTextColor(Color.parseColor("#DCDCDC"));

        }

        sorting_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort_dialog.dismiss();
            }
        });


        Rl_layoyt_datefrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                seleceteddate = "1";
                datePicker(savedInstanceState);
            }
        });


        Rl_layout_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleceteddate = "2";
                datePicker(savedInstanceState);
            }
        });


        sortingname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sorting_checkename.setVisibility(View.VISIBLE);
                sorting_checkeddate.setVisibility(View.GONE);

                Sselected_sorting = "name";
            }
        });


        sortingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sorting_checkename.setVisibility(View.GONE);
                sorting_checkeddate.setVisibility(View.VISIBLE);

                Sselected_sorting = "date";
            }
        });


        Ascending_orderby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sorting_descinngimg.setVisibility(View.GONE);
                sorting_ascendingimg.setVisibility(View.VISIBLE);

                Sselected_ordrby = "-1";
            }
        });


        Descending_Orderby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sorting_descinngimg.setVisibility(View.VISIBLE);
                sorting_ascendingimg.setVisibility(View.GONE);

                Sselected_ordrby = "1";
            }
        });
        today_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                today_booking_image.setVisibility(View.VISIBLE);
                recent_booking_image.setVisibility(View.GONE);
                upcoming_booking_image.setVisibility(View.GONE);
                Filter_booking_type = "1";
                Filter_type = "today";
                sortingname.setEnabled(false);
                sortingDate.setEnabled(false);
                Rl_layoyt_datefrom.setEnabled(false);
                Rl_layout_to.setEnabled(false);
                sorting_checkename.setVisibility(View.GONE);
                sorting_checkeddate.setVisibility(View.GONE);
                name_text.setTextColor(Color.parseColor("#DCDCDC"));
                date_text.setTextColor(Color.parseColor("#DCDCDC"));
                Tv_fromdaate.setTextColor(Color.parseColor("#DCDCDC"));
                Tv_todate.setTextColor(Color.parseColor("#DCDCDC"));
                select_filter = true;
            }
        });
        recent_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recent_booking_image.setVisibility(View.VISIBLE);
                today_booking_image.setVisibility(View.GONE);
                upcoming_booking_image.setVisibility(View.GONE);
                Filter_booking_type = "1";
                Filter_type = "recent";
                sortingname.setEnabled(false);
                sortingDate.setEnabled(false);
                Rl_layoyt_datefrom.setEnabled(false);
                Rl_layout_to.setEnabled(false);
                sorting_checkename.setVisibility(View.GONE);
                sorting_checkeddate.setVisibility(View.GONE);
                name_text.setTextColor(Color.parseColor("#DCDCDC"));
                date_text.setTextColor(Color.parseColor("#DCDCDC"));
                Tv_fromdaate.setTextColor(Color.parseColor("#DCDCDC"));
                Tv_todate.setTextColor(Color.parseColor("#DCDCDC"));
                select_filter = true;
            }
        });
        upcoming_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upcoming_booking_image.setVisibility(View.VISIBLE);
                today_booking_image.setVisibility(View.GONE);
                recent_booking_image.setVisibility(View.GONE);
                Filter_booking_type = "1";
                Filter_type = "upcoming";
                sortingname.setEnabled(false);
                sortingDate.setEnabled(false);
                Rl_layoyt_datefrom.setEnabled(false);
                Rl_layout_to.setEnabled(false);
                sorting_checkename.setVisibility(View.GONE);
                sorting_checkeddate.setVisibility(View.GONE);
                name_text.setTextColor(Color.parseColor("#DCDCDC"));
                date_text.setTextColor(Color.parseColor("#DCDCDC"));
                Tv_fromdaate.setTextColor(Color.parseColor("#DCDCDC"));
                Tv_todate.setTextColor(Color.parseColor("#DCDCDC"));
                select_filter = true;
            }
        });


        sorting_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!select_filter) {
                    if (Tv_fromdaate.getText().toString().equalsIgnoreCase("From") || Tv_todate.getText().toString().equalsIgnoreCase("To")) {
                        Alert("Sorry", "Please Enter the From and To Date");
                    } else {
                        sortedMethode();
                    }
                } else {
                    sortedMethode();
                    select_filter = false;
                }
            }
        });
    }

    private void sortedMethode() {
//        drawer_layout.closeDrawer(drawer);
        //postJobRequestSorting(ServiceConstant.myjobs_sortingurl,"2");
        System.out.println("Stype---------------" + Stype);
        if (Stype == "2") {

            Intent myjobtype_intent = new Intent();
            myjobtype_intent.setAction("com.app.MyJob_OnGoing_Fragment");
            myjobtype_intent.putExtra("Type", Stype);
            myjobtype_intent.putExtra("SortBy", Sselected_sorting);
            myjobtype_intent.putExtra("OrderBy", Sselected_ordrby);
            myjobtype_intent.putExtra("from", Tv_fromdaate.getText().toString());
            myjobtype_intent.putExtra("to", Tv_todate.getText().toString());
            myjobtype_intent.putExtra("filter_type", Filter_type);
            context.sendBroadcast(myjobtype_intent);
            // postJobRequestSorting(ServiceConstant.myjobs_sortingurl,Stype);

        } else if (Stype == "4") {

            System.out.println("Stype1---------------" + "Ongoing");
            Intent myjobtypecomplete_intent = new Intent();
            myjobtypecomplete_intent.setAction("com.app.MyJob_Completed_Fragment");
            myjobtypecomplete_intent.putExtra("Type", Stype);
            myjobtypecomplete_intent.putExtra("SortBy", Sselected_sorting);
            myjobtypecomplete_intent.putExtra("OrderBy", Sselected_ordrby);
            myjobtypecomplete_intent.putExtra("from", Tv_fromdaate.getText().toString());
            myjobtypecomplete_intent.putExtra("to", Tv_todate.getText().toString());
            context.sendBroadcast(myjobtypecomplete_intent);

        } else if (Stype == "5") {
            System.out.println("Stype3---------------" + "cancel");
            Intent myjobtypecancelled_intent = new Intent();
            myjobtypecancelled_intent.setAction("com.app.MyJob_Cancelled_Fragment");
            myjobtypecancelled_intent.putExtra("Type", Stype);
            myjobtypecancelled_intent.putExtra("SortBy", Sselected_sorting);
            myjobtypecancelled_intent.putExtra("OrderBy", Sselected_ordrby);
            myjobtypecancelled_intent.putExtra("from", Tv_fromdaate.getText().toString());
            myjobtypecancelled_intent.putExtra("to", Tv_todate.getText().toString());
            context.sendBroadcast(myjobtypecancelled_intent);

        }
    }

    private void loadingDialog() {

        dialog = new LoadingDialog(MyJobs.this);
        dialog.setLoadingTitle(getResources().getString(R.string.loading_in));
        dialog.show();

    }

    private void dismissDialog() {
        dialog.dismiss();
    }

    //--------------Date Select Method-----------
    private void datePicker(Bundle savedState) {

        dialogCaldroidFragment = new CaldroidFragment();
        dialogCaldroidFragment.setCaldroidListener(caldroidListener);

        // If activity is recovered from rotation
        final String dialogTag = "CALDROID_DIALOG_FRAGMENT";
        if (savedState != null) {
            dialogCaldroidFragment.restoreDialogStatesFromKey(getSupportFragmentManager(), savedState,
                    "DIALOG_CALDROID_SAVED_STATE", dialogTag);
            Bundle args = dialogCaldroidFragment.getArguments();
            if (args == null) {
                args = new Bundle();
                dialogCaldroidFragment.setArguments(args);
            }
        } else {
            // Setup arguments
            Bundle bundle = new Bundle();
            // Setup dialogTitle
            dialogCaldroidFragment.setArguments(bundle);
        }

        Calendar cal = Calendar.getInstance();
        Date currentDate = null;
        Date maximumDate = null;
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            String formattedDate = df.format(cal.getTime());
            currentDate = df.parse(formattedDate);

            // Max date is next 7 days
            cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 7);
            maximumDate = cal.getTime();

        } catch (ParseException e1) {
            e1.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }


        dialogCaldroidFragment.setMinDate(currentDate);
        dialogCaldroidFragment.setMaxDate(maximumDate);
        dialogCaldroidFragment.show(getSupportFragmentManager(), dialogTag);
        dialogCaldroidFragment.refreshView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!ChatMessageService.isStarted()) {
            Intent intent = new Intent(MyJobs.this, ChatMessageService.class);
            startService(intent);
        }
    }

    public class RefreshReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equalsIgnoreCase("com.avail.finish")) {
                finish();
            }

        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(finishReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
