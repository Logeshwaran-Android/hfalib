package com.hoperlady.Pojo;

import com.google.gson.Gson;

public class WeekTaskBarChartPojo {

    private String job_date = "";
    private String earnings = "";
    private String task_count = "";
    private boolean barLineSelected = false;

    public boolean isBarLineSelected() {
        return barLineSelected;
    }

    public void setBarLineSelected(boolean barLineSelected) {
        this.barLineSelected = barLineSelected;
    }

    public String getJob_date() {
        return job_date;
    }

    public void setJob_date(String job_date) {
        this.job_date = job_date;
    }

    public String getEarnings() {
        return earnings;
    }

    public void setEarnings(String earnings) {
        this.earnings = earnings;
    }

    public String getTask_count() {
        return task_count;
    }

    public void setTask_count(String task_count) {
        this.task_count = task_count;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
