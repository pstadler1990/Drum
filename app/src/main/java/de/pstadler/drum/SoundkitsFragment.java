package de.pstadler.drum;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import de.pstadler.drum.http.HttpDownloadTaskJSON;
import de.pstadler.drum.http.IDownloadListener;


public class SoundkitsFragment extends Fragment implements IDownloadListener
{
    private ListView listViewAvailableKits;
    private ArrayList<JSONArray> dummyList;

    public static final String kitlistDownloadUrl = "https://raw.githubusercontent.com/pstadler1990/Drum/master/kitlist.json?token=AThErQbD0utaIAMf6VpllUHXiqxnbrArks5bdIZgwA%3D%3D";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

		//TODO: custom adapter for listview and then add downloaded kits into the listview

		HttpDownloadTaskJSON downloadTask = new HttpDownloadTaskJSON(this);
		downloadTask.execute(kitlistDownloadUrl);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_soundkits, container, false);

        listViewAvailableKits = rootView.findViewById(R.id.soundkits_list_kits);

        return rootView;
    }

	@Override
	public void onProgress(int p)
	{
		Toast.makeText(getContext(), "Download completed!", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onDownloadComplete(List<?> result)
	{
		dummyList = (ArrayList<JSONArray>)result;

		Log.d("ss", "a");
	}
}
