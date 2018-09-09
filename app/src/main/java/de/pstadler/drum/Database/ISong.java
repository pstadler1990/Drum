package de.pstadler.drum.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;


@Dao
public interface ISong
{
	@Insert
	void insertSong(Song song);

	@Delete
	void deleteSong(Song song);

	@Query("SELECT * FROM Song")
	Song[] getSongs();

	@Query("SELECT * FROM SONG WHERE name = :name")
	Song getNameByName(String name);
}
