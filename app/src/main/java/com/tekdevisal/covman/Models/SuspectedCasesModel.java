package com.tekdevisal.covman.Models;

public class SuspectedCasesModel {
    String case_id;
    String first_name;
    String last_name;
    String phone_number;
    String date;
    String time;
    String org_name;

    public SuspectedCasesModel(String case_id, String first_name, String last_name, String phone_number,
                               String date,String time, String org_name) {
        this.case_id = case_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone_number = phone_number;
        this.date = date;
        this.time = time;
        this.org_name = org_name;
    }

    public String getCase_id() {
        return case_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getOrgName(){
        return org_name;
    }

}
