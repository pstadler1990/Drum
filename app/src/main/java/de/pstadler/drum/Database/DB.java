package de.pstadler.drum.Database;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


public class DB
{
	public static final int MESSAGE_TYPE_UNDEFINED = 0;
	public static final int MESSAGE_TYPE_GET_SOUNDS = 1;
	public static final int MESSAGE_TYPE_INSERT_SOUND_OK = 2;

	private static final String soundDatabaseName = "DB_SOUND";
	private SoundDatabase soundDatabase;


	@SuppressLint("HandlerLeak")
	public DB(Context context)
	{
		if (context != null)
		{
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
	public void getSoundsFromKit(final Handler handler, final String kitName)
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
				message.setTarget(handler);
				message.what = MESSAGE_TYPE_GET_SOUNDS;
				message.setData(bundle);

				if(handler != null) {
					handler.dispatchMessage(message);
				}
			}
		}).start();
	}

	public void insertSound(final Handler handler, final Sound... sounds)
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
					handler.dispatchMessage(message);
				}
			}
		}).start();
	}
}



