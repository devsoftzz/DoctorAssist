package com.devsoftzz.doctorassist.Models;

public class AppointmentPojo {
    String hospital;
    String date,time;
    int _id;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public AppointmentPojo() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public AppointmentPojo(String hospital, String date, String time) {
        this.hospital = hospital;
        this.date = date;
        this.time = time;
    }
}