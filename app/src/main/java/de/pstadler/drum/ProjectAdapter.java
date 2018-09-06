package de.pstadler.drum;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import de.pstadler.drum.Database.Song;


public class ProjectAdapter extends ArrayAdapter<Song>
{
	private ArrayList<Song> songs;
	private Context context;

	public ProjectAdapter(@NonNull Context context, ArrayList<Song> songs)
	{
		super(context, R.layout.song_item, songs);
		this.context = context;
		this.songs = songs;
	}

	@NonNull
	@Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
	{
		View v = convertView;
		if(v == null)
		{
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.soundkit_group, null);
		}

		TextView name = v.findViewById(R.id.song_item_name);

		Song song = songs.get(position);

		name.setText(song.name);

		return v;
	}
}
