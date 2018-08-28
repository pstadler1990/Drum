package de.pstadler.drum.Sound.Playback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class PlaybackEngine extends ScheduledThreadPoolExecutor
{
	private ArrayList<IClock> handlers;
	private int bpm = 80;
	private int currentBarNumber = 0;


	public PlaybackEngine(int corePoolSize)
	{
		super(corePoolSize);
		this.handlers = new ArrayList<>();
	}

	public void addPlayer(IClock...handlers)
	{
		this.handlers.addAll(Arrays.asList(handlers));
	}

	public void run()
	{
		scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				for(IClock player : handlers)
				{
					player.onClockUpdate(currentBarNumber);
				}
				currentBarNumber++;	//TODO: check against real track bar count
			}
		}, 0, 1000, TimeUnit.MILLISECONDS);
	}

	public void stop()
	{
		stop();
	}

}
