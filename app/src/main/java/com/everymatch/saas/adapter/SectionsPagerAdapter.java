package com.everymatch.saas.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.everymatch.saas.server.responses.ResponseApplication;
import com.everymatch.saas.ui.sign.WalkthroughFragment;

import java.util.ArrayList;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private final ArrayList<ResponseApplication.Start.DataModelApplication> data;

    public SectionsPagerAdapter(FragmentManager fm, ArrayList<ResponseApplication.Start.DataModelApplication> data) {
        super(fm);
        this.data = data;
    }

    @Override
    public Fragment getItem(int position) {
        return WalkthroughFragment.newInstance(position, data.get(position));
    }

    @Override
    public int getCount() {
        return data.size();
    }

}