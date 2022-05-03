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
import com.simplebytes.pocketchange.adapters.HistoryOfferRVAdapter;
import com.simplebytes.pocketchange.helpers.AppSingleton;
import com.simplebytes.pocketchange.helpers.Config;
import com.simplebytes.pocketchange.models.HistoryOffer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryOffersFragment extends Fragment {

    ArrayList<HistoryOffer> offers = new ArrayList<>();
    final HistoryOfferRVAdapter adapter = new HistoryOfferRVAdapter(offers);

    public HistoryOffersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history_offers, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.offerRecyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);

        getData();

        recyclerView.setAdapter(adapter);

        return view;
    }

    private void getData() {
        String url = Config.Tracker_URL + AppSingleton.getInstance().getUsername();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    //User hasn't completed an offer
                    if(response.length() == 0 || AppSingleton.getInstance().getUsername().isEmpty())
                    {
                        offers.clear();
                        HistoryOffer offer = new HistoryOffer();
                        offer.setType("Start Completing Offers To Earn Points");
                        offer.setPoints("");
                        offer.setDate("");

                        offers.add(offer);

                        adapter.notifyDataSetChanged();
                    }

                    //User has offer history
                    if(response.length() > 0 && !AppSingleton.getInstance().getUsername().isEmpty()) {

                        offers.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject object = response.getJSONObject(i);
                            HistoryOffer offer = new HistoryOffer();

                            if (!object.isNull("type"))
                                offer.setType(object.getString("type"));
                            if (!object.isNull("date"))
                                offer.setDate(object.getString("date"));
                            if (!object.isNull("points"))
                                offer.setPoints("Points: +" + object.getString("points"));

                            offers.add(offer);
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
