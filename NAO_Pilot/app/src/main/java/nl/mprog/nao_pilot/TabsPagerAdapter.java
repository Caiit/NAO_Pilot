package nl.mprog.nao_pilot;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
/**
 * NAO Pilot
 * Caitlin Lagrand (10759972)
 * UvA Programmeerproject
 *
 * TabsPagerAdapter handles the tabs of the app.
 */

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int pos) {

        switch (pos) {
            case 0:
                return new ConnectFragment();
            case 1:
                return new SpeakFragment();
            case 2:
                return new WalkFragment();
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int pos) {
        switch(pos) {
            case 0:
                return "Connect";
            case 1:
                return "Speak";
            case 2:
                return "Walk";
            default:
                return null;
        }
    }
}
