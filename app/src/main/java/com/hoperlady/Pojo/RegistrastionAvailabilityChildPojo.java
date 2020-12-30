package com.hoperlady.Pojo;

import java.io.Serializable;

public class RegistrastionAvailabilityChildPojo implements Serializable {
    private String slot = "";
    private String time = "";
    private boolean selected = false;
    private String Day = "";
    private int ParentIndex;
    private boolean wholeDaySelect = false;

    public boolean isWholeDaySelect() {
        return wholeDaySelect;
    }

    public void setWholeDaySelect(boolean wholeDaySelect) {
        this.wholeDaySelect = wholeDaySelect;
    }

    public int getParentIndex() {
        return ParentIndex;
    }

    public void setParentIndex(int parentIndex) {
        ParentIndex = parentIndex;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
