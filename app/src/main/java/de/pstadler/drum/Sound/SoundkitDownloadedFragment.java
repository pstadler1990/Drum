package de.pstadler.drum.Sound;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import java.util.ArrayList;
import java.util.Arrays;

import de.pstadler.drum.App;
import de.pstadler.drum.Database.IDBHandler;
import de.pstadler.drum.Database.Sound;
import de.pstadler.drum.IChildFragment;
import de.pstadler.drum.R;
import de.pstadler.drum.http.DownloadSound;

import static de.pstadler.drum.Database.DB.MESSAGE_TYPE_DELETE_KIT_OK;


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
		if (childHandler != null)
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

		/* A click on one of the child items triggers the onSoundSelected event */
		listViewDownloadedKits.setOnChildClickListener(new ExpandableListView.OnChildClickListener()
		{
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
			{
				if (soundHandler != null)
				{
					Sound sound = (Sound) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
					soundHandler.onSoundSelected(sound);
				}
				return false;
			}
		});

		/* A long click on the group triggers an AlertDialog to delete the selected kit from
		the database and local disk */
		listViewDownloadedKits.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{
			@Override
			/* Source: https://stackoverflow.com/questions/28561551/how-can-i-set-longclick-on-group-in-expandablelistview-android */
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
			{
				long packedPosition = listViewDownloadedKits.getExpandableListPosition(position);

				int itemType = ExpandableListView.getPackedPositionType(packedPosition);
				int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);

				if (itemType == ExpandableListView.PACKED_POSITION_TYPE_GROUP)
				{
					final Soundkit soundkit = (Soundkit) listViewDownloadedKits.getItemAtPosition(groupPosition);

					/* Show dialog to confirm the download of the selected soundkit */
					AlertDialog dialog = new AlertDialog.Builder(getContext())
							.setPositiveButton(R.string.delete_kit, new DialogInterface.OnClickListener()    //TODO: string hardcoded
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									/* Requests the download of the selected kit from the github repository,
									   Expects the current context to implement the IDBHandler interface, will throw
									   a ClassCastException else */
									if (soundkit != null)
									{
										try
										{
											((App) getActivity().getApplicationContext()).getDatabase().deleteKit((IDBHandler) getContext(), soundkit.name);
										} catch (ClassCastException e)
										{
											throw new ClassCastException(String.valueOf(R.string.idb_handler));    // TODO: hard coded string
										}
									}
								}
							})
							.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()            //TODO: string hardcoded
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									dialog.cancel();
								}
							})
							.setTitle(R.string.delete_soundkit)                                                    // TODO: hardcoded string
							.setMessage(soundkit.name)
							.create();

					dialog.show();
				}
				return false;
			}
		});

		return rootView;
	}

	@Override
	public void onSoundkitRefresh(Soundkit... soundkit)
	{
		soundkits.clear();
		onSoundkitAdd(soundkit);
	}

	@Override
	public void onSoundkitAdd(Soundkit... soundkit)
	{
		soundkits.addAll(Arrays.asList(soundkit));

		Activity activity = getActivity();
		if (activity != null)
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