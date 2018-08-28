package de.pstadler.drum.Sound;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import de.pstadler.drum.IRequestDownload;
import de.pstadler.drum.R;
import de.pstadler.drum.http.DownloadSound;
import de.pstadler.drum.http.HttpDownloadTaskJSON;
import de.pstadler.drum.http.IDownloadListener;


public class SoundkitsDownloadFragment extends Fragment implements IDownloadListener
{
	private IRequestDownload iRequestDownload;
    private ListView listViewAvailableKits;
    private ArrayList<Soundkit> soundkits;
    private SoundkitAdapter soundkitAdapter;

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

		soundkits = new ArrayList<>();
		soundkitAdapter = new SoundkitAdapter(getContext(), soundkits);

		/* Downloads a list of soundkits from the github repository */
		HttpDownloadTaskJSON downloadTask = new HttpDownloadTaskJSON(this);
		downloadTask.execute(getString(R.string.res_sound_kitlist));
    }


	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);

		try {
			iRequestDownload = (IRequestDownload) getActivity();
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException(context.toString() +
					" has not implemented IRequestDownload interface!");
		}
	}

	@NonNull
	@Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_download_soundkits, container, false);

        listViewAvailableKits = rootView.findViewById(R.id.soundkits_list_kits);
        listViewAvailableKits.setAdapter(soundkitAdapter);
        listViewAvailableKits.setLongClickable(true);
        listViewAvailableKits.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id)
			{
				/* Show dialog to confirm the download of the selected soundkit */
				AlertDialog dialog = new AlertDialog.Builder(getContext())
						.setPositiveButton("Download kit", new DialogInterface.OnClickListener()	//TODO: string hardcoded
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{						// TODO: hardcoded string
								/* Requests the download of the selected kit from the github repository */
								Soundkit soundkit = (Soundkit) listViewAvailableKits.getItemAtPosition(position);
								if(soundkit != null)
								{
									DownloadSound[] downloadSounds = soundkit.downloadSounds.toArray(new DownloadSound[soundkit.downloadSounds.size()]);
									soundkit.elements = downloadSounds.length;
									iRequestDownload.requestDownload(downloadSounds);
								}
							}
						})
						.setNegativeButton("Cancel", new DialogInterface.OnClickListener()			//TODO: string hardcoded
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								dialog.cancel();
							}
						})
						.setTitle("Download soundkit?")													// TODO: hardcoded string
						.create();

				dialog.show();

				return true;
			}
		});

        return rootView;
    }

	@Override
	public void onDownloadStart()
	{

	}

	@Override
	public void onDownloadProgress(int p)
	{
		return;	/* We don't want to show a progress update here, as we're just downloading
				   json files */
	}

	@Override
	public void onDownloadCanceled()
	{
		return; /* TODO: implement */
	}

	@Override
	public void onDownloadComplete(List<?> result)
	{
		ArrayList<JSONArray> jsonList = (ArrayList<JSONArray>)result;

		/* Converts the downloaded JsonArray to soundkit files */
		for(int i=0; i<jsonList.get(0).length(); i++)
		{
			JSONObject jsonObject = jsonList.get(0).optJSONObject(i);

			Soundkit soundkit = new Soundkit();
			soundkit.name = jsonObject.optString("kit-name");				//TODO: hardcoded string
			JSONObject kitElements = jsonObject.optJSONObject("kit-elements");	//TODO: hardcoded string

			/* Store url strings in the sound kit to be able to download the sample files later */
			if(kitElements.length() > 0)
			{
				soundkit.downloadSounds = new ArrayList<>();

				Iterator<String> iterator = kitElements.keys();
				while(iterator.hasNext())
				{
					String soundName = iterator.next();
					if(soundName.length() > 0)
					{
						/* Converts the relative sound file paths to the required format: /path/file.wav?raw=true */
						String soundUrlString = getString(R.string.res_sound_root_file_raw, kitElements.optString(soundName));
						if (soundUrlString.length() > 0)
						{
							/* DownloadSound is a boilerplate object to store important parameters
							   with a sound url, i.e. name and the name of the kit */
							DownloadSound downloadSound = new DownloadSound();
							downloadSound.kitName = soundkit.name;
							downloadSound.name = soundName;
							downloadSound.url = soundUrlString;
							soundkit.downloadSounds.add(downloadSound);
							soundkit.elements++;
						}
					}
				}
			}
			soundkits.add(soundkit);
		}

		soundkitAdapter.notifyDataSetChanged();
	}
}
