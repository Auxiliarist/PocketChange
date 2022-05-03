package com.simplebytes.pocketchange.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.simplebytes.pocketchange.R;
import com.simplebytes.pocketchange.activities.MainActivity;
import com.simplebytes.pocketchange.authentication.SigninActivity;
import com.simplebytes.pocketchange.helpers.AppSingleton;
import com.simplebytes.pocketchange.helpers.Config;

import java.util.HashMap;
import java.util.Map;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class ShareFragment extends Fragment {

    private OnShareFragmentInteractionListener shareListener;

    TextView numInvited, numEarned;
    Button btnClaim, btnInv;
    int credits;
    final MainActivity activity = new MainActivity();

    public ShareFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);
        activity.branch = Branch.getInstance(getActivity().getApplicationContext());

        numInvited = (TextView)view.findViewById(R.id.textView31);
        numEarned = (TextView)view.findViewById(R.id.textView28);
        btnClaim = (Button)view.findViewById(R.id.button2);
        btnInv = (Button)view.findViewById(R.id.buttonInvite);

        numEarned.setText(String.valueOf(activity.branch.getCredits()));

        btnClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activity.branch.loadRewards(new Branch.BranchReferralStateChangedListener() {
                    @Override
                    public void onStateChanged(boolean changed, BranchError error) {

                    if(error == null && !AppSingleton.getInstance().getUsername().isEmpty()) {
                        credits = activity.branch.getCredits();
                        shareListener.onClaimInviteClick(credits);
                    } else {
                        startActivity(new Intent(getContext(), SigninActivity.class));
                    }

                    }
                });


            }
        });

        btnInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!AppSingleton.getInstance().getUsername().isEmpty()) {
                    shareListener.onInviteClick();
                }else {
                    startActivity(new Intent(getContext(), SigninActivity.class));
                    ActivityCompat.finishAffinity(getActivity());
                }
            }
        });

        GetNumRefs();

        return view;
    }


    private void GetNumRefs(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GetRefs_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(AppSingleton.getInstance().getUsername().isEmpty())
                    numInvited.setText("0");
                else
                    numInvited.setText(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Timeout error
                Toast.makeText(getContext(), "Error Loading Referrals. Check Internet Connection", Toast.LENGTH_SHORT).show();
                numInvited.setText("0");
            }
        }) {
            @Override
            protected Map<String, String> getParams()  {
                Map<String, String> params = new HashMap<>();
                params.put("username", AppSingleton.getInstance().getUsername());
                return params;
            }
        };

        AppSingleton.getInstance().addToRequestQueue(stringRequest);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnShareFragmentInteractionListener) {
            shareListener = (OnShareFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        shareListener = null;
    }

    public interface OnShareFragmentInteractionListener {
        void onClaimInviteClick(int credits);
        void onInviteClick();
    }

}
