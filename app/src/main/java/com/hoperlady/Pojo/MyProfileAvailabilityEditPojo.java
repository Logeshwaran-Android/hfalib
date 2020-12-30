package com.hoperlady.Pojo;

import java.io.Serializable;

public class MyProfileAvailabilityEditPojo implements Serializable {
    private String slot = "";
    private String time = "";
    private String selected = "";
    private String Day = "";

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
