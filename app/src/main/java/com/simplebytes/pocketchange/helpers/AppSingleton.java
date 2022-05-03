package com.simplebytes.pocketchange.helpers;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.simplebytes.pocketchange.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.branch.referral.Branch;

public class AppSingleton extends MultiDexApplication {

    public static final String TAG = AppSingleton.class.getSimpleName();
    private static AppSingleton mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private SharedPreferences sharedPref;

    private String username, accessToken, email, ip_addr;
    private long id;
    private int state, errorCode;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        Branch.getAutoInstance(this);

        mRequestQueue = getRequestQueue();
        sharedPref = this.getSharedPreferences(this.getApplicationContext().getString(R.string.settings_file), Context.MODE_PRIVATE);

        this.readData();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static synchronized AppSingleton getInstance() {
        if (mInstance == null)
            mInstance = new AppSingleton();

        return mInstance;
    }

    public boolean isConnected(){

        Connection con = new Connection(getApplicationContext());
        return con.isConnectingToInternet();
    }

    public Boolean authorize(JSONObject object) {

        try
        {
            if(object.has("error_code"))
                this.setErrorCode(object.getInt("error_code"));

            if(!object.has("error"))
            {
                return false;
            }

            if(object.getBoolean("error"))
                return false;

            if(!object.has("account"))
                return false;

            JSONArray accountArray = object.getJSONArray("account");

            if(accountArray.length() > 0) {
                JSONObject accountObj = (JSONObject)accountArray.get(0);

                this.setUsername(accountObj.getString("username"));
                this.setEmail(accountObj.getString("email"));
                this.setIp_addr(accountObj.getString("ip_addr"));
                this.setState(accountObj.getInt("state"));
            }

            this.setId(object.getLong("accountId"));
            this.setAccessToken(object.getString("accessToken"));

            this.saveData();

            return true;

        }catch (JSONException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public void Logout(){
        final String accessToken;
        final Long accountId;

        accessToken = this.getAccessToken();
        accountId = this.getId();

        if (AppSingleton.getInstance().isConnected() && AppSingleton.getInstance().getId() != 0) {

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Config.METHOD_ACCOUNT_LOGOUT, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                if(!response.getBoolean("error")) {
                                    //Logout success
                                }

                            } catch (JSONException e) {

                                e.printStackTrace();

                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    //Log.d("LOGOUT", error.toString());
                    AppSingleton.getInstance().removeData();
                    AppSingleton.getInstance().readData();

                    Branch.getInstance(getApplicationContext()).logout();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("accountId", Long.toString(accountId));
                    params.put("accessToken", accessToken);

                    return params;
                }
            };

            AppSingleton.getInstance().addToRequestQueue(jsonReq);

        }

        AppSingleton.getInstance().removeData();
        AppSingleton.getInstance().readData();

        Branch.getInstance(getApplicationContext()).logout();
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue( Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null)
            mRequestQueue.cancelAll(tag);
    }

    public void readData(){
        this.setId(sharedPref.getLong(getString(R.string.settings_account_id), 0));
        this.setUsername(sharedPref.getString(getString(R.string.settings_account_username), ""));
        this.setAccessToken(sharedPref.getString(getString(R.string.settings_account_access_token), ""));
    }

    public void saveData(){
        sharedPref.edit().putLong(getString(R.string.settings_account_id), this.getId()).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_username), this.getUsername()).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_access_token), this.getAccessToken()).apply();
    }

    private void removeData() {

        sharedPref.edit().putLong(getString(R.string.settings_account_id), 0).apply();
        sharedPref.edit().putString(getString(R.string.settings_account_username), "").apply();
        sharedPref.edit().putString(getString(R.string.settings_account_access_token), "").apply();
    }



    public RequestQueue getRequestQueue() {

        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return this.mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    public String getUsername() {

        return this.username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public long getId() {

        return this.id;
    }

    public void setId(long id) {

        this.id = id;
    }

    public String getAccessToken() {

        return this.accessToken;
    }

    public void setAccessToken(String accessToken) {

        this.accessToken = accessToken;
    }

    public void setErrorCode(int errorCode) {

        this.errorCode = errorCode;
    }

    public int getErrorCode() {

        return this.errorCode;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getEmail() {

        return this.email;
    }

    public void setIp_addr(String ip_addr) {

        this.ip_addr = ip_addr;
    }

    public String getIp_addr() {

        return this.ip_addr;
    }

    public void setState(int state) {

        this.state = state;
    }

    public int getState() {

        return this.state;
    }
}
