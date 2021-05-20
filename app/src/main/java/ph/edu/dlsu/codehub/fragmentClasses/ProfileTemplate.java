package ph.edu.dlsu.codehub.fragmentClasses;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import ph.edu.dlsu.codehub.R;
import ph.edu.dlsu.codehub.activityClasses.BaseToolbarActivity;
import ph.edu.dlsu.codehub.activityClasses.SearchActivity;
import ph.edu.dlsu.codehub.helperClasses.SectionsPagerAdapter;
import ph.edu.dlsu.codehub.activityClasses.ViewProfileActivity;

public class ProfileTemplate extends BaseToolbarActivity {
    //large application of LBYCPEI ^^:

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_template);
        setToolBar();

        //set content container
        mViewPager = findViewById(R.id.profile_container); //not yet made
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);//not yet referenced
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_add_24);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_notifications_black_24dp);
    }


    private void setupViewPager(ViewPager viewPager)
    {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new CreatePostFragment());
        adapter.addFragment(new NotificationsFragment());
        viewPager.setAdapter(adapter);
    }


}