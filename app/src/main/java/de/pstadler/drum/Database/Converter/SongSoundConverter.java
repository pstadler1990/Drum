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

		/* String format is: <sound name>,<kit name>,<path>, ...
		   => Divide it by 3 */
		int soundCount = fields.length / 3;
		Sound[] sounds = new Sound[soundCount];
		for(int i=0, j=0; i<fields.length; i+=3)
		{
			sounds[j] = new Sound(fields[i], fields[i+1], fields[i+2]);
			j++;
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
