package de.pstadler.drum;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import de.pstadler.drum.Database.DBHelper;
import de.pstadler.drum.Database.IDBHandler;
import de.pstadler.drum.Database.Sound;
import de.pstadler.drum.FileAccess.FileAccessor;
import de.pstadler.drum.Sound.Soundkit;
import de.pstadler.drum.Sound.SoundkitDownloadedFragment;
import de.pstadler.drum.Sound.SoundkitsDownloadFragment;
import de.pstadler.drum.http.DownloadSound;
import de.pstadler.drum.http.HttpDownloadTaskSound;
import de.pstadler.drum.http.IDownloadListener;
import static de.pstadler.drum.Database.DB.MESSAGE_TYPE_GET_SOUNDKITS;
import static de.pstadler.drum.Database.DB.MESSAGE_TYPE_GET_SOUNDS;
import static de.pstadler.drum.Database.DB.MESSAGE_TYPE_INSERT_SOUND_OK;
import static de.pstadler.drum.Database.DB.MESSAGE_TYPE_KIT_EXISTS;

/* This class is responsible for downloading and displaying a list of available soundkits from
the github repository */
public class SoundKitsActivity extends AppCompatActivity implements IDownloadListener,
		IRequestDownload<DownloadSound>,
		IDBHandler, IChildFragment
{
	private SoundkitsDownloadFragment soundkitsDownloadFragment;
	private SoundkitDownloadedFragment soundkitDownloadedFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound_kits);

		soundkitsDownloadFragment = new SoundkitsDownloadFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.soundkits_available_online_container, soundkitsDownloadFragment, "FRAGMENT_AVAILABLE_SOUNDKITS").commit();

		soundkitDownloadedFragment = new SoundkitDownloadedFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.soundkits_downloaded_container, soundkitDownloadedFragment, "FRAGMENT_DOWNLOADED_SOUNDKITS").commit();
	}

	@Override
	public void onProgress(int p)
	{
		// loading bar text
		//String percentageString = getString(R.string.httpdownloader_loading_percentage, (int)1);
		return; /* do nothing here yet, TODO: add code */
	}

	@Override
	public void onDownloadComplete(List<?> result)
	{
		/* Downloaded a list (initiated by requestDownload(..)) of (.wav) sounds into the result byte array list,
		   Write the files onto the internal disk and create an entry in the database */

		final ArrayList<DownloadSound> downloadedSounds = (ArrayList<DownloadSound>) result;

		for(int b=0; b<downloadedSounds.size(); b++)
		{
			final DownloadSound currentSound = downloadedSounds.get(b);
			String fileWithExt = FileAccessor.getWavFilename(getApplicationContext(), currentSound.name);
			final String currentPath = FileAccessor.getAbsolutePath(getApplicationContext(), currentSound.kitName, fileWithExt);

			/* Call Database API to store the file path references */
			((App)getApplicationContext()).getDatabase().getSoundExists(new IDBHandler()
			{
				@Override
				public void onMessageReceived(Message message)
				{
					switch (message.what)
					{
						case MESSAGE_TYPE_KIT_EXISTS:
							boolean exists = message.getData().getBoolean("soundExists");			//TODO: hard coded string
							if(!exists)
							{
								/* Write the files to the internal disk
			   					   writeFileToDisk(..) returns the absolute path to the file if it was successful */
								String fileNameWithExt = FileAccessor.getWavFilename(getApplicationContext(), currentSound.name);
								String dir = currentSound.kitName;
								String path = FileAccessor.writeFileToDisk(getApplicationContext(), dir, fileNameWithExt, currentSound.bytes);

								/* Create a new sound object and store it in the database */
								Sound sound = new Sound(currentSound.name, currentSound.kitName, path);

								((App) getApplicationContext()).getDatabase().insertSound(SoundKitsActivity.this, sound);
							}
							break;
					}
				}
			}, currentPath);
		}
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

	@Override
	public void onMessageReceived(Message message)
	{
		switch (message.what)
		{
			case MESSAGE_TYPE_GET_SOUNDS:
				Sound[] sounds = (Sound[])message.getData().getParcelableArray("getSounds");

				/* Add the sounds to the SoundkitDownloadedFragment's listview
				and call notifyDatasetChanged()! */
				Soundkit[] soundkitArray = DBHelper.createSoundkitsFromSounds(sounds);
				soundkitDownloadedFragment.onSoundkitAdd(soundkitArray);
				break;

			case MESSAGE_TYPE_INSERT_SOUND_OK:
				// TODO: notifyDatasetChanged on available sounds listview
				break;

			case MESSAGE_TYPE_GET_SOUNDKITS:
				/* Add the sounds to the SoundkitDownloadedFragment's listview
				and call notifyDatasetChanged()! */
				Soundkit[] soundkitsDownloaded = (Soundkit[]) message.getData().getSerializable("getSounds");
				soundkitDownloadedFragment.onSoundkitAdd(soundkitsDownloaded);
				break;
		}
	}

	@Override
	public void onChildCreated(String tag)
	{
		if(tag.equals("FRAGMENT_DOWNLOADED_SOUNDKITS"))
		{
			((App) getApplicationContext()).getDatabase().getSoundkits(this);
		}
	}
}
