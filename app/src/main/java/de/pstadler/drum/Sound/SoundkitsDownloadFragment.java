package de.pstadler.drum.Sound;


import android.app.AlertDialog;
import android.app.ProgressDialog;
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
	private ProgressDialog progressDialog;
	private boolean hasHandler = false;

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
			hasHandler = true;

		}
		catch (ClassCastException e) {
			hasHandler = false;
		}
	}

	@NonNull
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_download_soundkits, container, false);

		listViewAvailableKits = rootView.findViewById(R.id.soundkits_list_kits);
		ViewGroup listViewHeader = (ViewGroup)getLayoutInflater().inflate(R.layout.soundkit_group_header, listViewAvailableKits, false);
		listViewAvailableKits.addHeaderView(listViewHeader);
		listViewAvailableKits.setEmptyView(rootView.findViewById(R.id.empty_list));
		listViewAvailableKits.setAdapter(soundkitAdapter);

		/* Attach a long click handler for the items, but only if the parent activity / fragment
		   implements the required interface */
		if(hasHandler)
		{
			listViewAvailableKits.setLongClickable(true);
			listViewAvailableKits.setOnItemClickListener(new AdapterView.OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> parent, View view, final int position, long id)
				{
					final Soundkit soundkit = (Soundkit) listViewAvailableKits.getItemAtPosition(position);

					/* Show dialog to confirm the download of the selected soundkit */
					AlertDialog dialog = new AlertDialog.Builder(getContext())
							.setPositiveButton("Download kit", new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									/* Requests the download of the selected kit from the github repository */
									if (soundkit != null)
									{
										DownloadSound[] downloadSounds = soundkit.downloadSounds.toArray(new DownloadSound[soundkit.downloadSounds.size()]);
										soundkit.elements = downloadSounds.length;
										iRequestDownload.requestDownload(downloadSounds);
									}
								}
							})
							.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									dialog.cancel();
								}
							})
							.setTitle("Download soundkit?")
							.setMessage(soundkit.name)
							.create();

					dialog.show();
				}
			});
		}

		return rootView;
	}

	@Override
	public void onDownloadStart()
	{
		/* Show indicator */
		progressDialog = new ProgressDialog(getContext());
		progressDialog.setMessage("Downloading files...");
		progressDialog.show();
	}

	@Override
	public void onDownloadProgress(int p)
	{
		progressDialog.setProgress(p);
	}

	@Override
	public void onDownloadCanceled()
	{
		progressDialog.dismiss();
	}

	@Override
	public void onDownloadComplete(List<?> result)
	{
		ArrayList<JSONArray> jsonList = (ArrayList<JSONArray>)result;

		progressDialog.dismiss();

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