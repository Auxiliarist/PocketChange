package com.simplebytes.pocketchange.models;

import com.simplebytes.pocketchange.R;

public class HistoryOffer {

    private String points;
    private String type;
    private String date;
    private int image;


    public int getImage() {
        if(type.equals("Daily Reward"))
            image = R.drawable.ic_checkin_big;

        if(type.equals("Click And Earn"))
            image = R.drawable.ic_click;

        if(type.equals("Video Credit"))
            image = R.drawable.ic_play;

        if(type.equals("Pollfish Credit"))
            image = R.drawable.pollfish_indicator_right;

        if(type.equals("OfferToro Credit"))
            image = R.drawable.ic_toro;

        if(type.equals("SuperRewards Credit"))
            image = R.drawable.ic_sr;

        if(type.contains("Referral Points"))
            image = R.drawable.ic_branchio;

        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
