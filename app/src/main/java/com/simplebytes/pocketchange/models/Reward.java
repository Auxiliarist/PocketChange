package com.simplebytes.pocketchange.models;

import java.sql.Struct;
import java.util.EnumSet;

/**
 * Created by Donut on 2/21/2017.
 */

public class Reward {

    public final String name; //steam gplay amazon
    public final String amount;
    public final int points;

    public Reward(String name, String amount, int points)
    {
        this.name = name;
        this.amount = amount;
        this.points = points;
    }
}
