package de.pstadler.drum;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import de.pstadler.drum.Database.IDBHandler;
import de.pstadler.drum.Database.Sound;
import de.pstadler.drum.FileAccess.FileAccessor;
import de.pstadler.drum.Sound.SoundkitsDownloadFragment;
import de.pstadler.drum.http.DownloadSound;
import de.pstadler.drum.http.HttpDownloadTaskSound;
import de.pstadler.drum.http.IDownloadListener;

import static de.pstadler.drum.Database.DB.MESSAGE_TYPE_DELETE_KIT_OK;
import static de.pstadler.drum.Database.DB.MESSAGE_TYPE_GET_SOUNDS;
import static de.pstadler.drum.Database.DB.MESSAGE_TYPE_INSERT_SOUND_OK;
import static de.pstadler.drum.Database.DB.MESSAGE_TYPE_UNDEFINED;


/* This class is responsible for downloading and displaying a list of available soundkits from
the github repository */
public class SoundKitsActivity extends AppCompatActivity implements IDownloadListener,
		IRequestDownload<DownloadSound>,
		IDBHandler
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound_kits);

		SoundkitsDownloadFragment soundkitsDownloadFragment = new SoundkitsDownloadFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.soundkits_available_online_container, soundkitsDownloadFragment).commit();

		//FileAccessor.readFileFromDisk(
		((App)getApplicationContext()).getDatabase().deleteKit(this,"east-coast-hh");
		((App)getApplicationContext()).getDatabase().getSoundsFromKit( this, "east-coast-hh");
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
		Sound[] sounds = new Sound[downloadedSounds.size()];

		for(int b=0; b<downloadedSounds.size(); b++)
		{
			DownloadSound currentSound = downloadedSounds.get(b);

			/* Write the files to the internal disk
			   writeFileToDisk(..) returns the absolute path to the file if it was successful */

			String fileNameWithExt = FileAccessor.getWavFilename(getApplicationContext(), currentSound.name);
			String dir = currentSound.kitName;
			String path = FileAccessor.writeFileToDisk(getApplicationContext(), dir, fileNameWithExt, currentSound.bytes);

			if(path.length() > 0)
			{
				sounds[b] = new Sound(currentSound.name, currentSound.kitName, path);
			}
		}

		/* Call Database API to store the file path references */
		((App) getApplicationContext()).getDatabase().insertSound(this, sounds);
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
				for(Sound s : sounds)
				{
					// TODO: Add the sounds to the available kits fragment (listview), grouped by kitname
				}
				break;

			case MESSAGE_TYPE_DELETE_KIT_OK:
				break;

			case MESSAGE_TYPE_INSERT_SOUND_OK:
				break;

			case MESSAGE_TYPE_UNDEFINED:
			default:
				break;
		}
	}
}
