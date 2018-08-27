package de.pstadler.drum.Database;

import java.util.ArrayList;
import java.util.HashMap;
import de.pstadler.drum.Sound.Soundkit;


public class DBHelper
{
	public static Soundkit[] createSoundkitsFromSounds(Sound[] sounds)
	{
		HashMap<String, Soundkit> soundkits = new HashMap<>();

		for(Sound sound : sounds)
		{
			if(! soundkits.containsKey(sound.kitName))
			{
				Soundkit soundkit = new Soundkit();
				soundkit.name = sound.kitName;
				soundkit.sounds = new ArrayList<>();
				soundkits.put(sound.kitName, soundkit);
			}
			soundkits.get(sound.kitName).elements++;
			soundkits.get(sound.kitName).sounds.add(sound);
		}

		return soundkits.values().toArray(new Soundkit[0]);
	}
}
