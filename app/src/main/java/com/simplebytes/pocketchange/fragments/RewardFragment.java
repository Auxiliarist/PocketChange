package com.simplebytes.pocketchange.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.simplebytes.pocketchange.R;
import com.simplebytes.pocketchange.adapters.AnimatedExpandableListView;
import com.simplebytes.pocketchange.adapters.RewardExpandableListAdapter;
import com.simplebytes.pocketchange.authentication.SigninActivity;
import com.simplebytes.pocketchange.helpers.AppSingleton;
import com.simplebytes.pocketchange.helpers.Config;
import com.simplebytes.pocketchange.helpers.CustomRequest;
import com.simplebytes.pocketchange.models.Reward;
import com.simplebytes.pocketchange.models.RewardListData;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RewardFragment extends Fragment {

    private OnRewardFragmentInteractionListener rewardListener;

    private int previousItem = -1;
    String email;
    int totalPoints;

    private android.app.ProgressDialog progressDialog;
    AlertDialog.Builder dialog;
    AlertDialog.Builder emailDialog;

    private List<String> expandableListGroups;
    private HashMap<String, List<Reward>> expandableListItems;
    AnimatedExpandableListView expandableListView;
    RewardExpandableListAdapter expandableListAdapter;
    RewardListData data = new RewardListData();

    public RewardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reward, container, false);

        expandableListView = (AnimatedExpandableListView) view.findViewById(R.id.rewardListView);

        expandableListItems = data.RewardData();
        expandableListGroups = new ArrayList<>(expandableListItems.keySet());
        expandableListAdapter = new RewardExpandableListAdapter(getContext(), expandableListGroups, expandableListItems);

        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                GroupCollapse(groupPosition);
                return true;
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                //Perform SignIn Check
                if(!AppSingleton.getInstance().getUsername().isEmpty()) {

                    EmailWithdraw(expandableListGroups.get(groupPosition), expandableListItems.get(expandableListGroups.get(groupPosition)).get(childPosition).points, expandableListItems.get(expandableListGroups.get(groupPosition)).get(childPosition).amount);
                }else {
                    startActivity(new Intent(getContext(), SigninActivity.class));
                    ActivityCompat.finishAffinity(getActivity());
                }

                return  true;
            }
        });

        GetTotalPoints();

        return view;
    }

    private void GetTotalPoints(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.Points_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!response.isEmpty())
                        totalPoints = Integer.parseInt(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getContext(),error.toString(),Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("username", AppSingleton.getInstance().getUsername());
                return params;
            }

        };

        AppSingleton.getInstance().addToRequestQueue(stringRequest);
    }

    @TargetApi(23)
    public void GroupCollapse(int groupPosition)
    {
        if(expandableListView.isGroupExpanded(groupPosition))
        {
            //Collapse group
            expandableListView.collapseGroupWithAnimation(groupPosition);
            previousItem = -1;
        }else {
            //Collapse last expanded group & expand new
            if(previousItem != -1 && expandableListGroups.size() > previousItem)
            {
                //If we will now collapse group above us, we need to restore scroll position
                //to keep user looking at the same position
                boolean needsToRestore = previousItem < expandableListView.getFirstVisiblePosition();
                View firstChild = expandableListView.getChildAt(0);

                int restoreYOffset = (firstChild == null) ? 0 : (firstChild.getTop() - expandableListView.getPaddingTop());
                expandableListView.collapseGroupWithAnimation(previousItem);
                expandableListView.expandGroupWithAnimation(groupPosition);
                if(needsToRestore)
                {
                    int restorePosition = expandableListView.getFirstVisiblePosition() - expandableListItems.get(expandableListGroups.get(previousItem)).size();
                    expandableListView.setSelectionFromTop(restorePosition,restoreYOffset);
                }

                previousItem = groupPosition;
            }else{
                //No group to collapse, so expand immediately
                expandableListView.expandGroupWithAnimation(groupPosition);
                previousItem = groupPosition;
            }
        }


    }

    public void EmailWithdraw(final String ItemName, final int PointCost, final String CashCost)
    {
        emailDialog = new AlertDialog.Builder(getContext());
        final EditText edittext = new EditText(getContext());

        edittext.setHint("example@email.com");
        emailDialog.setView(edittext);
        emailDialog.setTitle("Please Enter Recipient Email");

        emailDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                email = edittext.getText().toString();
                WithdrawItem(ItemName, PointCost, CashCost, email);
            }
        });

        emailDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do nothing
                dialog.dismiss();
            }
        });

        emailDialog.show();

    }

    //FIX Points HERE (FIXED)
    public void WithdrawItem(final String ItemName, final int PointCost, final String CashCost, final String Email)
    {
        dialog = new AlertDialog.Builder(this.getContext());
        //int totalPointss = 1000;

        if(totalPoints >= PointCost)
        {
            dialog.setTitle("Confirm");
            dialog.setMessage("You are redeeming a " + CashCost + " " + ItemName + " to " + Email + " for the amount of " + PointCost + " points.");

            dialog.setPositiveButton("Correct", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog = android.app.ProgressDialog.show(getContext(), "Please Wait", "Sending Request...");
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    Toast.makeText(getContext(), "Withdraw worked!", Toast.LENGTH_SHORT).show();

                    Insert(ItemName, PointCost, CashCost, Email);
                }
            });

            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //do nothing
                    dialog.dismiss();
                }
            });

            dialog.show();
        }else{
            //Display dialog: not enough points
            dialog.setMessage("You don't have enough points!");
            dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //do nothing
                    dialog.dismiss();
                }
            });

            dialog.show();

        }
    }

    public void Insert(String ItemName, int PointCost, String CashCost, String Email)
    {
        String deviceName = Build.MODEL;
        String deviceMan = Build.MANUFACTURER;

        Calendar c = Calendar.getInstance();
        SimpleDateFormat dd = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        String CurrentDate = dd.format(c.getTime());

        String po = "" + PointCost;
        InsertToDatabase(ItemName, PointCost, CashCost, Email, deviceName, deviceMan, po, CurrentDate);
    }

    private void SpendPoints(int points, final String type)
    {
        final String amount = Integer.toString(points);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        final String CurrentDate = dateFormat.format(calendar.getTime());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.Spend_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "Server Error! Please Try Again Later", Toast.LENGTH_SHORT).show();
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

        AppSingleton.getInstance().addToRequestQueue(stringRequest);
    }

    private void InsertToDatabase(final String ItemName, final int PointCost, final String CashCost, final String Email, final String DeviceName, final String DeviceMan, final String po, final String Date)
    {
        if(AppSingleton.getInstance().isConnected()){

            CustomRequest jsonReq = new CustomRequest(Request.Method.POST, Config.Redeem_URL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    SpendPoints(PointCost, CashCost + " - " + ItemName);

                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Congratulations!");
                    alert.setMessage("Your order has been received! Please allow for up to 48hrs for delivery.");
                    //set custom icon
                    alert.setIcon(R.drawable.custom_img);

                    if(progressDialog.isShowing())
                        progressDialog.dismiss();

                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //reload activity
                            getActivity().finish();
                            startActivity(getActivity().getIntent());
                        }
                    });

                    alert.setNeutralButton("Share", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            rewardListener.onShareRewardClick(CashCost, ItemName);
                        }
                    });

                    alert.show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "Something Went Wrong, Please Try Again Later", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", AppSingleton.getInstance().getUsername());
                    params.put("user_input", Email);
                    params.put("deviceName", DeviceName);
                    params.put("deviceMan", DeviceMan);
                    params.put("gift_name", ItemName);
                    params.put("amount", CashCost);
                    params.put("points", po);
                    params.put("Current_Date", Date);

                    return params;
                }
            };

            AppSingleton.getInstance().addToRequestQueue(jsonReq);

        }else{
            Toast.makeText(getContext().getApplicationContext(), "Please check your Internet Connection", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRewardFragmentInteractionListener) {
            rewardListener = (OnRewardFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        rewardListener = null;
    }

    public interface OnRewardFragmentInteractionListener {
        void onShareRewardClick(String worth, String name);
    }

}
