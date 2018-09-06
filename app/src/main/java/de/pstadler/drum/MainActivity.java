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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import de.pstadler.drum.Database.Sound;
import de.pstadler.drum.Sound.Playback.IClock;
import de.pstadler.drum.Sound.Playback.IPlaybackControl;
import de.pstadler.drum.Sound.Playback.PlaybackArray;
import de.pstadler.drum.Sound.Playback.PlaybackConverter;
import de.pstadler.drum.Sound.Playback.PlaybackEngine;
import de.pstadler.drum.Sound.Playback.Player;
import de.pstadler.drum.Track.BarFragment;
import de.pstadler.drum.Track.TrackFragment;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, IClock, IPlaybackControl
{
	public static final int TRACKS_MAX = 8;
	public static final int PAGES_MAX = 10;	// TODO: Add limit to page create function!
    public static int pages = 0;
    private boolean loopPlayback = false;
    private boolean isPlaying = false;
    private ViewPager viewPager;
    private ScreenSlidePageAdapter pagerAdapter;
    private LinearLayout mainAppbar;
    private ImageButton buttonPlayStop;
    private TextView mainStepNumber;
    private TextView mainBarNumber;
    private TextView mainBPM;
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
        mainBarNumber = findViewById(R.id.main_bar_number);
        mainStepNumber = findViewById(R.id.main_step_number);
        mainBPM = findViewById(R.id.main_bpm);

        mainAppbar.setElevation(getResources().getDimension(R.dimen.app_main_topbar_elevation));
		getSupportActionBar().setElevation(0);

        pagerAdapter = new ScreenSlidePageAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

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

        playbackEngine = new PlaybackEngine(this);

		mainBPM.setText("80");
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
        int n = (trackCount.length > 0) ? trackCount[0] : 1;

        int backupCurrentPage = viewPager.getCurrentItem();

        for(int i=0; i<pagerAdapter.getCount(); i++)
        {
            viewPager.setCurrentItem(i);
            if(viewPager.getCurrentItem() == i)
            {
                BarFragment bf = barFragments.get(i);
                int curTracks = bf.getTrackCount();

                if (curTracks <= (curTracks + n)) {
                    createNewTrack(n, i);
                }
            }
        }

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

				if(isPlaying) {
					playbackEngine.stopPlayback();
				}
				else
				{
					/* Prepare playback and play song */
					preparePlayback();

					viewPager.setCurrentItem(0);

					/* Get the user entered BPM, validate and set if valid */
					int bpmText = Integer.parseInt(mainBPM.getText().toString());
					boolean validBPM = playbackEngine.setBpmValidated(bpmText);
					if(!validBPM) {
						String bpmDefaultText = String.valueOf(PlaybackEngine.BPM_DEFAULT);
						mainBPM.setText(bpmDefaultText);
					}

					/* Start playback finally */
					playbackEngine.startPlayback(pagerAdapter.getCount(), loopPlayback);
				}
				break;
        }
    }

    /* (BLOCKING) Prepare the song playback
       Collects the track information from all bars / pages and converts them into a single
       PlaybackArray per track
       Creates audio players, if needed */
    private void preparePlayback()
	{
		/* Preload the whole song */
		ArrayList<PlaybackArray[]> playbackArrays = new ArrayList<>();

		/* (BLOCKING) Activate each bar / page one after the other
		   Collect track information and convert it via convertBarToArray
		   Add the new track information to the playbackArrays list */
		for(int p=0; p<pagerAdapter.getCount(); p++)
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

		/* Create players if needed */
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

		/*  Preload the sound files */
		Player[] players = playbackEngine.getPlayers();

		for(int i=0; i<instrumentsInBar; i++)
		{
			players[i].preparePlayback(trackPlaybackArray[i]);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
			case R.id.menu_main_add_page:
				/* */
				if(!isPlaying) {
					createNewPage();
				}
				break;
			case R.id.menu_main_add_track:
				/* */
				if(!isPlaying) {
					createNewTrackSynchronized();
				}
				break;
			case R.id.menu_main_loop_enable:
				/* */
				if(item.isChecked()) {
					loopPlayback = false;
					item.setChecked(false);
				} else {
					loopPlayback = true;
					item.setChecked(true);
				}
				break;
			case R.id.menu_main_save_track:
				/* TODO: Show dialog with EditText (song name) and call insert song method from db */
				break;

		}
		return super.onOptionsItemSelected(item);
	}

	/* Listen to the PlayBackEngine's onClockUpdate event to control song playback and
	   to update the user interface */
	@Override
	public void onClockUpdate(final int barId, final int stepId)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run() {
				mainStepNumber.setText(String.valueOf(stepId));
			}
		});

		if(stepId == (TrackFragment.NUMBER_OF_BUTTONS - 1))
		{
			if (barId >= (pagerAdapter.getCount() - 1))
			{
				if (loopPlayback)
				{
					runOnUiThread(new Runnable()
					{
						@Override
						public void run()
						{
							mainBarNumber.setText(String.valueOf("0"));
							mainStepNumber.setText(String.valueOf("7"));
						}
					});
				}
			}
		}
	}

	@Override
	public void onStartPlayback()
	{
		isPlaying = true;
		runOnUiThread(new Runnable()
		{
			@Override
			public void run() {
				buttonPlayStop.setActivated(true);
			}
		});

	}

	@Override
	public void onStopPlayback()
	{
		isPlaying = false;
		runOnUiThread(new Runnable()
		{
			@Override
			public void run() {
				buttonPlayStop.setActivated(false);
				mainStepNumber.setText("0");
				mainBarNumber.setText("0");
			}
		});

	}

	@Override
	public void onBarComplete(final int barId)
	{
		if(barId >= pagerAdapter.getCount())
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					mainBarNumber.setText(String.valueOf(0));

					BarFragment fragment = (BarFragment) pagerAdapter.getItem(0);
					while(fragment.isHidden());
					viewPager.setCurrentItem(0);
				}
			});
		}
		else
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					mainBarNumber.setText(String.valueOf(barId));

					BarFragment fragment = (BarFragment) pagerAdapter.getItem(barId);
					while(fragment.isHidden());
					viewPager.setCurrentItem(barId);
				}
			});
		}
	}

	public void synchronizeSounds(int trackId, Sound sound)
	{
		for(int p=0; p<pagerAdapter.getCount(); p++)
		{
			BarFragment fragment = (BarFragment) pagerAdapter.getItem(p);
			while(fragment.isHidden());

			TrackFragment trackFragment = (TrackFragment) fragment.getTracks().get(trackId);
			trackFragment.setSound(sound);
		}
	}

	/*Adapter for the pages (= bars); each page represents a single bar of the whole song*/
    private class ScreenSlidePageAdapter extends FragmentStatePagerAdapter
    {
        ScreenSlidePageAdapter(FragmentManager fm)
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
