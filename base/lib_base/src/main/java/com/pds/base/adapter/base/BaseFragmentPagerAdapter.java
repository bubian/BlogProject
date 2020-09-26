package com.pds.base.adapter.base;

import android.os.Bundle;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.Comparator;
import java.util.List;

public class BaseFragmentPagerAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = BaseFragmentPagerAdapter.class.getSimpleName();

    private SparseArray<Fragment> mCachedFragments = new SparseArray<Fragment>();
    private final List<FragmentData> mFragmentDatas;

    public BaseFragmentPagerAdapter(FragmentManager fm, List<FragmentData> fragmentDatas) {
        super(fm);
        this.mFragmentDatas = fragmentDatas;
    }

    public FragmentData getFragmentDataAt(int index) {
        return mFragmentDatas.get(index);
    }

    public List<FragmentData> getFragmentDatas() {
        return mFragmentDatas;
    }

    @Override
    public Fragment getItem(int position) {
        try {
            Fragment fragment = mCachedFragments.get(position);
            FragmentData data = mFragmentDatas.get(position);
            if (fragment == null || fragment.isRemoving() || !fragment.getClass().equals(data.fragmentClass)) {
                fragment = (Fragment) data.fragmentClass.newInstance();
                mCachedFragments.put(position, fragment);
            }

            if (data.bundle != null) {
                fragment.setArguments(data.bundle);
            }
            return fragment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getCount() {
        return mFragmentDatas.size();
    }

    public Fragment getFragment(int position) {
        if (null == mCachedFragments && mCachedFragments.size() <= position){
            return null;
        }
        return mCachedFragments.get(position);
    }

    public void addFragment(FragmentData data) {
        mFragmentDatas.add(data);
        notifyDataSetChanged();
    }

    public void setFragments(List<FragmentData> datas) {
        if (datas != null && datas.size() > 0) {
            mFragmentDatas.clear();
            mFragmentDatas.addAll(datas);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentDatas.get(position).title;
    }

    /**
     * contains: title, fragmentClass and fragment args(arguments)
     */
    public static class FragmentData implements Comparable<FragmentData> {
        /**
         * the title to show
         */
        public String title;
        /**
         * the fragment class
         */
        public Class<?> fragmentClass;
        /**
         * the arguments of create fragment
         */
        public Bundle bundle;
        /**
         * this is only used in {@link BaseFragmentPagerAdapter#(Comparator)}
         */
        public int priority;

        /**
         * the extra data
         */
        public Object extra;

        public boolean forceUpdate;


        public FragmentData(@NonNull String title, @NonNull Class<?> fragmentClass, Bundle bundle) {
            super();
            this.title = title;
            this.fragmentClass = fragmentClass;
            this.bundle = bundle;
        }

        @Override
        public int compareTo(@NonNull FragmentData another) {
            return this.priority < another.priority ? -1 :
                    (this.priority == another.priority ? 0 : 1);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || !(o instanceof FragmentData))
                return false;
            FragmentData that = (FragmentData) o;
            return title.equals(that.title);
        }

        @Override
        public int hashCode() {
            return title.hashCode();
        }

        @Override
        public String toString() {
            return "FragmentData{" +
                    "title='" + title + '\'' +
                    ", fragmentClass=" + fragmentClass +
                    ", bundle=" + bundle +
                    ", priority=" + priority +
                    ", extra=" + extra +
                    '}';
        }
    }

}
