package com.simplebytes.pocketchange.adapters;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.simplebytes.pocketchange.R;
import com.simplebytes.pocketchange.models.Reward;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RewardExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private Context context;
    private List<String> expandableListGroups;
    private HashMap<String, List<Reward>> expandableListItems;

    public  RewardExpandableListAdapter(Context context, List<String> groups, HashMap<String, List<Reward>> items)
    {
        this.context = context;
        this.expandableListGroups = groups;
        this.expandableListItems = items;

        Sort();
    }


    private void Sort()
    {
        Collections.sort(expandableListGroups);
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final Reward childItem = (Reward) getChild(groupPosition, childPosition);

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_reward_item, null);
        }

        TextView expandedListTextView = (TextView) convertView.findViewById(R.id.expandedListItem);
        TextView expandedListPoints = (TextView)convertView.findViewById(R.id.expandedListPoints);
        expandedListTextView.setText(childItem.amount);
        expandedListTextView.setTextColor(Color.argb(255, 0, 102, 0));
        expandedListPoints.setText("- " + String.valueOf(childItem.points) + " Points");
        expandedListPoints.setTextColor(Color.RED);

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return this.expandableListItems.get(this.expandableListGroups.get(groupPosition)).size();
    }

    @Override
    public int getGroupCount() {
        return this.expandableListGroups.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.expandableListGroups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.expandableListItems.get(this.expandableListGroups.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String groupTitle = (String)getGroup(groupPosition);
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_reward_group, null);
        }
        TextView groupTitleTextView = (TextView)convertView.findViewById(R.id.groupTitle);
        ImageView groupTitleImageView = (ImageView)convertView.findViewById(R.id.groupImage);
        groupTitleTextView.setTypeface(null, Typeface.BOLD);
        groupTitleTextView.setText(groupTitle);

        if(groupTitleTextView.getText().equals("PayPal Payment"))
            groupTitleImageView.setImageResource(R.drawable.ic_paypal_logo);

        if(groupTitleTextView.getText().equals("Amazon Gift Code"))
            groupTitleImageView.setImageResource(R.drawable.ic_amazon_icon);

        if(groupTitleTextView.getText().equals("Steam Wallet Code"))
            groupTitleImageView.setImageResource(R.drawable.ic_steam);

        if(groupTitleTextView.getText().equals("Google Play Code"))
            groupTitleImageView.setImageResource(R.drawable.ic_googleplay_icon);

        LinearLayout linearLayout = (LinearLayout)convertView.findViewById(R.id.rewardGroupLinLay);
        DisplayMetrics dm = parent.getContext().getResources().getDisplayMetrics();
        int densityDpi = dm.densityDpi;

        if(densityDpi >= DisplayMetrics.DENSITY_XXHIGH) // 480
            linearLayout.setPadding(0, parent.getContext().getResources().getDimensionPixelSize(R.dimen.groupSizeXXHigh), 0, parent.getContext().getResources().getDimensionPixelSize(R.dimen.groupSizeXXHigh));
        else if(densityDpi >= DisplayMetrics.DENSITY_XHIGH) // 320
            linearLayout.setPadding(0, parent.getContext().getResources().getDimensionPixelSize(R.dimen.groupSizeXHigh), 0, parent.getContext().getResources().getDimensionPixelSize(R.dimen.groupSizeXHigh));
        else if(densityDpi >= DisplayMetrics.DENSITY_HIGH) // 240
            linearLayout.setPadding(0, parent.getContext().getResources().getDimensionPixelSize(R.dimen.groupSizeHigh), 0, parent.getContext().getResources().getDimensionPixelSize(R.dimen.groupSizeHigh));
        else
            linearLayout.setPadding(0, parent.getContext().getResources().getDimensionPixelSize(R.dimen.groupSizeReg), 0, parent.getContext().getResources().getDimensionPixelSize(R.dimen.groupSizeReg));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
