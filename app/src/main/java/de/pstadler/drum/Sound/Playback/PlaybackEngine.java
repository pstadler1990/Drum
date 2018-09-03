package de.pstadler.drum.Sound.Playback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import de.pstadler.drum.Track.TrackFragment;


public class PlaybackEngine extends Timer
{
	public static final int BPM_DEFAULT = 80;
	public static final int BPM_MIN = 30;
	public static final int BPM_MAX = 200;
	private IClock mainListenerClock;
	private IPlaybackControl mainListenerPlayback;
	private ArrayList<Player> players;
	private int bpm = BPM_DEFAULT;					// TODO: User must be able to set the bpm from the UI
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

		int convertedBPM = convertBPMToMsInterval(bpm);

		/* Tell the main listener, that playback has started */
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

				if ((currentStepNumber + 1) == numberOfSteps)
				{
					if ((currentBarNumber + 1) >= numberOfBars)
					{
						if(!loopPlayback)
						{
							stopProcess = true;
							mainListenerPlayback.onStopPlayback();
							cancel();
						}
						else
						{
							currentBarNumber = 0;
							currentStepNumber = -1;
							mainListenerClock.onBarComplete(0);
						}
					}
					else {
						mainListenerClock.onBarComplete(currentBarNumber + 1);
					}
				}

				if (currentStepNumber++ >= (numberOfSteps - 1))
				{
					currentBarNumber++;
					currentStepNumber = 0;
				}
			}
		}, 0, convertedBPM);

	}

	public void stopPlayback()
	{
		stopProcess = true;
	}

	public boolean setBpmValidated(int bpm)
	{
		if(bpm >= BPM_MIN && bpm <= BPM_MAX) {
			this.bpm = bpm;
			return true;
		} else {
			return false;
		}
	}

	private int convertBPMToMsInterval(int bpm)
	{
		if(bpm <= 0) {
			bpm = BPM_DEFAULT;
		}
		return (int) ( 60.0f / bpm) * 1000;
	}
}
