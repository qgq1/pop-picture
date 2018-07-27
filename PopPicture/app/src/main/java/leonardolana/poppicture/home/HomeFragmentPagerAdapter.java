package leonardolana.poppicture.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import leonardolana.poppicture.data.HomeSection;

/**
 * Created by leonardolana on 7/24/18.
 */

public class HomeFragmentPagerAdapter extends FragmentPagerAdapter {

    private static final String KEY_SAVE_FRAGMENT = "home_fragment_save_fragment_";
    private static final String KEY_SAVED_FRAGMENTS_COUNT = "home_fragment_saved_fragments_count";

    private Fragment mFragmentReferences[];
    private final FragmentManager mFragmentManager;

    public HomeFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
    }

    public HomeFragmentPagerAdapter(FragmentManager fm, HomeSection[] homeSections) {
        super(fm);
        mFragmentManager = fm;

        List<Fragment> fragments = new ArrayList<>();

        for (HomeSection section : homeSections) {
            try {
                Class<?> clazz = Class.forName(section.getName());
                if (Fragment.class.isAssignableFrom(clazz)) {
                    fragments.add((Fragment) clazz.newInstance());
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }

        mFragmentReferences = fragments.toArray(new Fragment[fragments.size()]);
    }

    @Override
    public Fragment getItem(final int position) {
        return mFragmentReferences != null ? mFragmentReferences[position] : null;
    }

    public int getItemPosition(HomeSection section) {
        Fragment fragment;
        for(int i=0; i<mFragmentReferences.length; i++) {
            fragment = mFragmentReferences[i];
            if(TextUtils.equals(fragment.getClass().getName(), section.getName()))
                return i;
        }

        return 0;
    }

    @Override
    public int getCount() {
        return mFragmentReferences != null ? mFragmentReferences.length : 0;
    }

    protected void saveFragments(Bundle outState) {
        outState.putInt(KEY_SAVED_FRAGMENTS_COUNT, mFragmentReferences.length);
        for (int i = 0; i < mFragmentReferences.length; i++) {
            if (mFragmentReferences[i] != null)
                mFragmentManager.putFragment(outState, KEY_SAVE_FRAGMENT + i, mFragmentReferences[i]);
        }
    }

    protected void restoreFragments(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int count = savedInstanceState.getInt(KEY_SAVED_FRAGMENTS_COUNT, 0);
            mFragmentReferences = new Fragment[count];
            Fragment frag;
            int i = 0;
            while ((frag = mFragmentManager.getFragment(savedInstanceState, KEY_SAVE_FRAGMENT + i)) != null) {
                mFragmentReferences[i] = frag;
                i++;
            }
        }
    }
}