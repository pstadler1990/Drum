package de.pstadler.drum.Sound.Playback;

import android.media.MediaPlayer;

import java.io.IOException;

public class Player extends MediaPlayer implements IClock
{
	static int players = 0;
	private int playerId;

	private boolean readyForPlayback = false;
	private PlaybackArray playbackArray;

	public Player()
	{
		playerId = players++;
	}

	public void preparePlayback(PlaybackArray playbackArray)
	{
		readyForPlayback = false;

		this.playbackArray = playbackArray;
		try
		{
			/* (BLOCKING) Preload the file from the sound path */
			setDataSource(playbackArray.getSound().path);
			prepare();
			readyForPlayback = true;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClockUpdate(int barId, int stepId)
	{
		if(playbackArray.getPlaybackArray()[stepId])
		{
			start();
		}
	}
}
