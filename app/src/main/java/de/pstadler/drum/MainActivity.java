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
import java.util.List;
import java.util.concurrent.BrokenBarrierException;


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

    private int tracks = 0;
    private int instrumentId = 1;
    public static final int TRACKS_MAX = 15;


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
            createNewPage(oldPages);
        }
        else
            createNewPage();
    }

    private void createNewPage(int...pageNumber)
    {
        int n = (pageNumber.length > 0)? pageNumber[0] : 1;

        for(int i=0; i<n; i++)
        {
            barFragments.add(new BarFragment());
            pages++;
            pagerAdapter.notifyDataSetChanged();
        }

        if(pages > 1)
        {
            viewPager.setCurrentItem(pages);
            cloneTrackInformation(pages - 1);
        }
    }

    public int createNewTrack(int trackCount, int...position)
    {
        int tracksCreated = 0;
        BarFragment barFragment = (position.length > 0)? barFragments.get(position[0]) : barFragments.get(viewPager.getCurrentItem());

        if(barFragment != null)
        {
            for (int i = 0; i < trackCount; i++)
            {
                int curTracks = barFragment.getTrackCount();
                if (curTracks + (trackCount - i) <= TRACKS_MAX)
                {
                    barFragment.onAddTrackListener(curTracks, Instrument.INSTRUMENT_DEFAULT);
                    tracksCreated++;
                }
            }
        }
        return tracksCreated;
    }

    public void createNewTrackSynchronized(int...trackCount)
    {
        int tracksCreated = 0;
        int n = (trackCount.length > 0) ? trackCount[0] : 1;

        int backupCurrentPage = viewPager.getCurrentItem();

        for(int i=0; i<pagerAdapter.getCount(); i++)
        {
            viewPager.setCurrentItem(i);
            if(viewPager.getCurrentItem() == i)
            {
                BarFragment bf = barFragments.get(i);
                int curTracks = bf.getTrackCount();

                if (curTracks <= (curTracks + n))
                {
                    tracksCreated = createNewTrack(n, i);
                }
            }
        }
        tracks += tracksCreated;

        viewPager.setCurrentItem(backupCurrentPage);
    }

    private void cloneTrackInformation(int destPage)
    {
        if(destPage < 0 || destPage > pagerAdapter.getCount()) return;

        /*Ensure to fetch an attached fragment to clone from*/
        int currentPage = viewPager.getCurrentItem();
        int srcPage = (destPage == currentPage)? (destPage - 1) : currentPage;

        BarFragment src = (BarFragment)pagerAdapter.getItem(srcPage);
        BarFragment dst = (BarFragment)pagerAdapter.getItem(destPage);

        if(src == null || dst == null) return;

        List<Fragment> srcTracks = src.getTracks();
        for(Fragment f : srcTracks)
        {
            TrackFragment tf = (TrackFragment)f;
            String instrumentName = tf.getInstrumentText();
            int instrumentId = tf.getInstrumentId();
            int trackId = tf.getTrackId();
            dst.createNewTrack(trackId, instrumentId, instrumentName);
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
                createNewTrackSynchronized();
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
