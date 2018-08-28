package de.pstadler.drum.Sound.Playback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import static de.pstadler.drum.Track.TrackFragment.NUMBER_OF_BUTTONS;


public class PlaybackEngine extends ScheduledThreadPoolExecutor
{
	private IClock listenerActivity;
	private ArrayList<Player> players;
	private int bpm = 80;					// TODO: User must be able to set the bpm from the UI
	private int currentBarNumber = 0;
	private int currentStepNumber = 0;
	private int numberOfSteps = NUMBER_OF_BUTTONS;
	static ScheduledFuture<?> t;
	private boolean stopProcess = false;

	public PlaybackEngine(int corePoolSize, IClock listenerActivity)
	{
		super(corePoolSize);
		this.players = new ArrayList<>();
		this.listenerActivity = listenerActivity;
	}

	public void addPlayer(Player...players)
	{
		this.players.addAll(Arrays.asList(players));
	}

	public Player[] getPlayers()
	{
		return players.toArray(new Player[0]);
	}

	public void startPlayback()
	{
		stopProcess = false;
		currentBarNumber = 0;
		currentStepNumber = 0;

		t = scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				if(stopProcess) {
					t.cancel(true);
				}
				else
				{
					for (IClock player : players) {
						player.onClockUpdate(currentBarNumber, currentStepNumber);
					}
					listenerActivity.onClockUpdate(currentBarNumber, currentStepNumber);

					if (currentStepNumber++ >= numberOfSteps)
					{
						currentBarNumber++;    //TODO: check against real track bar count
						currentStepNumber = 0;
					}
				}
			}
		}, 0, 300, TimeUnit.MILLISECONDS);	// TODO: convert BPM into milliseconds
	}

	public void stopPlayback()
	{
		stopProcess = true;
	}

}
