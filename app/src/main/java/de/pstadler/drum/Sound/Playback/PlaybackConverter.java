package de.pstadler.drum.Sound.Playback;

import java.util.ArrayList;
import de.pstadler.drum.Database.Sound;
import de.pstadler.drum.Track.BarFragment;
import de.pstadler.drum.Track.TrackFragment;


public class PlaybackConverter
{
	private static final int buttonCount = TrackFragment.NUMBER_OF_BUTTONS;

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

	public static PlaybackArray[] convertPlaybackArrayListToPlaybackArrayForEachTrack(ArrayList<PlaybackArray[]> list)
	{
		int trackCount = list.get(0).length;
		int pageCount = list.size();
		int arrayLengthPerTrack = pageCount * buttonCount;

		PlaybackArray[] playbackArrays = new PlaybackArray[trackCount];


		boolean[][] tmpArray = new boolean[trackCount][];
		for(int j=0; j<trackCount; j++)
		{
			tmpArray[j] = new boolean[arrayLengthPerTrack];
		}

		for(int p=0; p<list.size(); p++)
		{
			for(int e=0; e<list.get(p).length; e++)
			{
				Object src = list.get(p)[e].getPlaybackArray();
				if(src != null) {
					System.arraycopy(src, 0, tmpArray[e], (p * 8), 8);
				}
			}
		}

		for(int j=0; j<trackCount; j++)
		{
			Sound src = list.get(0)[j].getSound();
			if(src != null)
			{
				playbackArrays[j] = new PlaybackArray(src, null);
				playbackArrays[j].playbackArray = new boolean[arrayLengthPerTrack];

				System.arraycopy(tmpArray[j], 0, playbackArrays[j].playbackArray, 0, arrayLengthPerTrack);
			}
		}

		return playbackArrays;
	}
}
