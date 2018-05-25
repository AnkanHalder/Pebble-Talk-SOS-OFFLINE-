package com.example.samscots.sosoffine;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Sam Scots on 10/27/2017.
 */

public class PagerAdapter extends FragmentPagerAdapter {
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Online onlineFragment=new Online();
                return onlineFragment;
            case 1:
                Chats chatFragment=new Chats();
                return chatFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "ONLINE";
            case 1:
                return "CHATS";
            default:
                return null;
        }
    }
}
