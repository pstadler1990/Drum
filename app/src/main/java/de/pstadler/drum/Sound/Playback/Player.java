package de.pstadler.drum.Sound.Playback;

import android.media.MediaPlayer;
import java.io.IOException;
import de.pstadler.drum.Track.TrackFragment;


public class Player extends MediaPlayer implements IClock
{
	private static final int buttonCount = TrackFragment.NUMBER_OF_BUTTONS;
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
		if(readyForPlayback && playbackArray.getPlaybackArray()[(barId * buttonCount) + stepId])
		{
			/* If the player is already playing, seek to second 0 to start the sound again */
			if(isPlaying()) {
				seekTo(0);
			}
			else {
				start();
			}
		}
	}

	@Override
	public void onBarComplete(int barId) { }
}
