package com.tekdevisal.covman.Models;

public class VaccinesModel {
    String id;
    String date;
    String first_name;
    String last_name;
    String latitude;
    String longitude;
    String phone_number;
    String user_id;

    public VaccinesModel(String id, String date, String first_name, String last_name, String latitude, String longitude, String phone_number, String user_id) {
        this.id = id;
        this.date = date;
        this.first_name = first_name;
        this.last_name = last_name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone_number = phone_number;
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getUser_id() {
        return user_id;
    }
}
