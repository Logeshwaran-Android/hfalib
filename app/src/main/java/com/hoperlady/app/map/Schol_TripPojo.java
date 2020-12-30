package com.hoperlady.app.map;

public class Schol_TripPojo {
    private String user_id="";
    private String user_nme="";
    private String user_email="";
    private String phone_number="";
    private String pickup_location="";
    private double pickup_lat=0.0;
    private double pickup_lon=0.0;
    private String pickup_time="";
    private String stud_img="";
    private String student_status="";
    public String getParent_id() {
        return parent_id;
    }
    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }
    private String parent_id="";
    public String getStudent_status() {
        return student_status;
    }
    public void setStudent_status(String student_status) {
        this.student_status = student_status;
    }
    public String getStud_img() {
        return stud_img;
    }
    public void setStud_img(String stud_img) {
        this.stud_img = stud_img;
    }
    public String getUser_id() {
        return user_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getUser_nme() {
        return user_nme;
    }
    public void setUser_nme(String user_nme) {
        this.user_nme = user_nme;
    }
    public String getUser_email() {
        return user_email;
    }
    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }
    public String getPhone_number() {
        return phone_number;
    }
    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
    public String getPickup_location() {
        return pickup_location;
    }
    public void setPickup_location(String pickup_location) {
        this.pickup_location = pickup_location;
    }
    public double getPickup_lat() {
        return pickup_lat;
    }
    public void setPickup_lat(double pickup_lat) {
        this.pickup_lat = pickup_lat;
    }
    public double getPickup_lon() {
        return pickup_lon;
    }
    public void setPickup_lon(double pickup_lon) {
        this.pickup_lon = pickup_lon;
    }
    public String getPickup_time() {
        return pickup_time;
    }
    public void setPickup_time(String pickup_time) {
        this.pickup_time = pickup_time;
    }
    public String getDrop_time() {
        return drop_time;
    }
    public void setDrop_time(String drop_time) {
        this.drop_time = drop_time;
    }
    public String getDrop_location() {
        return drop_location;
    }
    public void setDrop_location(String drop_location) {
        this.drop_location = drop_location;
    }
    public String getDrop_lat() {
        return drop_lat;
    }
    public void setDrop_lat(String drop_lat) {
        this.drop_lat = drop_lat;
    }
    public String getDrop_lon() {
        return drop_lon;
    }
    public void setDrop_lon(String drop_lon) {
        this.drop_lon = drop_lon;
    }
    private String drop_time="";
    private String drop_location="";
    private String drop_lat="";
    private String drop_lon="";
}