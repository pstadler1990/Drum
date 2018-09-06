package de.pstadler.drum.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;


@Dao
public interface ISound
{
	@Insert
	void insertSound(Sound sound);

	@Delete
	void deleteSound(Sound sound);

	@Query("SELECT * FROM Sound WHERE kitName = :kitName")
	Sound[] getSoundsFromKit(String kitName);

	@Query("DELETE FROM Sound WHERE kitName = :kitName")
	void deleteKit(String kitName);

	@Query("SELECT COUNT(*) FROM Sound WHERE kitName = :kitName")
	int getKitExists(String kitName);

	@Query("SELECT COUNT(*) FROM Sound WHERE path = :path")
	int getSoundExists(String path);

	@Query("SELECT * FROM Sound")
	Sound[] getSounds();
}
