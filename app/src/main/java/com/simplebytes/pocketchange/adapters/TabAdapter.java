package com.simplebytes.pocketchange.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentPagerAdapter {

    private List<Fragment> Fragments;
    private List<String> FragmentNames;

    public TabAdapter(FragmentManager fm)
    {
        super(fm);
        Fragments = new ArrayList<>();
        FragmentNames = new ArrayList<>();
    }

    public void AddFragment(Fragment fragment, String name)
    {
        Fragments.add(fragment);
        FragmentNames.add(name);
    }


    @Override
    public Fragment getItem(int position) {
        return Fragments.get(position);
    }

    @Override
    public int getCount() {
        return Fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return FragmentNames.get(position);
    }

    public List<Fragment> getFragments() {
        return Fragments;
    }

    public void setFragments(List<Fragment> fragments) {
        Fragments = fragments;
    }

    public List<String> getFragmentNames() {
        return FragmentNames;
    }

    public void setFragmentNames(List<String> fragmentNames) {
        FragmentNames = fragmentNames;
    }

}
