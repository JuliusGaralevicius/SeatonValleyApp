package com.seatonvalleyccapp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


public class AuthenticationActivity extends AppCompatActivity implements OnChangeFragmentCallback {
    ViewPager pager;
    CustomPagerAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        adapter = new CustomPagerAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.vp_auth_viewpager);
        System.err.println(pager);
        pager.setAdapter(adapter);

    }

    @Override
    public void changeFragment(int newFragment) {
        pager.setCurrentItem(newFragment, true);
    }

    private class CustomPagerAdapter extends FragmentPagerAdapter {
        private int NUM_SCREENS = 2;
        SignInFragment inFragment = SignInFragment.newInstance();
        SignUpFragment upFragment = SignUpFragment.newInstance(inFragment);
        public CustomPagerAdapter(FragmentManager m){
            super(m);

        }
        @Override
        public Fragment getItem(int position) {

            switch (position){
                case 0:
                    return inFragment;
                case 1:
                    return upFragment;
                default:
                    return inFragment;
            }
        }
        @Override
        public int getCount() {
            return NUM_SCREENS;
        }
    }
}
