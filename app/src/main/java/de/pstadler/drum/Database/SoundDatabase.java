package de.pstadler.drum.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;


@Database(entities = {Sound.class}, version = 2, exportSchema = false)
public abstract class SoundDatabase extends RoomDatabase
{
	public abstract ISound getSoundInterface();
}