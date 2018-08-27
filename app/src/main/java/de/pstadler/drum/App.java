package de.pstadler.drum;

import android.app.Application;
import de.pstadler.drum.Database.DB;


public class App extends Application
{
	private DB database;

	@Override
	public void onCreate()
	{
		super.onCreate();

		database = new DB(getApplicationContext());
	}

	@Override
	public void onTerminate()
	{
		super.onTerminate();

		database.closeDatabase();
	}

	public DB getDatabase()
	{
		return database;
	}
}
