package com.devsoftzz.doctorassist.Models;

public class Medicine {

    String medicine;

    public Medicine(String medicine, int a, int b, int c, int days) {
        this.medicine = medicine;
        this.a = a;
        this.b = b;
        this.c = c;
        this.days = days;
    }

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    int a,b,c;
    int days;

}

