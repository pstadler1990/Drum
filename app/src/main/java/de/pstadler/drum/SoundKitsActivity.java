package de.pstadler.drum;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.invoke.ConstantCallSite;
import java.util.ArrayList;
import java.util.List;

import de.pstadler.drum.Sound.SoundkitsDownloadFragment;
import de.pstadler.drum.http.DownloadSound;
import de.pstadler.drum.http.HttpDownloadTaskSound;
import de.pstadler.drum.http.IDownloadListener;


/* This class is responsible for downloading and displaying a list of available soundkits from
the github repository */
public class SoundKitsActivity extends AppCompatActivity implements IDownloadListener, IRequestDownload<DownloadSound>
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
		/* Downloaded a list (initiated by requestDownload(..)) of (.wav) sounds into the result byte array list,
		   Write the files onto the internal disk and create an entry in the database */

		ArrayList<DownloadSound> downloadedSounds = (ArrayList<DownloadSound>) result;

		for(int b=0; b<downloadedSounds.size(); b++)
		{
			/* Convert the sound name to a standardized file name => <sound_name>.wav */
			String fileName = getString(R.string.res_sound_name_template, downloadedSounds.get(b).name);
			FileOutputStream outputStream;

			File file = new File(getDir(downloadedSounds.get(b).kitName, MODE_PRIVATE), fileName);
			file.setWritable(true);
			file.setReadable(true);

			try
			{
				outputStream = new FileOutputStream(file);
				outputStream.write(downloadedSounds.get(b).bytes);
				outputStream.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		/* TODO: Call Database API to store the file path references */
	}

	@Override
	public void requestDownload(DownloadSound... files)
	{
		/* Child fragment (SoundkitsDownloadFragment) calls this interface method
		to request the download of a given list of strings */

		if(files.length > 0)
		{
			HttpDownloadTaskSound httpDownloadTaskSound = new HttpDownloadTaskSound(this);
			httpDownloadTaskSound.execute(files);
		}
	}
}
