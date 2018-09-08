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
			v = vi.inflate(R.layout.song_item, null);
		}

		TextView name = v.findViewById(R.id.song_item_name);
		TextView tracks = v.findViewById(R.id.song_item_tracks);
		TextView bars = v.findViewById(R.id.song_item_bars);
		TextView bpm = v.findViewById(R.id.song_item_bpm);

		Song song = songs.get(position);

		if(name != null) {
			name.setText(song.name);
		}
		else {
			name.setText("no name");	// TODO: hard coded
		}

		tracks.setText(String.valueOf(song.tracks));
		bars.setText(String.valueOf(song.bars));
		bpm.setText(String.valueOf(song.bpm));

		return v;
	}
}
