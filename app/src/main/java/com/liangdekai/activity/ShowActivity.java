package com.liangdekai.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import com.liangdekai.fragment.ShowPhoto;
import com.liangdekai.photodepot.R;

import java.util.ArrayList;
import java.util.List;

public class ShowActivity extends FragmentActivity {
    private ViewPager mViewPager ;
    private ImageView mImageView ;
    private List<Fragment> mFragment ;
    private FragmentStatePagerAdapter mFragmentAdapter;
    private List<String> mImageList;
    private int mLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_show);
        init();
    }

    private void init(){
        mFragment = new ArrayList<Fragment>();
        mImageList = getIntent().getStringArrayListExtra("imageList");
        mLevel = getIntent().getIntExtra("sign" , 0);
        mViewPager = (ViewPager) findViewById(R.id.activity_vp_show);
        mImageView = (ImageView) findViewById(R.id.fragment_show);
        ShowPhoto image = ShowPhoto.getInstance(mImageList.get(mLevel));
        mFragment.add(image);
        mFragmentAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }
        };
        mViewPager.setAdapter(mFragmentAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
