package com.simplebytes.pocketchange.models;

import android.widget.ImageView;

import com.simplebytes.pocketchange.R;

public class HistoryReward {

    private String points;
    private String type;
    private String date;
    private int image;

    public int getImage() {

        if(type.contains("Amazon"))
            image = R.drawable.ic_amazon_icon;

        if(type.contains("Google"))
            image = R.drawable.ic_googleplay_icon;

        if(type.contains("Steam"))
            image = R.drawable.ic_steam;

        if(type.contains("PayPal"))
            image = R.drawable.ic_paypal_logo;

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
