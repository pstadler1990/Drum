package de.pstadler.drum.Database.Converter;

import android.arch.persistence.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

public class SongPlaybackConverter
{
	@TypeConverter
	public static List<String> getPlaybackList(String playbackString)
	{
		if(playbackString.length() == 0) return null;

		String[] stringArray = playbackString.split(",");
		int boolCount = stringArray.length;

		String trackString = "";
		ArrayList<String> playbackList = new ArrayList<>();

		for(int i=0, j=1; i<boolCount; i++, j++)
		{
			trackString += stringArray[i];
			if(j%8 == 0 && i>0)
			{
				playbackList.add(trackString);
				trackString = "";
			}
		}
		return playbackList;
	}

	@TypeConverter
	public static String getPlaybackString(List<String> playbackList)
	{
		if(playbackList == null) return "";

		String playbackString = "";
		for(String s : playbackList)
		{
			playbackString += s + ",";
		}
		return playbackString.substring(0, playbackString.length() - 1);
	}

	@TypeConverter
	public static String getPlaybackString(boolean[] playbackArray)
	{
		if(playbackArray == null) return "";

		String playbackString = "";
		for(boolean b : playbackArray)
		{
			playbackString += ((b) ? "1" : "0") + ",";
		}
		return playbackString.substring(0, playbackString.length() - 1);
	}
}
