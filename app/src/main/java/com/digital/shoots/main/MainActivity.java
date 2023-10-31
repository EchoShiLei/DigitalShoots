package com.digital.shoots.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.digital.shoots.R;
import com.digital.shoots.ble.BleDeviceManager;
import com.digital.shoots.tab.TabFragmentAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.zyq.easypermission.EasyPermission;
import com.zyq.easypermission.EasyPermissionHelper;
import com.zyq.easypermission.EasyPermissionResult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView mTab;
    private ViewPager2 mViewPager;
    private TabFragmentAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initContainer();

    }

    private void initContainer() {
        mTab = findViewById(R.id.bottom);
        mViewPager = findViewById(R.id.container);
        mAdapter = new TabFragmentAdapter(this);
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
        if(mViewPager.getCurrentItem() == 0) {
            if(!mAdapter.getTrainersFragment().onBackPressed()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }
}