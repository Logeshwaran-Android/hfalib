package com.hoperlady.Pojo;

import java.io.Serializable;
import java.util.ArrayList;

public class RegistrastionAvailabilityPojo implements Serializable {
    private String dayNames = "";
    private boolean wholeDayChoose = false;
    private boolean selectedDay = false;
    private ArrayList<RegistrastionAvailabilityChildPojo> pojoArrayList;

    public boolean isSelectedDay() {
        return selectedDay;
    }

    public void setSelectedDay(boolean selectedDay) {
        this.selectedDay = selectedDay;
    }

    public boolean isWholeDayChoose() {
        return wholeDayChoose;
    }

    public void setWholeDayChoose(boolean wholeDayChoose) {
        this.wholeDayChoose = wholeDayChoose;
    }

    public String getDayNames() {
        return dayNames;
    }

    public void setDayNames(String dayNames) {
        this.dayNames = dayNames;
    }

    public ArrayList<RegistrastionAvailabilityChildPojo> getPojoArrayList() {
        return pojoArrayList;
    }

    public void setPojoArrayList(ArrayList<RegistrastionAvailabilityChildPojo> pojoArrayList) {
        this.pojoArrayList = pojoArrayList;
    }
}
