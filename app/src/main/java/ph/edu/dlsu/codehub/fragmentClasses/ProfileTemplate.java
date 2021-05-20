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


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.top_bar, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                intent.putExtra("stringQuery", s);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//        TextView displayTextView = (TextView) findViewById(R.id.displayText);
        switch (item.getItemId())
        {
            case R.id.action_favorite:
//                displayTextView.setText("Fav");
                Intent intent = new Intent(getApplicationContext(), ViewProfileActivity.class);
                startActivity(intent);

                Log.d("enter function","entered function");



                return true;
//            case R.id.action_settings:
//                displayTextView.setText("Settings");
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}