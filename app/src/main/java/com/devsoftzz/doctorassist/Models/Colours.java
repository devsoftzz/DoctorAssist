package com.devsoftzz.doctorassist.Models;

import android.graphics.Color;

import java.util.ArrayList;

public class Colours {

    private ArrayList<Integer> mColours = new ArrayList<>();

    public Colours() {
        mColours.add(Color.rgb(243,146,163));
        mColours.add(Color.rgb(133,208,204));
        mColours.add(Color.rgb(253,209,148));
        mColours.add(Color.rgb(148,208,253));
        mColours.add(Color.rgb(201,203,165));
        mColours.add(Color.rgb(167,165,204));
    }

    public ArrayList<Integer> getColours(){
        return mColours;
    }

    public int coloursSize(){
        return  mColours.size();
    }
}
