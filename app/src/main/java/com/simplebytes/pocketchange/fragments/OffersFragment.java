package com.simplebytes.pocketchange.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.simplebytes.pocketchange.R;
import com.simplebytes.pocketchange.adapters.OffersListAdapter;
import com.simplebytes.pocketchange.authentication.SigninActivity;
import com.simplebytes.pocketchange.helpers.AppSingleton;
import com.simplebytes.pocketchange.helpers.Config;

public class OffersFragment extends Fragment {

    private OnOfferFragmentInteractionListener offerListener;

    public OffersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_offers, container, false);

        ListView listView = (ListView)view.findViewById(R.id.offerListView);
        OffersListAdapter adapter = new OffersListAdapter(getContext(), Config.titles, Config.icons, Config.descriptions);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Perform SignIn Check
                if(!AppSingleton.getInstance().getUsername().isEmpty()) {

                    offerListener.onOfferClick(parent, view, position, id);
                }else {
                    startActivity(new Intent(getContext(), SigninActivity.class));
                    ActivityCompat.finishAffinity(getActivity());
                }
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnOfferFragmentInteractionListener) {
            offerListener = (OnOfferFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        offerListener = null;
    }


    public interface OnOfferFragmentInteractionListener {
        void onOfferClick(AdapterView<?> parent, View view, int position, long id);
    }
}
