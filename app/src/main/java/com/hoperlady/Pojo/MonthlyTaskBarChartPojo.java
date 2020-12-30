package com.hoperlady.Pojo;

import com.google.gson.Gson;

import java.util.ArrayList;

public class MonthlyTaskBarChartPojo {

    private String billingStartDate = "";
    private String billingEndDate = "";
    private ArrayList<WeekTaskBarChartPojo> barChartPojoArrayList;
    private String total_earnings = "";
    private String total_task = "";
    private String total_worked_hours = "";
    private String maxValues = "";

    public String getMaxValues() {
        return maxValues;
    }

    public void setMaxValues(String maxValues) {
        this.maxValues = maxValues;
    }

    public String getTotal_earnings() {
        return total_earnings;
    }

    public void setTotal_earnings(String total_earnings) {
        this.total_earnings = total_earnings;
    }

    public String getTotal_task() {
        return total_task;
    }

    public void setTotal_task(String total_task) {
        this.total_task = total_task;
    }

    public String getTotal_worked_hours() {
        return total_worked_hours;
    }

    public void setTotal_worked_hours(String total_worked_hours) {
        this.total_worked_hours = total_worked_hours;
    }

    public String getBillingStartDate() {
        return billingStartDate;
    }

    public void setBillingStartDate(String billingStartDate) {
        this.billingStartDate = billingStartDate;
    }

    public String getBillingEndDate() {
        return billingEndDate;
    }

    public void setBillingEndDate(String billingEndDate) {
        this.billingEndDate = billingEndDate;
    }

    public ArrayList<WeekTaskBarChartPojo> getBarChartPojoArrayList() {
        return barChartPojoArrayList;
    }

    public void setBarChartPojoArrayList(ArrayList<WeekTaskBarChartPojo> barChartPojoArrayList) {
        this.barChartPojoArrayList = barChartPojoArrayList;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
