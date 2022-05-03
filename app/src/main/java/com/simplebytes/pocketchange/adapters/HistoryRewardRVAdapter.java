package com.simplebytes.pocketchange.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplebytes.pocketchange.R;
import com.simplebytes.pocketchange.models.HistoryReward;

import java.util.ArrayList;

public class HistoryRewardRVAdapter extends RecyclerView.Adapter<HistoryRewardRVAdapter.RewardViewHolder> {

    private ArrayList<HistoryReward> dataSet;

    public HistoryRewardRVAdapter(ArrayList<HistoryReward> data){
        this.dataSet = data;
    }

    @Override
    public RewardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_history_reward_item, parent, false);

        return new RewardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HistoryRewardRVAdapter.RewardViewHolder holder, int position) {
        holder.points.setText(dataSet.get(position).getPoints());
        holder.type.setText(dataSet.get(position).getType());
        holder.date.setText(dataSet.get(position).getDate());
        holder.imageView.setImageResource(dataSet.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public class RewardViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private ImageView imageView;
        private TextView type;
        private TextView date;
        private TextView points;

        public RewardViewHolder(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.rewardCV);
            imageView = (ImageView) itemView.findViewById(R.id.imageView4);
            type = (TextView) itemView.findViewById(R.id.textView36);
            date = (TextView) itemView.findViewById(R.id.textView37);
            points = (TextView) itemView.findViewById(R.id.textView38);
        }
    }
}
