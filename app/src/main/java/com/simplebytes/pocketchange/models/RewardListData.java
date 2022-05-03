package com.simplebytes.pocketchange.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RewardListData
{
    public HashMap<String, List<Reward>> RewardData()
    {
        HashMap<String, List<Reward>> items = new HashMap<>();

        List<Reward> steam = new ArrayList<>();
        steam.add(new Reward("steam", "$5", 500));
        steam.add(new Reward("steam", "$10", 1000));
        steam.add(new Reward("steam", "$25", 2500));
        steam.add(new Reward("steam", "$50", 5000));

        List<Reward> gplay = new ArrayList<>();
        gplay.add(new Reward("gplay", "$10", 1000));
        gplay.add(new Reward("gplay", "$15", 1500));
        gplay.add(new Reward("gplay", "$25", 2500));
        gplay.add(new Reward("gplay", "$50", 5000));

        List<Reward> paypal = new ArrayList<>();
        paypal.add(new Reward("paypal", "$2", 200));
        paypal.add(new Reward("paypal", "$5", 500));
        paypal.add(new Reward("paypal", "$10", 1000));
        paypal.add(new Reward("paypal", "$25", 2500));
        paypal.add(new Reward("paypal", "$50", 5000));

        List<Reward> amazon = new ArrayList<>();
        amazon.add(new Reward("amazon", "$5", 500));
        amazon.add(new Reward("amazon", "$10", 1000));
        amazon.add(new Reward("amazon", "$25", 2500));
        amazon.add(new Reward("amazon", "$50", 5000));

        items.put("PayPal Payment", paypal);
        items.put("Amazon Gift Code", amazon);
        items.put("Steam Wallet Code", steam);
        items.put("Google Play Code", gplay);

        return items;
    }

}
