package de.pstadler.drum.Sound;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import de.pstadler.drum.http.HttpDownloadTaskJSON;
import de.pstadler.drum.http.IDownloadListener;


public class SoundkitsDownloadFragment extends Fragment implements IDownloadListener
{
	private String resSoundRootPath;
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

		resSoundRootPath = getString(R.string.res_sound_root);

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

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
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
							{
								/* TODO: Download the selected kit from the github repository
								   Store the files onto the file system
								   Add kit to downloaded kits in the database*/
								Soundkit soundkit = (Soundkit) listViewAvailableKits.getItemAtPosition(position);
								if(soundkit != null)
								{
									String[] urlStrings = soundkit.urlStrings.toArray(new String[soundkit.urlStrings.size()]);
									iRequestDownload.requestDownload(urlStrings);
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
						.setTitle("Download soundkit?")
						.create();

				dialog.show();

				return true;
			}
		});

        return rootView;
    }

	@Override
	public void onProgress(int p)
	{
		return;	/* We don't want to show a progress update here, as we're just downloading
				   json files */
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
			soundkit.name = jsonObject.optString("kit-name");
			JSONObject kitElements = jsonObject.optJSONObject("kit-elements");

			/* Store url strings in the sound kit to be able to download the sample files later */
			if(kitElements.length() > 0)
			{
				soundkit.urlStrings = new ArrayList<>();

				Iterator<String> iterator = kitElements.keys();
				while(iterator.hasNext())
				{
					String soundName = iterator.next();
					if(soundName.length() > 0)
					{
						String soundUrlString = resSoundRootPath + kitElements.optString(soundName);
						if (soundUrlString.length() > 0)
						{
							soundkit.urlStrings.add(soundUrlString);
						}
					}
				}
			}
			soundkits.add(soundkit);
		}

		soundkitAdapter.notifyDataSetChanged();
	}
}
