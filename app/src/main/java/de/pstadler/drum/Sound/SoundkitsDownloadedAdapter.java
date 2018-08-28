package de.pstadler.drum.Sound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import de.pstadler.drum.Database.Sound;
import de.pstadler.drum.R;

/* Custom ArrayAdapter for the Soundkit entries with an expandable children view
   for browsing and (if needed) selecting a single sound from the sound kit */

public class SoundkitsDownloadedAdapter extends BaseExpandableListAdapter
{
	private final LayoutInflater inflater;
	private ArrayList<Soundkit> soundkits;
	private Context context;

	public SoundkitsDownloadedAdapter(Context context, ArrayList<Soundkit> soundkits)
	{
		this.inflater = LayoutInflater.from(context);
		this.soundkits = soundkits;
		this.context = context;
	}

	@Override
	public int getGroupCount()
	{
		return soundkits.size();
	}

	@Override
	public int getChildrenCount(int groupPosition)
	{
		return soundkits.get(groupPosition).sounds.size();
	}

	@Override
	public Object getGroup(int groupPosition)
	{
		return soundkits.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition)
	{
		return soundkits.get(groupPosition).sounds.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition)
	{
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition)
	{
		return childPosition;
	}

	@Override
	public boolean hasStableIds()
	{
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
	{
		View v = convertView;

		if(v == null) {
			v = inflater.inflate(R.layout.soundkit_group, null);
		}

		ImageView icon = v.findViewById(R.id.soundkit_group_icon);
		TextView name = v.findViewById(R.id.soundkit_group_name);
		TextView elements = v.findViewById(R.id.soundkit_group_elements);

		Soundkit soundkit = (Soundkit) getGroup(groupPosition);

		/* If there's no icon, provide the custom one */
		if(soundkit.icon == null) {
			icon.setImageDrawable(context.getDrawable(R.drawable.ic_broken_image_black_24dp));
		}
		else {
			icon.setImageBitmap(soundkit.icon);
		}

		name.setText(soundkit.name);
		elements.setText(soundkit.elements);

		return v;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
							 final ViewGroup parent)
	{
		View v = convertView;

		if(v == null) {
			v = inflater.inflate(R.layout.soundkit_item, null);
		}

		TextView name = v.findViewById(R.id.soundkit_item_name);
		Sound sound = (Sound) getChild(groupPosition, childPosition);
		name.setText(sound.name);

		return v;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition)
	{
		return true;
	}
}
