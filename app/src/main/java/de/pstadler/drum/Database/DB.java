package de.pstadler.drum.Database;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import de.pstadler.drum.FileAccess.FileAccessor;


public class DB
{
	public static final int MESSAGE_TYPE_UNDEFINED = 0;
	public static final int MESSAGE_TYPE_GET_SOUNDS = 1;
	public static final int MESSAGE_TYPE_INSERT_SOUND_OK = 2;
	public static final int MESSAGE_TYPE_DELETE_KIT_OK = 3;
	public static final int MESSAGE_TYPE_KIT_EXISTS = 4;

	private static final String soundDatabaseName = "DB_SOUND";		//TODO: hard coded string
	private SoundDatabase soundDatabase;
	private Context context;


	@SuppressLint("HandlerLeak")
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

				bundle.putParcelableArray("getSounds", sounds);		//TODO: hard coded string
				message.what = MESSAGE_TYPE_GET_SOUNDS;
				message.setData(bundle);

				if(handler != null) {
					handler.onMessageReceived(message);
				}
			}
		}).start();
	}

	public void insertSound(final IDBHandler handler, final Sound... sounds)
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
				bundle.putBoolean("kitExists", exists);			//TODO: hard coded string
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
				bundle.putBoolean("SoundExists", exists);		//TODO: hard coded string
				message.setData(bundle);

				if(handler != null) {
					handler.onMessageReceived(message);
				}
			}
		}).start();
	}

}



