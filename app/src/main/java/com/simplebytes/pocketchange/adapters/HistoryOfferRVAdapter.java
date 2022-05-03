package com.simplebytes.pocketchange.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplebytes.pocketchange.R;
import com.simplebytes.pocketchange.models.HistoryOffer;

import java.util.ArrayList;

public class HistoryOfferRVAdapter extends RecyclerView.Adapter<HistoryOfferRVAdapter.OfferViewHolder> {

    private ArrayList<HistoryOffer> dataSet;


    public HistoryOfferRVAdapter(ArrayList<HistoryOffer> data){
        this.dataSet = data;
    }

    @Override
    public OfferViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_history_offer_item, parent, false);

        return new OfferViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OfferViewHolder holder, int position) {
        holder.points.setText(dataSet.get(position).getPoints());
        holder.type.setText(dataSet.get(position).getType());
        holder.date.setText(dataSet.get(position).getDate());
        holder.imageView.setImageResource(dataSet.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public class OfferViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private ImageView imageView;
        private TextView type;
        private TextView date;
        private TextView points;

        public OfferViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.offerCV);
            imageView = (ImageView) itemView.findViewById(R.id.imageView5);
            type = (TextView) itemView.findViewById(R.id.textView39);
            date = (TextView) itemView.findViewById(R.id.textView40);
            points = (TextView) itemView.findViewById(R.id.textView41);

        }
    }
}
