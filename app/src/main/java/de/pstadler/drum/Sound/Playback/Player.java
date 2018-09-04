package de.pstadler.drum.Sound.Playback;

import android.media.MediaPlayer;
import java.io.IOException;


public class Player extends MediaPlayer implements IClock
{
	private PlaybackEngine playbackEngine;
	private boolean readyForPlayback = false;
	private PlaybackArray playbackArray;

	public void preparePlayback(PlaybackArray playbackArray)
	{
		reset();

		this.playbackArray = playbackArray;
		try
		{
			if(playbackArray == null || playbackArray.getSound() == null)
			{
				readyForPlayback = false;
				return;
			}
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
		if(readyForPlayback && playbackArray.getPlaybackArray()[(barId * 8) + stepId])		// TODO: replace 8 with track button count constant
		{
			start();
		}
	}

	@Override
	public void onBarComplete(int barId) { }
}
