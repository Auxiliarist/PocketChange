package com.simplebytes.pocketchange.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MenuItem;

import com.simplebytes.pocketchange.R;
import com.simplebytes.pocketchange.adapters.TabAdapter;
import com.simplebytes.pocketchange.fragments.HistoryOffersFragment;
import com.simplebytes.pocketchange.fragments.HistoryRewardsFragment;

public class HistoryActivity extends BaseActivity {

    private TabLayout tabLayout;
    private TabAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_history;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("History");

        tabLayout = (TabLayout)findViewById(R.id.history_tab_layout);
        viewPager = (ViewPager)findViewById(R.id.history_viewpager_layout);

        //Create History Tabs
        if(viewPager != null)
        {
            SetUpViewPager(viewPager);
        }

        tabLayout.setupWithViewPager(viewPager);

    }

    private void SetUpViewPager(ViewPager viewPager)
    {
        adapter = new TabAdapter(getSupportFragmentManager());

        adapter.AddFragment(new HistoryOffersFragment(), "Offers");
        adapter.AddFragment(new HistoryRewardsFragment(), "Rewards");

        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
