package de.pstadler.drum.Sound;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import java.util.ArrayList;
import java.util.Arrays;
import de.pstadler.drum.IChildFragment;
import de.pstadler.drum.R;


public class SoundkitDownloadedFragment extends Fragment implements ISoundManager
{
	private IChildFragment parent;
	private ExpandableListView listViewDownloadedKits;
	private ArrayList<Soundkit> soundkits;
	private SoundkitsDownloadedAdapter soundkitAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		soundkits = new ArrayList<>();
		soundkitAdapter = new SoundkitsDownloadedAdapter(getActivity(), soundkits);

		/* As the fragment is attached, inform the parent activity to download the soundkits */
		if(parent != null)
		{
			parent.onChildCreated(getTag());
		}
	}

	public void setParent(IChildFragment parent)
	{
		this.parent = parent;
	}

	@NonNull
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_downloaded_soundkits, container, false);

		listViewDownloadedKits = rootView.findViewById(R.id.soundkits_list_kits);
		listViewDownloadedKits.setAdapter((ExpandableListAdapter)soundkitAdapter);

		return rootView;
	}

	@Override
	public void onSoundkitAdd(Soundkit...soundkit)
	{
		soundkits.addAll(Arrays.asList(soundkit));

		Activity activity = getActivity();
		if(activity != null)
		{
			activity.runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					if (soundkitAdapter != null)
					{
						soundkitAdapter.notifyDataSetChanged();
					}
				}
			});
		}
	}
}
