package de.pstadler.drum;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.lang.invoke.ConstantCallSite;
import java.util.ArrayList;
import java.util.List;

import de.pstadler.drum.Sound.SoundkitsDownloadFragment;
import de.pstadler.drum.http.HttpDownloadTaskSound;
import de.pstadler.drum.http.IDownloadListener;


/* This class is responsible for downloading and displaying a list of available soundkits from
the github repository */
public class SoundKitsActivity extends AppCompatActivity implements IDownloadListener, IRequestDownload
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound_kits);

		SoundkitsDownloadFragment soundkitsDownloadFragment = new SoundkitsDownloadFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.soundkits_available_online_container, soundkitsDownloadFragment).commit();

	}

	@Override
	public void onProgress(int p)
	{
		return; /* do nothing here yet, TODO: add code */
	}

	@Override
	public void onDownloadComplete(List<?> result)
	{
		/* Downloaded a list of (wav) sounds into the result byte array list,
		   Write the files onto the internal disk and create an entry in the database */

		ArrayList<byte[]> byteFiles = (ArrayList<byte[]>) result;
		System.out.println(byteFiles);


	}

	@Override
	public void requestDownload(String... urls)
	{
		// the list of download urls will be placed here..
		// TODO: Create a HttpDownloadTaskSound task here
		if(urls.length > 0)
		{
			HttpDownloadTaskSound httpDownloadTaskSound = new HttpDownloadTaskSound(this);
			httpDownloadTaskSound.execute(urls);
		}
	}
}
