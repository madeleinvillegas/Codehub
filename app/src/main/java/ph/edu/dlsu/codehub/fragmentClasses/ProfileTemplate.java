package ph.edu.dlsu.codehub.fragmentClasses;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;

import com.google.android.material.tabs.TabLayout;

import ph.edu.dlsu.codehub.R;
import ph.edu.dlsu.codehub.SectionsPagerAdapter;

public class ProfileTemplate extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_template);


        //top banner
        Toolbar myToolbar = findViewById(R.id.top_banner);
        setSupportActionBar(myToolbar);

        //set content container
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = findViewById(R.id.container); //not yet made
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
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        TextView displayTextView = (TextView) findViewById(R.id.displayText);
//        switch (item.getItemId())
//        {
//            case R.id.action_favorite:
//                displayTextView.setText("Fav");
//                return true;
//            case R.id.action_settings:
//                displayTextView.setText("Settings");
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }
}