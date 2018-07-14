package de.pstadler.drum;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import java.util.ArrayList;


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

        if(savedInstanceState != null)
        {
            int oldPages = savedInstanceState.getInt("pages");
            for(int i=0; i<oldPages; i++)
            {
                createNewPage();
            }
        }
        else
            createNewPage();
    }

    private void createNewPage()
    {
        barFragments.add(new BarFragment());
        pages++;
        pagerAdapter.notifyDataSetChanged();
        //TODO: createNewTrack() if fragment is attached (call n times, where n is the number of currently used tracks!)
    }

    private void createNewTrack()
    {
        BarFragment barFragment = barFragments.get(viewPager.getCurrentItem());

        if(barFragment != null)
        {
            barFragment.onAddTrackListener(1); //TODO: Add real track number
        }
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
    protected void onSaveInstanceState(Bundle outState)
    {
        outState.putInt("pages", pages);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.button_add_track:
                createNewTrack();
                break;
            case R.id.button_add_page:
                createNewPage();
                break;
        }
    }


    /*Adapter for the pages (= bars); each page represents a single bar of the whole song*/
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
}
