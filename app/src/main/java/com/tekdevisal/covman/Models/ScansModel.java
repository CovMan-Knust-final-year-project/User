package com.tekdevisal.covman.Models;

public class ScansModel {
    String id;
    String date;
    String time;
    String temperature;
    String status;

    public ScansModel(String id, String date, String time, String temperature, String status) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.temperature = temperature;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getStatus() {
        return status;
    }
}
