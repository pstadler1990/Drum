package de.pstadler.drum.Sound.Playback;

import de.pstadler.drum.Database.Sound;


public class PlaybackArray
{
	private Sound sound;
	private boolean[] playbackArray;

	public PlaybackArray(Sound sound, boolean[] playbackArray)
	{
		this.sound = sound;
		this.playbackArray = playbackArray;
	}
}
