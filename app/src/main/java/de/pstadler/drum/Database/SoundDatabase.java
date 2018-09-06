package de.pstadler.drum.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import de.pstadler.drum.Database.Converter.SongPlaybackConverter;
import de.pstadler.drum.Database.Converter.SongSoundConverter;


@TypeConverters({SongSoundConverter.class, SongPlaybackConverter.class})
@Database(entities = {Sound.class, Song.class}, version = 5, exportSchema = false)
public abstract class SoundDatabase extends RoomDatabase
{
	public abstract ISound getSoundInterface();
	public abstract ISong getSongInterface();
}