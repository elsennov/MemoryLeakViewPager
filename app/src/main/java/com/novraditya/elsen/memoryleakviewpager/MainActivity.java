package com.novraditya.elsen.memoryleakviewpager;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final int TAB_SIZE = 6;
    private static final String PARENT = "Parent ";

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    @Bind(R.id.tab_layout)
    TabLayout mViewPagerTabs;

    private ViewPagerAdapter mViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViewPager();
    }

    private void initViewPager() {
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        ParentFragment parentFragment;
        for (int i = 0; i < TAB_SIZE; i++) {
            parentFragment = ParentFragment.getInstance(PARENT + i);
            mViewPagerAdapter.addFragment(parentFragment, PARENT + i);
        }

        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPagerTabs.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mViewPagerAdapter != null) {
            mViewPagerAdapter.clearReference();
            mViewPagerAdapter = null;
        }
    }

    private static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<ParentFragment> mParentFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        public void addFragment(ParentFragment parentFragment, String title) {
            mParentFragmentList.add(parentFragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mParentFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mParentFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void clearReference() {
            if (mFragmentTitleList != null) {
                mFragmentTitleList.clear();
            }

            if (mParentFragmentList != null) {
                mParentFragmentList.clear();
            }
        }
    }
}
