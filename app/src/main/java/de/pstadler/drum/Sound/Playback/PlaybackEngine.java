package de.pstadler.drum.Sound.Playback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import static de.pstadler.drum.Track.TrackFragment.NUMBER_OF_BUTTONS;


public class PlaybackEngine extends ScheduledThreadPoolExecutor
{
	private ArrayList<IClock> handlers;
	private int bpm = 80;					// TODO: User must be able to set the bpm from the UI
	private int currentBarNumber = 0;
	private int currentStepNumber = 0;
	private int numberOfSteps = NUMBER_OF_BUTTONS;
	static ScheduledFuture<?> t;
	private boolean stopProcess = false;

	public PlaybackEngine(int corePoolSize)
	{
		super(corePoolSize);
		this.handlers = new ArrayList<>();
	}

	public void addPlayer(IClock...handlers)
	{
		this.handlers.addAll(Arrays.asList(handlers));
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
					for (IClock player : handlers) {
						player.onClockUpdate(currentBarNumber, currentStepNumber);
					}

					if (currentStepNumber++ >= numberOfSteps)
					{
						currentBarNumber++;    //TODO: check against real track bar count
						currentStepNumber = 0;
					}
				}
			}
		}, 0, 1000, TimeUnit.MILLISECONDS);	// TODO: convert BPM into milliseconds
	}

	public void stopPlayback()
	{
		stopProcess = true;
	}

}
