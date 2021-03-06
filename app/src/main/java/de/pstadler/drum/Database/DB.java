package de.pstadler.drum.Database;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import de.pstadler.drum.FileAccess.FileAccessor;
import de.pstadler.drum.Sound.Soundkit;


public class DB implements IDBHandler
{
	public static final int MESSAGE_TYPE_UNDEFINED = 0;
	public static final int MESSAGE_TYPE_GET_SOUNDS = 1;
	public static final int MESSAGE_TYPE_INSERT_SOUND_OK = 2;
	public static final int MESSAGE_TYPE_DELETE_KIT_OK = 3;
	public static final int MESSAGE_TYPE_KIT_EXISTS = 4;
	public static final int MESSAGE_TYPE_GET_SOUNDKITS = 5;
	public static final int MESSAGE_TYPE_GET_SONGS = 6;
	public static final int MESSAGE_TYPE_INSERT_SONG_OK = 7;
	public static final int MESSAGE_TYPE_UPDATE_SONG_OK = 8;
	public static final int MESSAGE_TYPE_DELETE_SONG_OK = 9;

	private static final String soundDatabaseName = "DB_SOUND";
	private SoundDatabase soundDatabase;
	private Context context;


	public DB(Context context)
	{
		if (context != null)
		{
			this.context = context;
			soundDatabase = Room.databaseBuilder(context, SoundDatabase.class, soundDatabaseName)
					.fallbackToDestructiveMigration()
					.build();
		}
	}

	public void closeDatabase()
	{
		soundDatabase.close();
	}


	/* Get an array of sounds from the database and send them via the given handler */
	public void getSoundsFromKit(final IDBHandler handler, final String kitName)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Sound[] sounds = soundDatabase.getSoundInterface().getSoundsFromKit(kitName);

				Message message = new Message();
				Bundle bundle = new Bundle();

				bundle.putParcelableArray("getSounds", sounds);
				message.what = MESSAGE_TYPE_GET_SOUNDS;
				message.setData(bundle);

				if(handler != null) {
					handler.onMessageReceived(message);
				}
			}
		}).start();
	}

	public void getSoundkits(final IDBHandler handler)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Sound[] sounds = soundDatabase.getSoundInterface().getSounds();
				Soundkit[] soundkits = DBHelper.createSoundkitsFromSounds(sounds);

				Message message = new Message();
				Bundle bundle = new Bundle();

				bundle.putParcelableArray("getSoundkits", soundkits);
				message.what = MESSAGE_TYPE_GET_SOUNDKITS;
				message.setData(bundle);

				if(handler != null) {
					handler.onMessageReceived(message);
				}
			}
		}).start();
	}

	public void insertSound(final IDBHandler handler, final Sound...sounds)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				for (Sound sound : sounds)
				{
					soundDatabase.getSoundInterface().insertSound(sound);
				}

				Message message = new Message();
				message.what = MESSAGE_TYPE_INSERT_SOUND_OK;

				if(handler != null) {
					handler.onMessageReceived(message);
				}
			}
		}).start();
	}

	/* This method has side effects: it will also try to delete the files associated
	   with the kit name */
	public void deleteKit(final IDBHandler handler, final String...kitNames)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				for(String kitName : kitNames)
				{
					soundDatabase.getSoundInterface().deleteKit(kitName);

					FileAccessor.deleteDirectoryRecursivelyFromDisk(context, kitName);
				}

				Message message = new Message();
				message.what = MESSAGE_TYPE_DELETE_KIT_OK;

				if(handler != null) {
					handler.onMessageReceived(message);
				}
			}
		}).start();
	}

	public void getKitExists(final IDBHandler handler, final String kitName)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				boolean exists = (soundDatabase.getSoundInterface().getKitExists(kitName) > 0);

				Message message = new Message();
				message.what = MESSAGE_TYPE_KIT_EXISTS;

				Bundle bundle = new Bundle();
				bundle.putBoolean("kitExists", exists);
				message.setData(bundle);

				if(handler != null) {
					handler.onMessageReceived(message);
				}
			}
		}).start();
	}

	public void getSoundExists(final IDBHandler handler, final String path)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				boolean exists = (soundDatabase.getSoundInterface().getSoundExists(path) > 0);

				Message message = new Message();
				message.what = MESSAGE_TYPE_KIT_EXISTS;

				Bundle bundle = new Bundle();
				bundle.putBoolean("soundExists", exists);
				message.setData(bundle);

				if(handler != null) {
					handler.onMessageReceived(message);
				}
			}
		}).start();
	}

	public void getSongs(final IDBHandler handler)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				Song[] songs = soundDatabase.getSongInterface().getSongs();

				Message message = new Message();
				Bundle bundle = new Bundle();

				bundle.putParcelableArray("getSongs", songs);
				message.what = MESSAGE_TYPE_GET_SONGS;
				message.setData(bundle);

				if(handler != null) {
					handler.onMessageReceived(message);
				}
			}
		}).start();
	}

	public void insertSong(final IDBHandler handler, final Song song)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				soundDatabase.getSongInterface().insertSong(song);

				Bundle bundle = new Bundle();
				bundle.putParcelable("getSong", song);

				Message message = new Message();
				message.what = MESSAGE_TYPE_INSERT_SONG_OK;
				message.setData(bundle);

				if(handler != null) {
					handler.onMessageReceived(message);
				}
			}
		}).start();
	}

	public void updateSong(final IDBHandler handler, final Song song)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				soundDatabase.getSongInterface().deleteSong(song);
				insertSong(DB.this, song);

				Message message = new Message();
				message.what = MESSAGE_TYPE_UPDATE_SONG_OK;

				if(handler != null) {
					handler.onMessageReceived(message);
				}
			}
		}).start();
	}

	public void deleteSong(final IDBHandler handler, final Song...songs)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				for(Song song : songs) {
					soundDatabase.getSongInterface().deleteSong(song);
				}

				Message message = new Message();
				message.what = MESSAGE_TYPE_DELETE_SONG_OK;

				if(handler != null) {
					handler.onMessageReceived(message);
				}
			}
		}).start();
	}

	@Override
	public void onMessageReceived(Message message)
	{
		switch (message.what)
		{
			case MESSAGE_TYPE_INSERT_SONG_OK:
				Song song = message.getData().getParcelable("getSong");
				Message messageUpdate = new Message();
				messageUpdate.what = MESSAGE_TYPE_UPDATE_SONG_OK;
				Bundle bundle = new Bundle();
				bundle.putParcelable("song", song);
				message.setData(bundle);
				break;
		}
	}
}