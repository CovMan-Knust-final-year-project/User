package com.tekdevisal.covman.Models;

public class AttendanceModel {
    String venue;
    String date;
    String time;

    public AttendanceModel(String venue, String date, String time) {
        this.venue = venue;
        this.date = date;
        this.time = time;
    }

    public String getVenue() {
        return venue;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
