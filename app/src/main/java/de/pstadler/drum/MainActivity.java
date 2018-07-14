package de.pstadler.drum;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    public static final String TAG = "MainActivityLog";
    public static int pages = 0;
    private ViewPager viewPager;
    private ScreenSlidePageAdapter pagerAdapter;
    private LinearLayout mainAppbar;
    private Button buttonAddTrack;
    private Button buttonAddPage;
    protected ArrayList<BarFragment> barFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.bars_viewpager);
        mainAppbar = findViewById(R.id.main_appbar);
        buttonAddTrack = findViewById(R.id.button_add_track);
        buttonAddPage = findViewById(R.id.button_add_page);

        mainAppbar.setElevation(getResources().getDimension(R.dimen.app_main_topbar_elevation));

        pagerAdapter = new ScreenSlidePageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        getSupportActionBar().setElevation(0);

        buttonAddTrack.setOnClickListener(this);
        buttonAddPage.setOnClickListener(this);

        // Create first page
        barFragments = new ArrayList<>();
        createNewPage();
    }

    protected void createNewPage()
    {
        barFragments.add(new BarFragment());
        pages++;
        pagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.button_add_track:
                BarFragment barFragment = barFragments.get(viewPager.getCurrentItem());

                if(barFragment != null)
                {
                    barFragment.onAddTrackListener(1); //TODO: Add real track number
                }
                break;
            case R.id.button_add_page:
                createNewPage();
                break;
        }
    }

    private class ScreenSlidePageAdapter extends FragmentStatePagerAdapter
    {
        public ScreenSlidePageAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return barFragments.get(position);
        }

        @Override
        public int getCount()
        {
            return pages;
        }
    }

    @Override
    protected void onPause()
    {
        Log.v(TAG, "Main activity is paused!!");
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        Log.v(TAG, "Main activity is destroyed!!");
        super.onDestroy();
    }
}
