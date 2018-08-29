package de.pstadler.drum.Sound.Playback;

import de.pstadler.drum.Database.Sound;


public class PlaybackArray
{
	private Sound sound;
	public boolean[] playbackArray;

	public PlaybackArray(Sound sound, boolean[] playbackArray)
	{
		this.sound = sound;
		this.playbackArray = playbackArray;
	}

	public Sound getSound()
	{
		return sound;
	}

	public boolean[] getPlaybackArray()
	{
		return playbackArray;
	}
}
