package de.pstadler.drum.Sound;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import java.util.ArrayList;
import java.util.Arrays;
import de.pstadler.drum.Database.Sound;
import de.pstadler.drum.IChildFragment;
import de.pstadler.drum.R;


public class SoundkitDownloadedFragment extends Fragment implements ISoundManager
{
	private IChildFragment childHandler;
	private ISoundSelected soundHandler;
	private ExpandableListView listViewDownloadedKits;
	private ArrayList<Soundkit> soundkits;
	private SoundkitsDownloadedAdapter soundkitAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		soundkits = new ArrayList<>();
		soundkitAdapter = new SoundkitsDownloadedAdapter(getActivity(), soundkits);
	}

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);

		/* Inform the parent activity / fragment, that this fragment is ready to use */
		if(childHandler instanceof IChildFragment)
		{
			childHandler.onChildCreated(getTag());
		}
	}

	public void setSoundHandler(ISoundSelected soundHandler)
	{
		this.soundHandler = soundHandler;
	}

	public void setChildHandler(IChildFragment childHandler)
	{
		this.childHandler = childHandler;
	}

	@NonNull
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_downloaded_soundkits, container, false);

		listViewDownloadedKits = rootView.findViewById(R.id.soundkits_list_kits);
		listViewDownloadedKits.setAdapter(soundkitAdapter);

		/* */
		listViewDownloadedKits.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
		{
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
			{
				if(soundHandler != null && (soundHandler instanceof ISoundSelected))
				{
					Sound sound = (Sound) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
					soundHandler.onSoundSelected(sound);
				}
				return false;
			}
		});

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
