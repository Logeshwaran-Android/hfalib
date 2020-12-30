package com.hoperlady.Pojo;

public class Availability_selecting_pojo {

    int day_position;
    String day_name;
    boolean morning = false, afternoon = false, evening = false;

    public int getDay_position() {
        return day_position;
    }

    public void setDay_position(int day_position) {
        this.day_position = day_position;
    }

    public String getDay_name() {
        return day_name;
    }

    public void setDay_name(String day_name) {
        this.day_name = day_name;
    }

    public boolean isMorning() {
        return morning;
    }

    public void setMorning(boolean morning) {
        this.morning = morning;
    }

    public boolean isAfternoon() {
        return afternoon;
    }

    public void setAfternoon(boolean afternoon) {
        this.afternoon = afternoon;
    }

    public boolean isEvening() {
        return evening;
    }

    public void setEvening(boolean evening) {
        this.evening = evening;
    }


}
