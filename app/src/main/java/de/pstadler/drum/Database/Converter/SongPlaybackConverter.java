package de.pstadler.drum.Database.Converter;

import android.arch.persistence.room.TypeConverter;

public class SongPlaybackConverter
{
	@TypeConverter
	public static boolean[] getPlaybackArray(String playbackString)
	{
		if(playbackString.length() == 0) return null;

		String[] stringArray = playbackString.split(",");
		int boolCount = stringArray.length;

		boolean[] playbackArray = new boolean[boolCount];

		for(int i=0; i<boolCount; i++)
		{
			playbackArray[i] = stringArray[i].equals("1");
		}
		return playbackArray;
	}

	@TypeConverter
	public static String getPlaybackString(boolean[] playbackArray)
	{
		if(playbackArray == null) return "";

		String playbackString = "";
		for(boolean b : playbackArray)
		{
			playbackString += ((b) ? "1" : "0" ) + ",";
		}
		return playbackString.substring(0, playbackString.length() - 1);
	}

}
