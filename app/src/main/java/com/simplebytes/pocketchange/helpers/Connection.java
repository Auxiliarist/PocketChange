package com.simplebytes.pocketchange.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Connection {

    private Context context;

    public Connection(Context context){
        this.context = context;
    }

    public boolean isConnectingToInternet(){

        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        return info != null;
    }

}
