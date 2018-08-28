package de.pstadler.drum.Sound.Playback;

import de.pstadler.drum.Track.BarFragment;
import de.pstadler.drum.Track.TrackFragment;

public class PlaybackConverter
{
	/* Convert a bar (fragment) to a list of PlaybackArrays
	   Each PlaybackArray contains the true/false (on/off) values for a single bar*/
	public static PlaybackArray[] convertBarToArray(BarFragment bar)
	{
		final int trackCount = bar.getTrackCount();
		TrackFragment[] tracks = bar.getTracks().toArray(new TrackFragment[0]);

		PlaybackArray[] playbackArrays = new PlaybackArray[trackCount];

		for(int t=0; t<trackCount; t++)
		{
			boolean[] buttonStates = tracks[t].getButtonStates();
			playbackArrays[t] = new PlaybackArray(tracks[t].getSound(), buttonStates);
		}

		return playbackArrays;
	}


}
