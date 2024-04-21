package com.digital.shoots.main;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.digital.shoots.R;
import com.digital.shoots.tab.ChangePagerListener;
import com.digital.shoots.tab.TabFragmentAdapter;
import com.digital.shoots.utils.LogcatHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    @IntDef({R.id.trainersFragment, R.id.drillsFragment, R.id.trackingFragment,
            R.id.myStatsFragment, R.id.myAccountFragment})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MenuId {
    }

    private BottomNavigationView mTab;
    private ViewPager2 mViewPager;
    private TabFragmentAdapter mAdapter;
    LogcatHelper logcatHelper;
    private ChangePagerListener mChangePagerListener = new ChangePagerListener() {
        @Override
        public void onChangerPager(int id) {
            if (mTab != null) {
                mTab.setSelectedItemId(id);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        initContainer();



        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String logPathTime = simpleDateFormat.format(System.currentTimeMillis());

        logcatHelper = LogcatHelper.getInstance(getApplicationContext(), "Download/log", "shoots" + logPathTime + ".log");

        logcatHelper.start("d");

    }

    private void initContainer() {
        mTab = findViewById(R.id.bottom);
        mTab.setItemIconTintList(null);
        mViewPager = findViewById(R.id.container);
        mAdapter = new TabFragmentAdapter(this, mChangePagerListener);
        mViewPager.setAdapter(mAdapter);
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mTab.getMenu().getItem(position).setChecked(true);
                switch (position) {
                    case 0:
                        mTab.setSelectedItemId(R.id.trainersFragment);
                        break;
                    case 1:
                        mTab.setSelectedItemId(R.id.drillsFragment);
                        break;
                    case 2:
                        mTab.setSelectedItemId(R.id.trackingFragment);
                        break;
                    case 3:
                        mTab.setSelectedItemId(R.id.myStatsFragment);
                        break;
                    case 4:
                        mTab.setSelectedItemId(R.id.myAccountFragment);
                        break;
                    default:
                        break;
                }
            }
        });
        mTab.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.trainersFragment:
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.drillsFragment:
                        mViewPager.setCurrentItem(1);
                        break;
                    case R.id.trackingFragment:
                        mViewPager.setCurrentItem(2);
                        break;
                    case R.id.myStatsFragment:
                        mViewPager.setCurrentItem(3);
                        break;
                    case R.id.myAccountFragment:
                    default:
                        mViewPager.setCurrentItem(4);
                        break;
                }
                return false;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            if (!mAdapter.getTrainersFragment().onBackPressed()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logcatHelper.stop();
    }
}