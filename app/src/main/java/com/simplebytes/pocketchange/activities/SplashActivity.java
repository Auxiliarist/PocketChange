package com.simplebytes.pocketchange.activities;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.simplebytes.pocketchange.authentication.SigninActivity;
import com.simplebytes.pocketchange.helpers.AppSingleton;
import com.simplebytes.pocketchange.helpers.Config;
import com.simplebytes.pocketchange.helpers.CustomRequest;
import com.simplebytes.pocketchange.helpers.PrefManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch()) {
            Intent skip = new Intent(this, IntroActivity.class);
            startActivity(skip);
            ActivityCompat.finishAffinity(SplashActivity.this);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, null);

        //Check if user is banned
        if(AppSingleton.getInstance().isConnected() && AppSingleton.getInstance().getId() != 0) {
            CustomRequest request = new CustomRequest(Request.Method.POST, Config.METHOD_ACCOUNT_AUTHORIZE, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    if (AppSingleton.getInstance().authorize(response)) {

                        if (AppSingleton.getInstance().getState() == Config.ACCOUNT_STATE_ENABLED) { //User is OK

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            ActivityCompat.finishAffinity(SplashActivity.this);

                        } else { // User is banned
                            AppSingleton.getInstance().Logout();
                            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
                            ActivityCompat.finishAffinity(SplashActivity.this);
                        }
                    } else { // Error Loading Data
                        startActivity(new Intent(getApplicationContext(), SigninActivity.class));
                        ActivityCompat.finishAffinity(SplashActivity.this);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    startActivity(new Intent(getApplicationContext(), SigninActivity.class));
                    ActivityCompat.finishAffinity(SplashActivity.this);
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("clientId", Config.CLIENT_ID);
                    params.put("accountId", Long.toString(AppSingleton.getInstance().getId()));
                    params.put("accessToken", AppSingleton.getInstance().getAccessToken());

                    return params;
                }
            };

            AppSingleton.getInstance().addToRequestQueue(request);

        }else{ //User hasn't created an account yet but isn't first launch

            Thread splashThread = new Thread() {
                @Override
                public void run() {
                    try {
                        int waited = 0;
                        while (waited < Config.splash_delay) {
                            sleep(100);
                            waited += 100;
                        }

                    } catch (InterruptedException e) {
                        // do nothing

                    } finally {

                        //finish();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        ActivityCompat.finishAffinity(SplashActivity.this);

                    }
                }
            };
            splashThread.start();

        }

    }
}
