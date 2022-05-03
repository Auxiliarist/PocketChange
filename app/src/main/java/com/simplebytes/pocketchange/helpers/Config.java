package com.simplebytes.pocketchange.helpers;

import com.simplebytes.pocketchange.R;

public class Config
{
    public static String package_name = "com.simplebytes.pocketchange";

    // Server URL ie., Webpanel Hosted Url -
    // must be http://folder.example.com/
    // do-not use http://example.com/folder/
    public static String Base_Url = "http://pocketchange.biz/"; //"http://10.0.0.5/pocketchange/";
    public static String Daily_URL = Base_Url + "get/add_day.php";
    public static String Redeem_URL = Base_Url + "get_req.php";
    public static String Spend_URL = Base_Url + "get/add_red.php";
    public static String Award_URL = Base_Url + "get/add_point.php";
    public static String Points_URL = Base_Url + "get/get_point.php";
    public static String Referral_URL = Base_Url + "get/add_ref.php";
    public static String Tracker_URL = Base_Url + "get/track.php?user=";
    public static String TrackerRed_URL = Base_Url + "get/track_red.php?user=";
    public static String GetRefs_URL = Base_Url + "get/get_ref.php";
    public static String FCM_URL = Base_Url + "get/get_token.php";

    // Daily Reward Points
    public static int daily_reward = 5;

    public static int splash_delay = 500;

    // Icons for Offer Fragment
    public static int[] icons={
            R.drawable.ic_checkin_big,
            R.drawable.ic_click,
            R.drawable.ic_play,
            R.drawable.ic_clipboard,
            R.drawable.ic_toro,
            R.drawable.ic_sr };

    //Titles for Offer Fragment
    public static String[] titles ={
            "Daily Check-In",
            "Click & Earn",
            "Watch Videos",
            "Take Surveys",
            "OfferToro OfferWall",
            "SuperRewards OfferWall" };

    //Description for Offer Fragment Titles
    public static String[] descriptions={
            "Open Daily and Earn 5 Points",
            "Click on Ad and Earn 1 Point",
            "Watch Videos to Earn Points",
            "Take Fast and Easy Surveys to Earn Points",
            "Complete Offers and Earn Points",
            "Complete Offers and Earn Points" };

    // Share text and link for Share Button
    public static String share_text = "Hello, look what a beautiful app that I found here:";
    public static String share_link = "https://play.google.com/store/apps/details?id=com.simplebytes.pocketchange";

    // APP RATING
    public static String rate_later = "Perhaps Later";
    public static String rate_never = "No Thanks";
    public static String rate_yes= "Rate Now";
    public static String rate_message = "We hope you enjoy using %1$s. Would you like to help us by rating us in the Store?";
    public static String rate_title = "Enjoying our app?";


    //CONSTANTS
    private static final String API_FILE_EXTENSION = ".php";
    private static final String API_VERSION = "v1";
    public static final String METHOD_ACCOUNT_LOGIN = Base_Url + "api/" + API_VERSION + "/method/account.signin" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_SIGNUP = Base_Url + "api/" + API_VERSION + "/method/account.signup" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_RECOVERY = Base_Url + "api/" + API_VERSION + "/method/account.recovery" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_AUTHORIZE = Base_Url + "api/" + API_VERSION + "/method/account.authorize" + API_FILE_EXTENSION;
    public static final String METHOD_ACCOUNT_LOGOUT = Base_Url + "api/" + API_VERSION + "/method/account.logout" + API_FILE_EXTENSION;

    public static final String CLIENT_ID = "1";
    public static final int ACCOUNT_STATE_ENABLED = 0;
    public static final String PREFS_NAME = "MyApp_Settings";

    //API KEYS
    public static String pollfishKey = "358245db-05da-480d-a1dc-8cf9471b19a3";
    public static String appodealKey = "83a47aa981c4e26498826f76d237730352d9a6a3ecd165ef";
    public static String srAppHash = "nqokfzkukoi.363169555825";
    public static String userId = "a6b1e993-4770-4710-bc45-be4ad39fbdc9";
    public static String adxmiID = "55ebedb5d0306fce";
    public static String adxmiSecret = "ec6b7cfaeb46287f";
    public static String branchKey = "key_live_mlDsogT8mIn0RAobFL1Bffoizygf81Xx";

    public static String toroSecret = "8f292891fb41ad22c93f7ec0315cb636";
    public static String toroAppId = "2555";

}
