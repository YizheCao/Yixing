package com.yixing.yixing;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private MapFragment mapFragment;
    private StateFragment stateFragment;
    private PersonFragment personFragment;
    private NoSlidingViewPager mViewPager;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_map:
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_state:
                    mViewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_people:
                    mViewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*初始化显示内容*/
        mapFragment = new MapFragment();
        stateFragment = new StateFragment();
        personFragment = new PersonFragment();
        mViewPager = (NoSlidingViewPager) findViewById(R.id.view_container);
        final ArrayList<Fragment> fgLists = new ArrayList<>(3);
        fgLists.add(mapFragment);
        fgLists.add(stateFragment);
        fgLists.add(personFragment);
        FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fgLists.get(position);
            }

            @Override
            public int getCount() {
                return fgLists.size();
            }
        };
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3); //预加载剩下两页

        /*给底部导航栏菜单项添加点击事件*/
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
