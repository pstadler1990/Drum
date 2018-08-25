package de.pstadler.drum.Sound;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.pstadler.drum.R;
import de.pstadler.drum.Sound.Soundkit;
import de.pstadler.drum.http.HttpDownloadTaskJSON;
import de.pstadler.drum.http.IDownloadListener;


public class SoundkitsDownloadFragment extends Fragment implements IDownloadListener
{
    private ListView listViewAvailableKits;
    private ArrayList<Soundkit> soundkits;
    private SoundkitAdapter soundkitAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

		//TODO: custom adapter for listview and then add downloaded kits into the listview
		soundkits = new ArrayList<>();
		soundkitAdapter = new SoundkitAdapter(getContext(), soundkits);

		HttpDownloadTaskJSON downloadTask = new HttpDownloadTaskJSON(this);
		downloadTask.execute(getString(R.string.res_sound_kitlist));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_download_soundkits, container, false);

        listViewAvailableKits = rootView.findViewById(R.id.soundkits_list_kits);

        listViewAvailableKits.setAdapter(soundkitAdapter);

        return rootView;
    }

	@Override
	public void onProgress(int p)
	{
		/* TODO: Show progress p */
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
						String soundUrlString = kitElements.optString(soundName);
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
