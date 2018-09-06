package de.pstadler.drum.Database.Converter;

import android.arch.persistence.room.TypeConverter;
import de.pstadler.drum.Database.Sound;


public class SongSoundConverter
{
	@TypeConverter
	public static Sound[] getSounds(String soundString)
	{
		String[] fields = soundString.split(",");
		if(fields.length == 0) return null;

		int soundCount = fields.length / 3;
		Sound[] sounds = new Sound[soundCount];
		for(int i=0; i<soundCount; i++)
		{
			sounds[i] = new Sound(fields[0], fields[1], fields[2]);
		}
		return sounds;
	}

	@TypeConverter
	public static String getSoundString(Sound[] sounds)
	{
		if(sounds == null || sounds.length == 0) return "";
		String soundString = "";
		for(Sound sound : sounds)
		{
			soundString += sound.name + "," + sound.kitName + "," + sound.path + ",";
		}
		return soundString.substring(0, soundString.length() - 1);
	}
}
