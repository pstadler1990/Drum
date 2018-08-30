package de.pstadler.drum.Sound.Playback;

import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import static de.pstadler.drum.Track.TrackFragment.NUMBER_OF_BUTTONS;


public class PlaybackEngine extends ScheduledThreadPoolExecutor
{
	private ArrayList<IClock> listeners;
	private ArrayList<Player> players;
	private int bpm = 80;					// TODO: User must be able to set the bpm from the UI
	private int currentBarNumber = 0;
	private int currentStepNumber = 0;
	private int numberOfSteps = NUMBER_OF_BUTTONS - 1;
	static ScheduledFuture<?> t;
	private boolean stopProcess = false;

	/* Initialize a PlaybackEngine with corePoolSize threads,
	   You can pass listeners of type IClock => those listeners are not returned as players,
	   but they can attach to the onClockUpdate trigger that is fired by the PlaybackEngine */
	public PlaybackEngine(int corePoolSize, IClock...listeners)
	{
		super(corePoolSize);
		this.players = new ArrayList<>();
		this.listeners = new ArrayList<>();
		addListener(listeners);
	}

	public void addListener(IClock...listeners)
	{
		this.listeners.addAll(Arrays.asList(listeners));
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

		for(IClock listener : listeners) {
			listener.onStartPlayback();
		}

		t = scheduleAtFixedRate(new Runnable()
		{
			@Override
			synchronized public void run()
			{
				if(stopProcess)
				{
					t.cancel(true);

					for(IClock listener : listeners) {
						listener.onStopPlayback();
					}
				}
				else
				{
					for (Player player : players) {
						player.onClockUpdate(currentBarNumber, currentStepNumber);
					}
					for(IClock listener : listeners) {
						listener.onClockUpdate(currentBarNumber, currentStepNumber);
					}

					if (currentStepNumber++ >= numberOfSteps)
					{
						currentBarNumber++;
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

	public void resetPlayback()
	{
		currentStepNumber = -1;
		currentBarNumber = 0;
	}
}
