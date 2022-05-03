package com.simplebytes.pocketchange.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplebytes.pocketchange.R;

public class OffersListAdapter extends ArrayAdapter<String> {

    private Context context;
    private int icons[];
    private String[] titles;
    private String[] descriptions;

    public OffersListAdapter(Context context, String[] titles, int[] icons, String[] desc) {

        super(context, R.layout.list_offer_item, titles);

        this.context = context;
        this.titles = titles;
        this.icons = icons;
        this.descriptions = desc;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_offer_item, null);
        }

        ImageView itemIcon = (ImageView) convertView.findViewById(R.id.imageView);
        TextView itemTitle = (TextView) convertView.findViewById(R.id.textView);
        TextView itemDesc = (TextView) convertView.findViewById(R.id.textView2);

        itemIcon.setImageResource(icons[position]);
        itemTitle.setText(titles[position]);
        itemDesc.setText(descriptions[position]);

        /*TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int actionBarHeight = parent.getResources().getDimensionPixelSize(tv.resourceId);


        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);*/

        DisplayMetrics dm = parent.getContext().getResources().getDisplayMetrics();
        int densityDpi = dm.densityDpi;

        int height = parent.getHeight();// + actionBarHeight; //150; //size.y;

        if(densityDpi <= DisplayMetrics.DENSITY_XHIGH)
        {
            height = parent.getHeight();
        }

        int itemHeight;
        if (getCount() >= 6)
            itemHeight = (height/6) - 3;
        else
            itemHeight = height/getCount();

        convertView.setMinimumHeight(itemHeight);

        return convertView;
    }

    @Override
    public int getCount() {
        return icons.length;
    }

}
