package com.hoperlady.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.hoperlady.Pojo.AvailDayPojo;
import com.hoperlady.Pojo.Availability_selecting_pojo;
import com.hoperlady.Pojo.Experiancepojo;
import com.hoperlady.R;
import com.hoperlady.Utils.ConnectionDetector;
import com.hoperlady.Utils.SessionManager;
import com.hoperlady.adapter.ExperianceAdapter;
import com.hoperlady.adapter.RegisterAvailabilty_Adapter;
import com.hoperlady.hockeyapp.ActivityHockeyApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import core.Dialog.PkLoadingDialog;
import core.Volley.ServiceRequest;
import core.service.ServiceConstant;


public class RegisterPageThird extends ActivityHockeyApp implements View.OnClickListener {

    private RelativeLayout register_avail_drawer, main_RL;
    private LinearLayout register_header_back_layout;
    private GridView gridView;
    private DrawerLayout drawer_layout;
    private TextView day_txt_TV;
    private SwitchCompat evening_switch, morning_switch, afternoon_switch;
    private LinearLayout save_LL, continue_button_LL;
    private int Position;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ArrayList<Availability_selecting_pojo> avail_pojoArraylist;
    private Availability_selecting_pojo avail_pojo;
    private RegisterAvailabilty_Adapter adapter;
    private ExperianceAdapter expAdapter;
    private ListView experiance_listview;
    private ConnectionDetector cd;
    private ArrayList<Experiancepojo> ExperianceList;
    private ArrayList<Experiancepojo> sessionExperianceList;
    private PkLoadingDialog myLoadingDialog;
    private ArrayList<String> questions_list;
    private SessionManager sessionManager;
    private ArrayList<AvailDayPojo> myDayArrList;

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-conditionc
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.setDivider(null);
        listView.requestLayout();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerpage_third_layout);
        initialize();
    }

    private void initialize() {
        register_header_back_layout = (LinearLayout) findViewById(R.id.register_header_back_layout);
        register_avail_drawer = (RelativeLayout) findViewById(R.id.register_avail_drawer);
        main_RL = (RelativeLayout) findViewById(R.id.main_RL);
        cd = new ConnectionDetector(RegisterPageThird.this);
        day_txt_TV = (TextView) findViewById(R.id.day_txt_TV);
        gridView = (GridView) findViewById(R.id.gridView);
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer_layout.requestDisallowInterceptTouchEvent(true);
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        morning_switch = (SwitchCompat) findViewById(R.id.morning_switch);
        afternoon_switch = (SwitchCompat) findViewById(R.id.afternoon_switch);
        evening_switch = (SwitchCompat) findViewById(R.id.evening_switch);
        save_LL = (LinearLayout) findViewById(R.id.save_LL);
        continue_button_LL = (LinearLayout) findViewById(R.id.continue_button_LL);
        avail_pojo = new Availability_selecting_pojo();
        avail_pojoArraylist = new ArrayList<>();
        myDayArrList = new ArrayList<>();
        //click listners
        register_header_back_layout.setOnClickListener(this);
        save_LL.setOnClickListener(this);
        continue_button_LL.setOnClickListener(this);
        loadDayItems();
        adapter = new RegisterAvailabilty_Adapter(this, myDayArrList);
        gridView.setAdapter(adapter);
        experiance_listview = (ListView) findViewById(R.id.experiance_listview);
        questions_list = new ArrayList<String>();
        sessionManager = new SessionManager(RegisterPageThird.this);
        if (cd.isConnectingToInternet()) {
            postQuestionAnswerRequest(ServiceConstant.QUESTION_ANSWER_URL);
        }
        defaultDaysFalse();
//        if (myDayArrList.size() > 0) {
//            setListViewHeightBasedOnChildren(gridView);
//            adapter.notifyDataSetChanged();
//        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer_layout, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                gridView.setEnabled(true);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                gridView.setEnabled(false);
            }
        };

        drawer_layout.setDrawerListener(actionBarDrawerToggle);
        drawer_layout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println("position" + position + "idddd" + id);
                if (position == 0) {
                    Position = position;
                    //   main_RL.setEnabled(false);
                    //  gridView.setEnabled(false);
                    if (avail_pojoArraylist.size() > 0) {
                        boolean selected_previously = false;
                        int selected_position = 0;
                        for (int i = 0; i < avail_pojoArraylist.size(); i++) {
                            if (avail_pojoArraylist.get(i).getDay_name().equalsIgnoreCase("monday")) {
                                selected_position = i;
                                selected_previously = true;
                            }
                        }
                        if (selected_previously) {
                            morning_switch.setChecked(avail_pojoArraylist.get(selected_position).isMorning());
                            afternoon_switch.setChecked(avail_pojoArraylist.get(selected_position).isAfternoon());
                            evening_switch.setChecked(avail_pojoArraylist.get(selected_position).isEvening());
                        } else {
                            morning_switch.setChecked(false);
                            afternoon_switch.setChecked(false);
                            evening_switch.setChecked(false);
                        }

                    } else {
                        morning_switch.setChecked(false);
                        afternoon_switch.setChecked(false);
                        evening_switch.setChecked(false);
                    }
                    drawer_layout.openDrawer(register_avail_drawer);
                    day_txt_TV.setText(getResources().getString(R.string.monday));


                } else if (position == 1) {
                    Position = position;
                    if (avail_pojoArraylist.size() > 0) {
                        boolean selected_previously = false;
                        int selected_position = 0;
                        for (int i = 0; i < avail_pojoArraylist.size(); i++) {
                            if (avail_pojoArraylist.get(i).getDay_name().equalsIgnoreCase("tuesday")) {
                                selected_position = i;
                                selected_previously = true;
                            }
                        }
                        if (selected_previously) {
                            morning_switch.setChecked(avail_pojoArraylist.get(selected_position).isMorning());
                            afternoon_switch.setChecked(avail_pojoArraylist.get(selected_position).isAfternoon());
                            evening_switch.setChecked(avail_pojoArraylist.get(selected_position).isEvening());
                        } else {
                            morning_switch.setChecked(false);
                            afternoon_switch.setChecked(false);
                            evening_switch.setChecked(false);
                        }

                    } else {
                        morning_switch.setChecked(false);
                        afternoon_switch.setChecked(false);
                        evening_switch.setChecked(false);
                    }
                    drawer_layout.openDrawer(register_avail_drawer);
                    day_txt_TV.setText(getResources().getString(R.string.tuesday));
                } else if (position == 2) {
                    Position = position;
                    if (avail_pojoArraylist.size() > 0) {
                        boolean selected_previously = false;
                        int selected_position = 0;
                        for (int i = 0; i < avail_pojoArraylist.size(); i++) {
                            if (avail_pojoArraylist.get(i).getDay_name().equalsIgnoreCase("wednesday")) {
                                selected_position = i;
                                selected_previously = true;
                            }
                        }
                        if (selected_previously) {
                            morning_switch.setChecked(avail_pojoArraylist.get(selected_position).isMorning());
                            afternoon_switch.setChecked(avail_pojoArraylist.get(selected_position).isAfternoon());
                            evening_switch.setChecked(avail_pojoArraylist.get(selected_position).isEvening());
                        } else {
                            morning_switch.setChecked(false);
                            afternoon_switch.setChecked(false);
                            evening_switch.setChecked(false);
                        }

                    } else {
                        morning_switch.setChecked(false);
                        afternoon_switch.setChecked(false);
                        evening_switch.setChecked(false);
                    }
                    drawer_layout.openDrawer(register_avail_drawer);
                    day_txt_TV.setText(getResources().getString(R.string.wednesday));
                } else if (position == 3) {
                    Position = position;
                    if (avail_pojoArraylist.size() > 0) {
                        boolean selected_previously = false;
                        int selected_position = 0;
                        for (int i = 0; i < avail_pojoArraylist.size(); i++) {
                            if (avail_pojoArraylist.get(i).getDay_name().equalsIgnoreCase("thursday")) {
                                selected_position = i;
                                selected_previously = true;
                            }
                        }
                        if (selected_previously) {
                            morning_switch.setChecked(avail_pojoArraylist.get(selected_position).isMorning());
                            afternoon_switch.setChecked(avail_pojoArraylist.get(selected_position).isAfternoon());
                            evening_switch.setChecked(avail_pojoArraylist.get(selected_position).isEvening());
                        } else {
                            morning_switch.setChecked(false);
                            afternoon_switch.setChecked(false);
                            evening_switch.setChecked(false);
                        }

                    } else {
                        morning_switch.setChecked(false);
                        afternoon_switch.setChecked(false);
                        evening_switch.setChecked(false);
                    }
                    drawer_layout.openDrawer(register_avail_drawer);
                    day_txt_TV.setText(getResources().getString(R.string.thursday));
                } else if (position == 4) {
                    Position = position;
                    if (avail_pojoArraylist.size() > 0) {
                        boolean selected_previously = false;
                        int selected_position = 0;
                        for (int i = 0; i < avail_pojoArraylist.size(); i++) {
                            if (avail_pojoArraylist.get(i).getDay_name().equalsIgnoreCase("friday")) {
                                selected_position = i;
                                selected_previously = true;
                            }
                        }
                        if (selected_previously) {
                            morning_switch.setChecked(avail_pojoArraylist.get(selected_position).isMorning());
                            afternoon_switch.setChecked(avail_pojoArraylist.get(selected_position).isAfternoon());
                            evening_switch.setChecked(avail_pojoArraylist.get(selected_position).isEvening());
                        } else {
                            morning_switch.setChecked(false);
                            afternoon_switch.setChecked(false);
                            evening_switch.setChecked(false);
                        }

                    } else {
                        morning_switch.setChecked(false);
                        afternoon_switch.setChecked(false);
                        evening_switch.setChecked(false);
                    }
                    drawer_layout.openDrawer(register_avail_drawer);
                    day_txt_TV.setText(getResources().getString(R.string.friday));

                } else if (position == 5) {
                    Position = position;
                    if (avail_pojoArraylist.size() > 0) {
                        boolean selected_previously = false;
                        int selected_position = 0;
                        for (int i = 0; i < avail_pojoArraylist.size(); i++) {
                            if (avail_pojoArraylist.get(i).getDay_name().equalsIgnoreCase("saturday")) {
                                selected_position = i;
                                selected_previously = true;
                            }
                        }
                        if (selected_previously) {
                            morning_switch.setChecked(avail_pojoArraylist.get(selected_position).isMorning());
                            afternoon_switch.setChecked(avail_pojoArraylist.get(selected_position).isAfternoon());
                            evening_switch.setChecked(avail_pojoArraylist.get(selected_position).isEvening());
                        } else {
                            morning_switch.setChecked(false);
                            afternoon_switch.setChecked(false);
                            evening_switch.setChecked(false);
                        }

                    } else {
                        morning_switch.setChecked(false);
                        afternoon_switch.setChecked(false);
                        evening_switch.setChecked(false);
                    }
                    drawer_layout.openDrawer(register_avail_drawer);
                    day_txt_TV.setText(getResources().getString(R.string.saturday));
                } else if (position == 6) {
                    if (avail_pojoArraylist.size() > 0) {
                        boolean selected_previously = false;
                        int selected_position = 0;
                        for (int i = 0; i < avail_pojoArraylist.size(); i++) {
                            if (avail_pojoArraylist.get(i).getDay_name().equalsIgnoreCase("sunday")) {
                                selected_position = i;
                                selected_previously = true;
                            }
                        }
                        if (selected_previously) {
                            morning_switch.setChecked(avail_pojoArraylist.get(selected_position).isMorning());
                            afternoon_switch.setChecked(avail_pojoArraylist.get(selected_position).isAfternoon());
                            evening_switch.setChecked(avail_pojoArraylist.get(selected_position).isEvening());
                        } else {
                            morning_switch.setChecked(false);
                            afternoon_switch.setChecked(false);
                            evening_switch.setChecked(false);
                        }

                    } else {
                        morning_switch.setChecked(false);
                        afternoon_switch.setChecked(false);
                        evening_switch.setChecked(false);
                    }
                    Position = position;
                    drawer_layout.openDrawer(register_avail_drawer);
                    day_txt_TV.setText(getResources().getString(R.string.sunday));
                }
            }
        });

    }

    private void defaultDaysFalse() {
        for (int i = 0; i < 7; i++) {
//            avail_pojo=new Availability_selecting_pojo();
            avail_pojo = new Availability_selecting_pojo();
            avail_pojo.setEvening(false);
            avail_pojo.setAfternoon(false);
            avail_pojo.setMorning(false);
            if (i == 0) {
                avail_pojo.setDay_position(i);
                avail_pojo.setDay_name("Monday");
            } else if (i == 1) {
                avail_pojo.setDay_position(i);
                avail_pojo.setDay_name("Tuesday");
            } else if (i == 2) {
                avail_pojo.setDay_position(i);
                avail_pojo.setDay_name("Wednesday");
            } else if (i == 3) {
                avail_pojo.setDay_position(i);
                avail_pojo.setDay_name("Thursday");
            } else if (i == 4) {
                avail_pojo.setDay_position(i);
                avail_pojo.setDay_name("Friday");
            } else if (i == 5) {
                avail_pojo.setDay_position(i);
                avail_pojo.setDay_name("Saturday");
            } else if (i == 6) {
                avail_pojo.setDay_position(i);
                avail_pojo.setDay_name("Sunday");
            }
            avail_pojoArraylist.add(avail_pojo);
        }
    }

    private void loadDayItems() {

        AvailDayPojo availDayPojo_mon = new AvailDayPojo();
        availDayPojo_mon.setDayName("Monday");
        availDayPojo_mon.setDaySelected("0");

        AvailDayPojo availDayPojo_tue = new AvailDayPojo();
        availDayPojo_tue.setDayName("Tuesday");
        availDayPojo_tue.setDaySelected("0");

        AvailDayPojo availDayPojo_wed = new AvailDayPojo();
        availDayPojo_wed.setDayName("Wednesday");
        availDayPojo_wed.setDaySelected("0");

        AvailDayPojo availDayPojo_thur = new AvailDayPojo();
        availDayPojo_thur.setDayName("Thursday");
        availDayPojo_thur.setDaySelected("0");

        AvailDayPojo availDayPojo_fri = new AvailDayPojo();
        availDayPojo_fri.setDayName("Friday");
        availDayPojo_fri.setDaySelected("0");

        AvailDayPojo availDayPojo_sat = new AvailDayPojo();
        availDayPojo_sat.setDayName("Saturday");
        availDayPojo_sat.setDaySelected("0");

        AvailDayPojo availDayPojo_sun = new AvailDayPojo();
        availDayPojo_sun.setDayName("Sunday");
        availDayPojo_sun.setDaySelected("0");

        myDayArrList.add(availDayPojo_mon);
        myDayArrList.add(availDayPojo_tue);
        myDayArrList.add(availDayPojo_wed);
        myDayArrList.add(availDayPojo_thur);
        myDayArrList.add(availDayPojo_fri);
        myDayArrList.add(availDayPojo_sat);
        myDayArrList.add(availDayPojo_sun);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_header_back_layout:
                onBackPressed();
                finish();
                break;
            case R.id.save_LL:
                savingAvailability();
                drawer_layout.closeDrawer(register_avail_drawer);
                break;
            case R.id.main_RL:
                main_RL.setEnabled(false);
                gridView.setEnabled(false);
                break;
            case R.id.continue_button_LL: {

                boolean hasAnyDaySelected = false;
                for (int i = 0; i < myDayArrList.size(); i++) {
                    if (myDayArrList.get(i).getDaySelected().equals("1")) {
                        hasAnyDaySelected = true;
                        break;
                    }
                }

                if (hasAnyDaySelected)
                    ContinueRegistrationRequest();
                else {
                    Toast.makeText(this, "Please choose your availability", Toast.LENGTH_SHORT).show();
                }
            }

        }

    }

    private void savingAvailability() {
        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
        avail_pojo = new Availability_selecting_pojo();
        if (Position == 0) {

            if (avail_pojoArraylist.size() > 0) {
                boolean present = false;
                int match_position = 0;
                for (int i = 0; i < avail_pojoArraylist.size(); i++) {
                    if (avail_pojoArraylist.get(i).getDay_name().equalsIgnoreCase("monday")) {
                        avail_pojo.setDay_position(Position);
                        avail_pojo.setDay_name("Monday");
                        match_position = i;
                        avail_pojo.setMorning(morning_switch.isChecked());
                        avail_pojo.setAfternoon(afternoon_switch.isChecked());
                        avail_pojo.setEvening(evening_switch.isChecked());
                        //   avail_pojoArraylist.add(i, avail_pojo);
                        present = true;
                    } else {
                        avail_pojo.setDay_name("Monday");
                        avail_pojo.setDay_position(Position);
                        avail_pojo.setMorning(morning_switch.isChecked());
                        avail_pojo.setAfternoon(afternoon_switch.isChecked());
                        avail_pojo.setEvening(evening_switch.isChecked());
                        // avail_pojoArraylist.add(avail_pojo);
//                        present = false;
                    }
                }
                if (present) {
                    avail_pojoArraylist.set(match_position, avail_pojo);
                } else {
                    avail_pojoArraylist.add(avail_pojo);
                }

            } else {
                avail_pojo.setDay_name("Monday");

                avail_pojo.setDay_position(Position);
                avail_pojo.setMorning(morning_switch.isChecked());
                avail_pojo.setAfternoon(afternoon_switch.isChecked());
                avail_pojo.setEvening(evening_switch.isChecked());
                avail_pojoArraylist.add(avail_pojo);
            }
            NotifySelectedBG(avail_pojo, Position);

        } else if (Position == 1) {

            if (avail_pojoArraylist.size() > 0) {
                boolean present = false;
                int match_position = 0;
                for (int i = 0; i < avail_pojoArraylist.size(); i++) {
                    if (avail_pojoArraylist.get(i).getDay_name().equalsIgnoreCase("tuesday")) {
                        avail_pojo.setDay_position(Position);
                        avail_pojo.setDay_name("Tuesday");
                        match_position = i;
                        avail_pojo.setMorning(morning_switch.isChecked());
                        avail_pojo.setAfternoon(afternoon_switch.isChecked());
                        avail_pojo.setEvening(evening_switch.isChecked());
                        //   avail_pojoArraylist.add(i, avail_pojo);
                        present = true;
                    } else {
                        avail_pojo.setDay_name("Tuesday");
                        avail_pojo.setDay_position(Position);
                        avail_pojo.setMorning(morning_switch.isChecked());
                        avail_pojo.setAfternoon(afternoon_switch.isChecked());
                        avail_pojo.setEvening(evening_switch.isChecked());
                        // avail_pojoArraylist.add(avail_pojo);
                        //      present = false;
                    }
                }
                if (present) {
                    avail_pojoArraylist.set(match_position, avail_pojo);
                } else {
                    avail_pojoArraylist.add(avail_pojo);
                }

            } else {
                avail_pojo.setDay_name("Tuesday");
                avail_pojo.setDay_position(Position);
                avail_pojo.setMorning(morning_switch.isChecked());
                avail_pojo.setAfternoon(afternoon_switch.isChecked());
                avail_pojo.setEvening(evening_switch.isChecked());
                avail_pojoArraylist.add(avail_pojo);
            }
            NotifySelectedBG(avail_pojo, Position);

        } else if (Position == 2) {
            if (avail_pojoArraylist.size() > 0) {
                boolean present = false;
                int match_position = 0;
                for (int i = 0; i < avail_pojoArraylist.size(); i++) {
                    if (avail_pojoArraylist.get(i).getDay_name().equalsIgnoreCase("wednesday")) {
                        avail_pojo.setDay_position(Position);
                        avail_pojo.setDay_name("Wednesday");
                        match_position = i;
                        avail_pojo.setMorning(morning_switch.isChecked());
                        avail_pojo.setAfternoon(afternoon_switch.isChecked());
                        avail_pojo.setEvening(evening_switch.isChecked());
                        //   avail_pojoArraylist.add(i, avail_pojo);
                        present = true;
                    } else {
                        avail_pojo.setDay_name("Wednesday");
                        avail_pojo.setDay_position(Position);
                        avail_pojo.setMorning(morning_switch.isChecked());
                        avail_pojo.setAfternoon(afternoon_switch.isChecked());
                        avail_pojo.setEvening(evening_switch.isChecked());
                        // avail_pojoArraylist.add(avail_pojo);
                        //  present = false;
                    }
                }
                if (present) {
                    avail_pojoArraylist.set(match_position, avail_pojo);
                } else {
                    avail_pojoArraylist.add(avail_pojo);
                }

            } else {
                avail_pojo.setDay_name("Wednesday");
                avail_pojo.setDay_position(Position);
                avail_pojo.setMorning(morning_switch.isChecked());
                avail_pojo.setAfternoon(afternoon_switch.isChecked());
                avail_pojo.setEvening(evening_switch.isChecked());
                avail_pojoArraylist.add(avail_pojo);
            }
            NotifySelectedBG(avail_pojo, Position);
        } else if (Position == 3) {
            if (avail_pojoArraylist.size() > 0) {
                boolean present = false;
                int match_position = 0;
                for (int i = 0; i < avail_pojoArraylist.size(); i++) {
                    if (avail_pojoArraylist.get(i).getDay_name().equalsIgnoreCase("thursday")) {
                        avail_pojo.setDay_position(Position);
                        avail_pojo.setDay_name("Thursday");
                        match_position = i;
                        avail_pojo.setMorning(morning_switch.isChecked());
                        avail_pojo.setAfternoon(afternoon_switch.isChecked());
                        avail_pojo.setEvening(evening_switch.isChecked());
                        //   avail_pojoArraylist.add(i, avail_pojo);
                        present = true;
                    } else {
                        avail_pojo.setDay_name("Thursday");
                        avail_pojo.setDay_position(Position);
                        avail_pojo.setMorning(morning_switch.isChecked());
                        avail_pojo.setAfternoon(afternoon_switch.isChecked());
                        avail_pojo.setEvening(evening_switch.isChecked());
                        // avail_pojoArraylist.add(avail_pojo);
                        //  present = false;
                    }
                }
                if (present) {
                    avail_pojoArraylist.set(match_position, avail_pojo);
                } else {
                    avail_pojoArraylist.add(avail_pojo);
                }

            } else {
                avail_pojo.setDay_name("Thursday");
                avail_pojo.setDay_position(Position);
                avail_pojo.setMorning(morning_switch.isChecked());
                avail_pojo.setAfternoon(afternoon_switch.isChecked());
                avail_pojo.setEvening(evening_switch.isChecked());
                avail_pojoArraylist.add(avail_pojo);
            }

            NotifySelectedBG(avail_pojo, Position);
        } else if (Position == 4) {
            if (avail_pojoArraylist.size() > 0) {
                boolean present = false;
                int match_position = 0;
                for (int i = 0; i < avail_pojoArraylist.size(); i++) {
                    if (avail_pojoArraylist.get(i).getDay_name().equalsIgnoreCase("friday")) {
                        avail_pojo.setDay_position(Position);
                        avail_pojo.setDay_name("Friday");
                        match_position = i;
                        avail_pojo.setMorning(morning_switch.isChecked());
                        avail_pojo.setAfternoon(afternoon_switch.isChecked());
                        avail_pojo.setEvening(evening_switch.isChecked());
                        //   avail_pojoArraylist.add(i, avail_pojo);
                        present = true;
                    } else {
                        avail_pojo.setDay_name("Friday");
                        avail_pojo.setDay_position(Position);
                        avail_pojo.setMorning(morning_switch.isChecked());
                        avail_pojo.setAfternoon(afternoon_switch.isChecked());
                        avail_pojo.setEvening(evening_switch.isChecked());
                        // avail_pojoArraylist.add(avail_pojo);
                        //   present = false;
                    }
                }
                if (present) {
                    avail_pojoArraylist.set(match_position, avail_pojo);
                } else {
                    avail_pojoArraylist.add(avail_pojo);
                }

            } else {
                avail_pojo.setDay_name("Friday");
                avail_pojo.setDay_position(Position);
                avail_pojo.setMorning(morning_switch.isChecked());
                avail_pojo.setAfternoon(afternoon_switch.isChecked());
                avail_pojo.setEvening(evening_switch.isChecked());
                avail_pojoArraylist.add(avail_pojo);
            }

            NotifySelectedBG(avail_pojo, Position);
        } else if (Position == 5) {
            if (avail_pojoArraylist.size() > 0) {
                boolean present = false;
                int match_position = 0;
                for (int i = 0; i < avail_pojoArraylist.size(); i++) {
                    if (avail_pojoArraylist.get(i).getDay_name().equalsIgnoreCase("saturday")) {
                        avail_pojo.setDay_position(Position);
                        avail_pojo.setDay_name("Saturday");
                        match_position = i;
                        avail_pojo.setMorning(morning_switch.isChecked());
                        avail_pojo.setAfternoon(afternoon_switch.isChecked());
                        avail_pojo.setEvening(evening_switch.isChecked());
                        //   avail_pojoArraylist.add(i, avail_pojo);
                        present = true;
                    } else {
                        avail_pojo.setDay_name("Saturday");
                        avail_pojo.setDay_position(Position);
                        avail_pojo.setMorning(morning_switch.isChecked());
                        avail_pojo.setAfternoon(afternoon_switch.isChecked());
                        avail_pojo.setEvening(evening_switch.isChecked());
                        // avail_pojoArraylist.add(avail_pojo);
                        //    present = false;
                    }
                }
                if (present) {
                    avail_pojoArraylist.set(match_position, avail_pojo);
                } else {
                    avail_pojoArraylist.add(avail_pojo);
                }

            } else {
                avail_pojo.setDay_name("Saturday");
                avail_pojo.setDay_position(Position);
                avail_pojo.setMorning(morning_switch.isChecked());
                avail_pojo.setAfternoon(afternoon_switch.isChecked());
                avail_pojo.setEvening(evening_switch.isChecked());
                avail_pojoArraylist.add(avail_pojo);
            }
            NotifySelectedBG(avail_pojo, Position);
        } else if (Position == 6) {
            if (avail_pojoArraylist.size() > 0) {
                boolean present = false;
                int match_position = 0;
                for (int i = 0; i < avail_pojoArraylist.size(); i++) {
                    if (avail_pojoArraylist.get(i).getDay_name().equalsIgnoreCase("sunday")) {
                        avail_pojo.setDay_position(Position);
                        avail_pojo.setDay_name("Sunday");
                        match_position = i;
                        avail_pojo.setMorning(morning_switch.isChecked());
                        avail_pojo.setAfternoon(afternoon_switch.isChecked());
                        avail_pojo.setEvening(evening_switch.isChecked());
                        //   avail_pojoArraylist.add(i, avail_pojo);
                        present = true;
                    } else {
                        avail_pojo.setDay_name("Sunday");
                        avail_pojo.setDay_position(Position);
                        avail_pojo.setMorning(morning_switch.isChecked());
                        avail_pojo.setAfternoon(afternoon_switch.isChecked());
                        avail_pojo.setEvening(evening_switch.isChecked());
                        // avail_pojoArraylist.add(avail_pojo);
                        //      present = false;
                    }
                }
                if (present) {
                    avail_pojoArraylist.set(match_position, avail_pojo);
                } else {
                    avail_pojoArraylist.add(avail_pojo);
                }

            } else {
                avail_pojo.setDay_name("Sunday");
                avail_pojo.setDay_position(Position);
                avail_pojo.setMorning(morning_switch.isChecked());
                avail_pojo.setAfternoon(afternoon_switch.isChecked());
                avail_pojo.setEvening(evening_switch.isChecked());
                avail_pojoArraylist.add(avail_pojo);
            }

            NotifySelectedBG(avail_pojo, Position);
        }
//       adapter=new    RegisterAvailabilty_Adapter(RegisterPageThird.this,avail_pojoArraylist);
//        adapter.notifyDataSetChanged();
    }

    private void NotifySelectedBG(Availability_selecting_pojo avail_pojo, final int position) {
        if (avail_pojo.isMorning() || avail_pojo.isAfternoon() || avail_pojo.isEvening()) {
            try {

                myDayArrList.get(position).setDaySelected("1");
                adapter.notifyDataSetChanged();
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        } else {
            myDayArrList.get(position).setDaySelected("0");
            adapter.notifyDataSetChanged();
        }
    }

    private void postQuestionAnswerRequest(String questionAnswerUrl) {
        myLoadingDialog = new PkLoadingDialog(RegisterPageThird.this);
        myLoadingDialog.show();

        HashMap<String, String> jsonParams = new HashMap<String, String>();
        jsonParams.put("tasker", "question");

        System.out.println("postQuestionAnswerRequest jsonParams" + jsonParams);

        ExperianceList = new ArrayList<Experiancepojo>();
        ServiceRequest mservicerequest = new ServiceRequest(RegisterPageThird.this);
        mservicerequest.makeServiceRequest(questionAnswerUrl, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {
            @Override
            public void onCompleteListener(String response) {

                System.out.println("postQuestionAnswerRequest response" + response);

                String Str_Status = "";
                try {
                    JSONObject jobject = new JSONObject(response);
                    Str_Status = jobject.getString("status");
                    if (Str_Status.equalsIgnoreCase("1")) {
                        JSONArray jsonArray = jobject.getJSONArray("response");
                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject listObject = jsonArray.getJSONObject(i);
                                Experiancepojo pojo = new Experiancepojo();
                                pojo.setQuestion(listObject.getString("question"));
                                pojo.setId(listObject.getString("_id"));
                                ExperianceList.add(pojo);
                            }
                        } else {
                            ExperianceList.clear();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    myLoadingDialog.dismiss();
                }
                if (Str_Status.equalsIgnoreCase("1")) {
                    expAdapter = new ExperianceAdapter(RegisterPageThird.this, ExperianceList, experiance_listview);
                    experiance_listview.setAdapter(expAdapter);
                    if (ExperianceList.size() > 0) {
                        setListViewHeightBasedOnChildren(experiance_listview);
                        expAdapter.notifyDataSetChanged();
                    }
                }
                myLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                myLoadingDialog.dismiss();
            }
        });
    }

    private void ContinueRegistrationRequest() {

        myLoadingDialog = new PkLoadingDialog(RegisterPageThird.this);
        myLoadingDialog.show();

        HashMap<String, String> jsonParams = new HashMap<String, String>();

        for (int i = 0; i < avail_pojoArraylist.size(); i++) {
            jsonParams.put("working_days[" + i + "][day]", avail_pojoArraylist.get(i).getDay_name());
            jsonParams.put("working_days[" + i + "][hour][morning]", "" + avail_pojoArraylist.get(i).isMorning());
            jsonParams.put("working_days[" + i + "][hour][afternoon]", "" + avail_pojoArraylist.get(i).isAfternoon());
            jsonParams.put("working_days[" + i + "][hour][evening]", "" + avail_pojoArraylist.get(i).isEvening());
        }

        expAdapter = new ExperianceAdapter(RegisterPageThird.this, ExperianceList, experiance_listview);
        questions_list = expAdapter.getAnswer();

        JSONArray jsonArray1 = new JSONArray();
        ArrayList<String> finalArayAnserlist = new ArrayList<String>();
        sessionExperianceList = new ArrayList<Experiancepojo>();
        for (int i = 0; i <= ExperianceList.size(); i++) {
            try {
                Experiancepojo experiancepojo = new Experiancepojo();
                experiancepojo.setId(ExperianceList.get(i).getId());
                experiancepojo.setAnswer(questions_list.get(i));
                sessionExperianceList.add(experiancepojo);

                JSONObject jsonObjectExpList = new JSONObject();
                jsonObjectExpList.put("question", ExperianceList.get(i).getId());
                jsonObjectExpList.put("answer", questions_list.get(i));
                jsonArray1.put(jsonObjectExpList);

            } catch (IndexOutOfBoundsException ie) {
                ie.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        jsonParams.put("profile_details", jsonArray1.toString());

        System.out.println("ContinueRegistrationRequest jsonParams" + jsonParams);

        ServiceRequest myRequest = new ServiceRequest(RegisterPageThird.this);
        myRequest.makeServiceRequest(ServiceConstant.Register_Form3, Request.Method.POST, jsonParams, new ServiceRequest.ServiceListener() {

            @Override
            public void onCompleteListener(String response) {

                System.out.println("ContinueRegistrationRequest response" + response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("1")) {
                        sessionManager.putAvailDays(avail_pojoArraylist);
                        if (sessionExperianceList.size() > 0) {
                            sessionManager.putExperianceList(sessionExperianceList);
                        }

                        Intent intent = new Intent(RegisterPageThird.this, RegisterPageFourth.class);
                        startActivity(intent);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                myLoadingDialog.dismiss();
            }

            @Override
            public void onErrorListener() {
                myLoadingDialog.dismiss();
            }
        });
    }
}
