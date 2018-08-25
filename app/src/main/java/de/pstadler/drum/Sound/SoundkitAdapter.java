package de.pstadler.drum.Sound;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import de.pstadler.drum.R;


/* Custom ArrayAdapter for the Soundkit entries
   [ ICON ] [ SOUNDKIT_NAME ] [ NUMBER_OF_ELEMENTS_IN_SOUNDKIT ] */
public class SoundkitAdapter extends ArrayAdapter<Soundkit>
{
	private ArrayList<Soundkit> soundkits;
	private Context context;

	public SoundkitAdapter(@NonNull Context context, ArrayList<Soundkit> soundkits)
	{
		super(context, R.layout.soundkit_item, soundkits);
		this.context = context;
		this.soundkits = soundkits;
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
	{
		View v = convertView;
		if(v == null)
		{
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.soundkit_item, null);
		}

		ImageView icon = v.findViewById(R.id.soundkit_item_icon);
		TextView name = v.findViewById(R.id.soundkit_item_name);
		TextView elements = v.findViewById(R.id.soundkit_item_elements);

		Soundkit soundkit = soundkits.get(position);

		/* If there's no icon, provide the custom one */
		if(soundkit.icon == null) {
			icon.setImageDrawable(context.getDrawable(R.drawable.ic_broken_image_black_24dp));
		}
		else {
			icon.setImageBitmap(soundkit.icon);
		}

		name.setText(soundkit.name);
		elements.setText(Integer.toString(soundkit.downloadSounds.size()));

		return v;
	}
}
