package com.hoperlady.Pojo;

import java.io.Serializable;
import java.util.ArrayList;

public class MyProfileAvailabilityPojo implements Serializable {
    private String dayNames = "";
    private String wholeDayChoose = "";
    private String selectedDay = "";
    private ArrayList<MyProfileAvailabilityShowPojo> pojoArrayList;

    public String getSelectedDay() {
        return selectedDay;
    }

    public void setSelectedDay(String selectedDay) {
        this.selectedDay = selectedDay;
    }

    public String getWholeDayChoose() {
        return wholeDayChoose;
    }

    public void setWholeDayChoose(String wholeDayChoose) {
        this.wholeDayChoose = wholeDayChoose;
    }

    public String getDayNames() {
        return dayNames;
    }

    public void setDayNames(String dayNames) {
        this.dayNames = dayNames;
    }

    public ArrayList<MyProfileAvailabilityShowPojo> getPojoArrayList() {
        return pojoArrayList;
    }

    public void setPojoArrayList(ArrayList<MyProfileAvailabilityShowPojo> pojoArrayList) {
        this.pojoArrayList = pojoArrayList;
    }
}
