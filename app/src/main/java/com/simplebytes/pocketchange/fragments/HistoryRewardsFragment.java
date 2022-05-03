package com.simplebytes.pocketchange.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.simplebytes.pocketchange.R;
import com.simplebytes.pocketchange.adapters.HistoryRewardRVAdapter;
import com.simplebytes.pocketchange.helpers.AppSingleton;
import com.simplebytes.pocketchange.helpers.Config;
import com.simplebytes.pocketchange.models.HistoryReward;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryRewardsFragment extends Fragment {

    ArrayList<HistoryReward> rewards = new ArrayList<>();
    final HistoryRewardRVAdapter adapter = new HistoryRewardRVAdapter(rewards);


    public HistoryRewardsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_rewards, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rewardRecyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);

        getData();

        recyclerView.setAdapter(adapter);

        return view;
    }

    private void getData() {
        String url = Config.TrackerRed_URL + AppSingleton.getInstance().getUsername();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    ////User hasn't redeemed an item
                    if(response.length() == 0 || AppSingleton.getInstance().getUsername().isEmpty())
                    {
                        rewards.clear();
                        HistoryReward reward = new HistoryReward();
                        reward.setType("Use Your Points To Redeem Items");
                        reward.setPoints("");
                        reward.setDate("");

                        rewards.add(reward);

                        adapter.notifyDataSetChanged();
                    }

                    //User has redemption history
                    if(response.length() > 0 && !AppSingleton.getInstance().getUsername().isEmpty()) {

                        rewards.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject object = response.getJSONObject(i);
                            HistoryReward reward = new HistoryReward();

                            if (!object.isNull("type"))
                                reward.setType(object.getString("type"));
                            if (!object.isNull("date"))
                                reward.setDate(object.getString("date"));
                            if (!object.isNull("points"))
                                reward.setPoints("Points: -" + object.getString("points"));

                            rewards.add(reward);
                        }

                        adapter.notifyDataSetChanged();
                    }

                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("JSON", error.toString());
            }
        });

        AppSingleton.getInstance().addToRequestQueue(request);
    }
}
