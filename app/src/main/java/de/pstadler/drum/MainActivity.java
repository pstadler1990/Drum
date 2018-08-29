package de.pstadler.drum;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import de.pstadler.drum.Database.Sound;
import de.pstadler.drum.Sound.Playback.IClock;
import de.pstadler.drum.Sound.Playback.PlaybackArray;
import de.pstadler.drum.Sound.Playback.PlaybackConverter;
import de.pstadler.drum.Sound.Playback.PlaybackEngine;
import de.pstadler.drum.Sound.Playback.Player;
import de.pstadler.drum.Track.BarFragment;
import de.pstadler.drum.Track.Instrument;
import de.pstadler.drum.Track.TrackFragment;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, IClock
{
    public static final String TAG = "MainActivityLog";
	public static final int TRACKS_MAX = 15;
    public static int pages = 0;
	private int tracks = 0;
    private ViewPager viewPager;
    private ScreenSlidePageAdapter pagerAdapter;
    private LinearLayout mainAppbar;
    private ImageButton buttonPlayStop;
    protected ArrayList<BarFragment> barFragments;
    private PlaybackEngine playbackEngine;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.bars_viewpager);
        mainAppbar = findViewById(R.id.main_appbar);
        buttonPlayStop = findViewById(R.id.button_playstop);

        mainAppbar.setElevation(getResources().getDimension(R.dimen.app_main_topbar_elevation));

        pagerAdapter = new ScreenSlidePageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        getSupportActionBar().setElevation(0);

        buttonPlayStop.setOnClickListener(this);

        // Create first page
        barFragments = new ArrayList<>();
        pages = 0;

        if(savedInstanceState != null)
        {
            int oldPages = savedInstanceState.getInt("pages");
            createNewPage(oldPages);
        }
        else
            createNewPage();

        /* Create playback engine */
        playbackEngine = new PlaybackEngine(TRACKS_MAX, this);	/* n = number of channels */
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.menu_mainactivity, menu);
		return true;
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

    private int createNewTrack(int trackCount, int...position)
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
                    barFragment.onAddTrackListener(curTracks, null);
                    tracksCreated++;
                }
            }
        }
        return tracksCreated;
    }

    private void createNewTrackSynchronized(int...trackCount)
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
            Sound sound = tf.getSound();
            int trackId = tf.getTrackId();
            dst.createNewTrack(trackId, sound);
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        }
        else {
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
			case R.id.button_playstop:

				// TODO: add progress bar or change playback button to something spinning

				/* Preload the whole song */
				BarFragment[] bars = new BarFragment[pagerAdapter.getCount()];

				ArrayList<PlaybackArray[]> playbackArrays = new ArrayList<>();

				/* (BLOCKING) Activate each bar / page one after the other
				   Collect track information and convert it via convertBarToArray
				   Add the new track information to the playbackArrays list */
				for(int p=0; p<bars.length; p++)
				{
					viewPager.setCurrentItem(p);
					BarFragment fragment = (BarFragment) pagerAdapter.getItem(p);
					while(fragment.isHidden());
					PlaybackArray[] playbackArray = PlaybackConverter.convertBarToArray(fragment);

					playbackArrays.add(playbackArray);
				}

				/* Combine the PlaybackArrays list (contains all PlayBackArrays of all tracks)
				   to a single PlaybackArray per track */
				PlaybackArray[] trackPlaybackArray = PlaybackConverter.convertPlaybackArrayListToPlaybackArrayForEachTrack(playbackArrays);

				/* Create players if needed and preload the sound files */
				int availablePlayers = playbackEngine.getPlayers().length;
				int instrumentsInBar = trackPlaybackArray.length;

				if(availablePlayers < instrumentsInBar)
				{
					int d = instrumentsInBar - availablePlayers;

					for (int i=0; i<d; i++)
					{
						Player player = new Player();
						playbackEngine.addPlayer(player);
					}
				}

				Player[] players = playbackEngine.getPlayers();

				for(int i=0; i<instrumentsInBar; i++)
				{
					players[i].preparePlayback(trackPlaybackArray[i]);
				}

				playbackEngine.startPlayback();

				//TODO: Lock play button and change icon to stop / pause
				buttonPlayStop.setClickable(false);
				break;
        }
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.menu_main_add_page:
				createNewPage();
				break;
			case R.id.menu_main_add_track:
				createNewTrackSynchronized();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClockUpdate(int barId, int stepId)
	{
		/* TODO: Add gui update on playback clock update here */

		if(stepId == (TrackFragment.NUMBER_OF_BUTTONS - 1))
		{
			if (barId >= (pagerAdapter.getCount() - 1))
			{
				playbackEngine.stopPlayback();

				//TODO: Unlock play button again and change back to play icon

				buttonPlayStop.setClickable(true);
				return;
			}
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
