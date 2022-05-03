package com.simplebytes.pocketchange.activities;

// SET S2S for Pollfish

//Thurs - Fix S2S, Set Branch Links On Website
//Fri - Progaurd Stuff

//Android
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//Volley
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

//Ads
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.AppodealSettings;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.offertoro.sdk.OTOfferWallSettings;
import com.offertoro.sdk.interfaces.OfferWallListener;
import com.offertoro.sdk.sdk.OffersInit;
import com.playerize.superrewards.SRUserPoints;
import com.playerize.superrewards.SuperRewards;
import com.pollfish.interfaces.PollfishSurveyCompletedListener;
import com.pollfish.interfaces.PollfishUserNotEligibleListener;
import com.pollfish.main.PollFish;
import com.pollfish.main.PollFish.ParamsBuilder;

//Mine
import com.simplebytes.pocketchange.R;
import com.simplebytes.pocketchange.adapters.TabAdapter;
import com.simplebytes.pocketchange.authentication.SigninActivity;
import com.simplebytes.pocketchange.fragments.OffersFragment;
import com.simplebytes.pocketchange.fragments.RewardFragment;
import com.simplebytes.pocketchange.fragments.ShareFragment;
import com.simplebytes.pocketchange.helpers.AppSingleton;
import com.simplebytes.pocketchange.helpers.CheckSdkConfig;
import com.simplebytes.pocketchange.helpers.Config;
import com.simplebytes.pocketchange.helpers.PrefManager;

//Java
import net.adxmi.android.AdManager;
import net.adxmi.android.os.EarnPointsOrderList;
import net.adxmi.android.os.OffersBrowserConfig;
import net.adxmi.android.os.OffersManager;
import net.adxmi.android.os.PointsChangeNotify;
import net.adxmi.android.os.PointsEarnNotify;
import net.adxmi.android.os.PointsManager;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

//Branch
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.SharingHelper;
import io.branch.referral.util.BranchEvent;
import io.branch.referral.util.LinkProperties;
import io.branch.referral.util.ShareSheetStyle;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PollfishSurveyCompletedListener, PollfishUserNotEligibleListener, OffersFragment.OnOfferFragmentInteractionListener,
        ShareFragment.OnShareFragmentInteractionListener, RewardFragment.OnRewardFragmentInteractionListener, PointsChangeNotify,
        PointsEarnNotify {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TabLayout tabLayout;
    private TabAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private ActionBarDrawerToggle drawerToggle;
    private PrefManager prefManager;
    public RequestQueue mRequestQueue;
    private FirebaseAnalytics mFirebaseAnalytics;

    public Branch branch;
    BranchUniversalObject branchUniversalObject;
    ShareSheetStyle shareSheetStyle;
    LinkProperties shareLink;
    LinkProperties refLink;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViews();
        initNavHeader();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        prefManager = new PrefManager(this);
        if (prefManager.isUserId()) {

            UUID uid = UUID.fromString(Config.userId);
            String id = uid.randomUUID().toString();

            SharedPreferences settings = getSharedPreferences(Config.PREFS_NAME, MODE_PRIVATE);
            // Writing data to SharedPreferences
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("user_id", id);
            editor.apply();

            prefManager.setUserId(false);
        }
        mRequestQueue = AppSingleton.getInstance().getRequestQueue();

        //AppRate
        initAppRate();

        //Ads
        initAppodeal();
        CheckSuperRewards();
        //initAdxmi();
        initToro();

        //Firebase
        initFBMessaging();

        //check whether the permission of READ_PHONE_STATE is granted
        //checkReadPhoneStatePermission();
    }

    private void initViews(){

        //Instantiate Views
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView = (NavigationView)findViewById(R.id.nav_view);
        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        viewPager = (ViewPager)findViewById(R.id.viewpager_layout);

        //Remove Title From AppBar
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        //ViewPager and Tabs
        if(viewPager != null)
            SetUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        //Navigation Drawer
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

    }

    private void initBranch(){

        branch = Branch.getInstance(getApplicationContext(), Config.branchKey);

        if(AppSingleton.getInstance().getUsername() != null)
            branch.setIdentity(AppSingleton.getInstance().getUsername());


        branch.initSession(new Branch.BranchUniversalReferralInitListener() {
            @Override
            public void onInitFinished(BranchUniversalObject branchUniversalObject, LinkProperties linkProperties, BranchError error) {

                JSONObject installParams = branch.getFirstReferringParams();

                try {

                    if ((Boolean) installParams.get("+is_first_session") && (Boolean) installParams.get("+clicked_branch_link")) {

                        Map<String, String> refMap = branchUniversalObject.getMetadata();
                        String userId = refMap.get("referrer_id");

                        AddReferral(userId);
                    }

                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, this.getIntent().getData(), this);

        branch.loadRewards();

        branchUniversalObject = new BranchUniversalObject()
                .setTitle("Pocket Change - Rewards App")
                .setContentImageUrl("http://pocketchange.biz/uploads/image.jpg")
                .addContentMetadata("referrer_id", AppSingleton.getInstance().getUsername());

        branchUniversalObject.userCompletedAction(BranchEvent.VIEW);

        shareLink = new LinkProperties()
                .setFeature(Branch.FEATURE_TAG_SHARE)
                .addControlParameter("$fallback_url", Config.Base_Url);

        refLink = new LinkProperties()
                .setFeature(Branch.FEATURE_TAG_REFERRAL)
                .addControlParameter("$fallback_url", Config.Base_Url);

        shareSheetStyle = new ShareSheetStyle(MainActivity.this, "Check this out!", "I just found this new app called Pocket Change where you can earn free gift cards and PayPal money. " +
                                                                                    "It's really cool, Check it out: ")
                .setCopyUrlStyle(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.ic_menu_set_as), "Copy Link", "Added to clipboard")
                .setMoreOptionStyle(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.ic_menu_more), "Use a different app")
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.TWITTER)
                .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
                .setAsFullWidthStyle(true)
                .setSharingTitle("Share With");

    }

    private void initAppodeal() {

        SharedPreferences settings = getSharedPreferences(Config.PREFS_NAME, MODE_PRIVATE);
        String userId = settings.getString("user_id", Config.userId);

        Appodeal.disableNetwork(this, "liverail");
        Appodeal.disableNetwork(this, "tapjoy");
        Appodeal.disableNetwork(this, "inner-active");
        Appodeal.disableLocationPermissionCheck();
        AppodealSettings.muteVideosIfCallsMuted(true);

        Appodeal.getUserSettings(this).setUserId(userId);

        Appodeal.initialize(this, Config.appodealKey, Appodeal.REWARDED_VIDEO|Appodeal.INTERSTITIAL);

        Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {
            boolean isLoaded = false;

            @Override
            public void onRewardedVideoLoaded() {
                isLoaded = true;

                if(isLoaded) {
                    Toast.makeText(MainActivity.this, "New Video Available", Toast.LENGTH_SHORT).show();
                    isLoaded = false;
                }
            }


            @Override
            public void onRewardedVideoFailedToLoad() {

                if (!isLoaded){

                    Toast.makeText(MainActivity.this, "No video available right now. Try again later", Toast.LENGTH_SHORT).show();
                    isLoaded = true;
                }
            }

            @Override
            public void onRewardedVideoShown() {
                Toast.makeText(getApplicationContext(), "Watch entire video to get Points", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoFinished(int amount, String currency) {

                //Toast.makeText(getApplicationContext(), String.format(Locale.US, "Video Finished. Credited: %d %s", amount, "Points"), Toast.LENGTH_SHORT).show();

                AwardPoints(amount, "Video Credit");

                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));

                PollFish.hide();
            }

            @Override
            public void onRewardedVideoClosed(boolean finished) {
                PollFish.hide();
            }

        });

        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
            boolean isLoaded = false;

            @Override
            public void onInterstitialLoaded(boolean b) {
                isLoaded = true;

                if(isLoaded) {
                    Toast.makeText(MainActivity.this, "New Click & Earn Available", Toast.LENGTH_SHORT).show();
                    isLoaded = false;
                }
            }

            @Override
            public void onInterstitialFailedToLoad() {
                if (!isLoaded) {
                    Toast.makeText(MainActivity.this, "No Click & Earn available right now. Try again later", Toast.LENGTH_SHORT).show();
                    isLoaded = true;
                }
            }

            @Override
            public void onInterstitialShown() {

            }

            @Override
            public void onInterstitialClicked() {
                AwardPoints(1, "Click And Earn");
            }

            @Override
            public void onInterstitialClosed() {
                PollFish.hide();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                PollFish.hide();
            }
        });

        PollFish.hide();
    }

    private void initAdxmi(){

        AdManager.getInstance(this).setEnableDebugLog(true);
        AdManager.getInstance(this).init(Config.adxmiID, Config.adxmiSecret);

        OffersManager.setUsingServerCallBack(false);

        OffersManager.getInstance(this).onAppLaunch();
        OffersManager.getInstance(this).setCustomUserId(AppSingleton.getInstance().getUsername());

        CheckSdkConfig.checkOfferConfig(this);

        PointsManager.setEnableEarnPointsToastTips(false);
        PointsManager.getInstance(this).registerNotify(this);
        PointsManager.getInstance(this).registerPointsEarnNotify(this);

        OffersBrowserConfig.setBrowserTitleBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        OffersBrowserConfig.setPointsLayoutVisibility(false);
        OffersBrowserConfig.setBrowserTitleText(getString(R.string.title_diy_offer));
    }

    private void initToro(){

        OTOfferWallSettings.getInstance().configInit(Config.toroAppId, Config.toroSecret, AppSingleton.getInstance().getUsername());

        OffersInit.getInstance().create(this);

        OffersInit.getInstance().setOfferWallListener(new OfferWallListener() {
            @Override
            public void onOTOfferWallInitSuccess() {

            }

            @Override
            public void onOTOfferWallInitFail(String s) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onOTOfferWallOpened() {

            }

            @Override
            public void onOTOfferWallCredited(double credits, double totalCredits) {
                branchUniversalObject.userCompletedAction("completeoffer");
            }

            @Override
            public void onOTOfferWallClosed() {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
    }

    private void ShowSuperRewards() {
        prefManager = new PrefManager(MainActivity.this);
        SharedPreferences settings = getSharedPreferences(Config.PREFS_NAME, MODE_PRIVATE);
        String userId = settings.getString("user_id", Config.userId);

        SuperRewards sr = new SuperRewards(getResources(), Config.package_name);
        sr.showOffers(MainActivity.this, Config.srAppHash, userId);
    }

    private void CheckSuperRewards() {

        prefManager = new PrefManager(MainActivity.this);
        SharedPreferences settings = getSharedPreferences(Config.PREFS_NAME, MODE_PRIVATE);
        String userId = settings.getString("user_id", Config.userId);
        SRUserPoints userPoints = new SRUserPoints(getApplicationContext());

        if (userPoints.updatePoints(Config.srAppHash, userId)) {
            int totalpoints = userPoints.getNewPoints();

            if (totalpoints > 0) {
                if(AppSingleton.getInstance().isConnected()) {

                    AwardPoints(totalpoints, "SuperRewards Credit");
                    branchUniversalObject.userCompletedAction("completeoffer");

                }else{
                    Toast.makeText(getApplicationContext(),"Couldn't Update SuperRewards Points. Check internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void initNavHeader() {

        LinearLayout layout = (LinearLayout)navigationView.getHeaderView(0).findViewById(R.id.navheader_layout);
        TextView header = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navheader_welcome);
        TextView username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.navheader_username);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartSignIn(v);
            }
        });

        if(AppSingleton.getInstance().getUsername().isEmpty())
        {
            header.setText(R.string.navheader_welcome);
            username.setText(getString(R.string.navheader_notsignedin));
        }else{
            header.setText(getString(R.string.navheader_signedin));
            username.setText(AppSingleton.getInstance().getUsername());
        }
    }

    private void SetUpViewPager(ViewPager viewPager) {

        viewPagerAdapter = new TabAdapter(getSupportFragmentManager());
        viewPagerAdapter.AddFragment(new OffersFragment(), "Offers");
        viewPagerAdapter.AddFragment(new ShareFragment(), "Share");
        viewPagerAdapter.AddFragment(new RewardFragment(), "Redeem");

        viewPager.setAdapter(viewPagerAdapter);
    }

    private void initAppRate(){

      android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this)
              .setTitle(Config.rate_title)
              .setMessage(String.format(Config.rate_message, getString(R.string.app_name)))
              .setNeutralButton(Config.rate_later, null)
              .setPositiveButton(Config.rate_yes, null)
              .setNegativeButton(Config.rate_never, null);

        new com.simplebytes.pocketchange.rate.AppRate(this)
                .setShowIfAppHasCrashed(true)
                .setMinDaysUntilPrompt(1)
                .setMinLaunchesUntilPrompt(4)
                .setCustomDialog(builder)
                .init();

    }

    private void initFBMessaging(){

        final String fcm_token = FirebaseInstanceId.getInstance().getToken();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.FCM_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("name", AppSingleton.getInstance().getUsername());
                params.put("fcm_id",fcm_token);
                return params;
            }

        };

        mRequestQueue.add(stringRequest);
    }

    private void Daily(int p, final String type) {

        final String points = Integer.toString(p);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dd = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        final String Current_Date = dd.format(c.getTime());

        final String v0 ="0";
        final String v1 ="1";
        final String v2 ="2";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.Daily_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if(response.intern().equals(v1)){
                    AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
                    alert.setTitle("Success!");
                    alert.setMessage("Daily Points Successfully Redeemed! Come Back Tomorrow For More Free Points!");
                    alert.setCanceledOnTouchOutside(false);

                    //add icon
                    alert.setIcon(R.drawable.custom_img);

                    alert.setButton(BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });
                    alert.show();
                }

                if(response.intern().equals(v0)){
                    AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
                    alert.setTitle("Already Claimed");
                    alert.setMessage("You already received today's daily reward. Please try again at a later time");
                    alert.setCanceledOnTouchOutside(false);

                    alert.setButton(BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //do nothing
                        }
                    });
                    alert.show();
                }

                if(response.intern().equals(v2)){
                    Toast.makeText(getApplicationContext(), "There was a server problem, please try again later", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", AppSingleton.getInstance().getUsername());
                params.put("points",points);
                params.put("type", type);
                params.put("date", Current_Date);
                return params;
            }
        };

        mRequestQueue.add(stringRequest);
    }

    public void AwardPoints(int points, final String type) {

        final String amount = Integer.toString(points);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        final String CurrentDate = dateFormat.format(calendar.getTime());

        if(!AppSingleton.getInstance().getUsername().isEmpty()) {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.Award_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Server Error! Please Try Again Later", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", AppSingleton.getInstance().getUsername());
                    params.put("points", amount);
                    params.put("type", type);
                    params.put("date", CurrentDate);

                    return params;
                }
            };

            mRequestQueue.add(stringRequest);
        }


    }

    public void AddReferral(final String userID) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        final String CurrentDate = dateFormat.format(calendar.getTime());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.Referral_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Server Error! Please Try Again Later", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("userID", userID);
                params.put("refID", AppSingleton.getInstance().getUsername());
                params.put("date", CurrentDate);

                return params;
            }
        };


        mRequestQueue.add(stringRequest);
    }

    public void StartSignIn(View v) {

        if(AppSingleton.getInstance().getUsername().isEmpty()) {
            startActivity(new Intent(getApplicationContext(), SigninActivity.class));
            ActivityCompat.finishAffinity(MainActivity.this);
        }
    }

    private void destroyOfferWall(){
        PointsManager.getInstance(this).unRegisterNotify(this);
        PointsManager.getInstance(this).unRegisterPointsEarnNotify(this);
        OffersManager.getInstance(this).onAppExit();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////  System Callbacks

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.Points_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(AppSingleton.getInstance().getUsername().isEmpty())
                    menu.findItem(R.id.points).setTitle("Points : 0");
                else
                    menu.findItem(R.id.points).setTitle("Points : " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Timeout error
                Toast.makeText(MainActivity.this, "Error Loading Points, Check Internet Connection", Toast.LENGTH_SHORT).show();
                menu.findItem(R.id.points).setTitle("Points : 0");
            }
        }) {
            @Override
            protected Map<String, String> getParams()  {
                Map<String, String> params = new HashMap<>();
                params.put("username", AppSingleton.getInstance().getUsername());
                return params;
            }
        };

        mRequestQueue.add(stringRequest);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId())
        {
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.START, true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId())
        {
            case R.id.nav_history:
                item.setChecked(false);
                item.setCheckable(false);
                startActivity(new Intent(getBaseContext(), HistoryActivity.class));
                break;

            case R.id.nav_privacy:
                item.setChecked(false);
                item.setCheckable(false);
                startActivity(new Intent(getBaseContext(), PrivacyActivity.class));
                break;

            case R.id.nav_about:
                item.setChecked(false);
                item.setCheckable(false);
                startActivity(new Intent(getBaseContext(), AboutActivity.class));
                break;
        }

        //Close drawer on item click
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        PollFish.ParamsBuilder paramsBuilder = new ParamsBuilder(Config.pollfishKey)
                .customMode(true)
                .releaseMode(true)
                .pollfishSurveyCompletedListener(this)
                .pollfishUserNotEligibleListener(this)
                .requestUUID(AppSingleton.getInstance().getUsername())
                .build();

        PollFish.initWith(this, paramsBuilder);
        PollFish.hide();
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onStart() {
        super.onStart();
        initBranch();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        this.setIntent(intent);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        PollFish.ParamsBuilder paramsBuilder = new ParamsBuilder(Config.pollfishKey)
                .customMode(true)
                .releaseMode(true)
                .pollfishSurveyCompletedListener(this)
                .pollfishUserNotEligibleListener(this)
                .requestUUID(AppSingleton.getInstance().getUsername())
                .build();

        PollFish.initWith(this, paramsBuilder);
        PollFish.hide();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //destroyOfferWall();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////  Fragment Callbacks

    @Override
    public void onOfferClick(AdapterView<?> parent, View view, int position, long id) {

        switch (position) {
            case 0: // Daily
                Daily(Config.daily_reward, "Daily Reward");

               /* if (hasGetReadPhoneStatePermission()) {
                    OffersManager.getInstance(MainActivity.this).showOffersWall();
                } else {
                    requestReadPhoneStatePermission();
                }*/

                break;

            case 1: // Click & Earn

                if (Appodeal.isLoaded(Appodeal.INTERSTITIAL))
                    Appodeal.show(this, Appodeal.INTERSTITIAL);
                else
                    Toast.makeText(getApplicationContext(), "No Click & Earn available right now. Try again later", Toast.LENGTH_SHORT).show();
                break;

            case 2: // Videos

                if (Appodeal.isLoaded(Appodeal.REWARDED_VIDEO))
                    Appodeal.show(MainActivity.this, Appodeal.REWARDED_VIDEO);
                else
                    Toast.makeText(getApplicationContext(), "No Video available right now. Try again later", Toast.LENGTH_SHORT).show();
                break;

            case 3: // Surveys
                PollFish.show();
                break;

            case 4: // ironSource Offerwall
                if(OTOfferWallSettings.getInstance().isInitialized())
                    OffersInit.getInstance().showOfferWall(this);
                else
                    Toast.makeText(getApplicationContext(), "OfferToro is not available right now. Check internet connection", Toast.LENGTH_SHORT).show();
                break;

            case 5: // SuperRewards Offerwall
                ShowSuperRewards();
                break;
        }
    }

    @Override
    public void onClaimInviteClick(int inviteCredits) {

        if(inviteCredits > 0) {

            Branch.getInstance().redeemRewards(inviteCredits);
            AwardPoints(inviteCredits, "Referral Points");

            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onInviteClick() {
        //create link, show share sheet
        branchUniversalObject.generateShortUrl(this, shareLink, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {

                if(error == null) {
                    branchUniversalObject.setCanonicalIdentifier("invite");
                    branchUniversalObject.showShareSheet(MainActivity.this, shareLink, shareSheetStyle, null);
                }

            }
        });
    }

    @Override
    public void onShareRewardClick(String worth, String name) {

        branchUniversalObject.setCanonicalIdentifier("reward").setContentDescription("I just received a free " + worth + " " + name + " using this app I found!").setTitle("Check This Out!");

        branchUniversalObject.showShareSheet(MainActivity.this, shareLink, shareSheetStyle, new Branch.BranchLinkShareListener() {
            @Override
            public void onShareLinkDialogLaunched() {

            }

            @Override
            public void onShareLinkDialogDismissed() {

            }

            @Override
            public void onLinkShareResponse(String sharedLink, String sharedChannel, BranchError error) {
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, null);
            }

            @Override
            public void onChannelSelected(String channelName) {
                branch.userCompletedAction(BranchEvent.SHARE_STARTED);
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////  Adxmi Stuff

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            switch (requestCode) {
                case 1: {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "You can now use Adxmi OfferWall", Toast.LENGTH_SHORT).show();
                    } else {
                        //had not get the permission
                        Toast.makeText(this, "You need to enable the Write External Storage Permission to use Adxmi Offerwall", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkReadPhoneStatePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasGetReadPhoneStatePermission()) {
                requestReadPhoneStatePermission();
            }
        }
    }

    private boolean hasGetReadPhoneStatePermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestReadPhoneStatePermission() {
        //You can choose a more friendly notice text. And you can choose any view you like, such as dialog.
        Toast.makeText(this, "Adxmi OfferWall Requires Read Phone State Permission", Toast.LENGTH_SHORT).show();
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_PHONE_STATE}, 1);
    }

    @Override
    public void onPointBalanceChange(int i) {

    }

    @Override
    public void onPointEarn(Context context, EarnPointsOrderList earnPointsOrderList) {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////  Pollfish Callbacks

    @Override
    public void onUserNotEligible() {

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("Survey Not Rewarded");
        alert.setMessage("Sorry, you are not eligible to finish this survey because of an answer you previously gave. " +
                    "However, you can try another server for another chance to earn points.");

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
    }

    @Override
    public void onPollfishSurveyCompleted(boolean playfulSurvey, int surveyPrice) {

    }

}
