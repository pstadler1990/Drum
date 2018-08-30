package de.pstadler.drum.Sound.Playback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import de.pstadler.drum.Track.TrackFragment;


public class PlaybackEngine extends Timer
{
	private IClock mainListenerClock;
	private IPlaybackControl mainListenerPlayback;
	private ArrayList<Player> players;
	private int bpm = 80;					// TODO: User must be able to set the bpm from the UI
	private boolean stopProcess = false;
	private static int currentBarNumber = 0;
	private static int currentStepNumber = 0;
	private final int numberOfSteps = TrackFragment.NUMBER_OF_BUTTONS;

	/* Initialize a PlaybackEngine with corePoolSize threads,
	   You can pass listeners of type IClock => those listeners are not returned as players,
	   but they can attach to the onClockUpdate trigger that is fired by the PlaybackEngine */
	public PlaybackEngine(Object mainListener)
	{
		super();

		this.players = new ArrayList<>();
		this.mainListenerClock = (IClock) mainListener;
		this.mainListenerPlayback = (IPlaybackControl) mainListener;
	}

	public void addPlayer(Player...players)
	{
		this.players.addAll(Arrays.asList(players));
	}

	public Player[] getPlayers()
	{
		return players.toArray(new Player[0]);
	}

	public void startPlayback(final int numberOfBars, final boolean loopPlayback)
	{
		stopProcess = false;
		currentBarNumber = 0;
		currentStepNumber = 0;

		mainListenerPlayback.onStartPlayback();

		scheduleAtFixedRate(new TimerTask()
		{
			@Override
			public void run()
			{
				if(stopProcess)
				{
					mainListenerPlayback.onStopPlayback();
					cancel();
				}

				mainListenerClock.onClockUpdate(currentBarNumber, currentStepNumber);

				for(final Player player : players)
				{
					player.onClockUpdate(currentBarNumber, currentStepNumber);
				}

				if (currentStepNumber == (numberOfSteps - 1))
				{
					if (currentBarNumber >= (numberOfBars - 1))
					{
						if (!loopPlayback) {
							stopProcess = true;
							mainListenerPlayback.onStopPlayback();
							cancel();
						}
						else
						{
							mainListenerClock.onBarComplete(currentBarNumber + 1);
							currentBarNumber = 0;
							currentStepNumber = -1;
						}
					}
				}

				if (currentStepNumber++ >= (numberOfSteps - 1))
				{
					currentBarNumber++;
					currentStepNumber = 0;
				}
			}
		}, 0, 300);	// TODO: convert bpm to ms

	}

	public void stopPlayback()
	{
		stopProcess = true;
	}
}
