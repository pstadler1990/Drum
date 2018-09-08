package de.pstadler.drum;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import de.pstadler.drum.Database.IDBHandler;
import de.pstadler.drum.Database.Song;
import static de.pstadler.drum.Database.DB.MESSAGE_TYPE_DELETE_SONG_OK;
import static de.pstadler.drum.Database.DB.MESSAGE_TYPE_GET_SONGS;


public class LoadProjectActivity extends AppCompatActivity implements IDBHandler
{
	private ListView listViewProjects;
	private ArrayList<Song> projects;
	private ProjectAdapter projectAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loadproject);

        listViewProjects = findViewById(R.id.load_list_projects);

		projects = new ArrayList<>();
        projectAdapter = new ProjectAdapter(this, projects);
        listViewProjects.setAdapter(projectAdapter);
		ViewGroup listViewHeader = (ViewGroup)getLayoutInflater().inflate(R.layout.song_item_header, listViewProjects, false);
       	listViewProjects.addHeaderView(listViewHeader);

        /* A long click on an item calls the delete dialog */
        listViewProjects.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
			{
				final Song song = (Song) listViewProjects.getItemAtPosition(position);

				AlertDialog dialog = new AlertDialog.Builder(LoadProjectActivity.this)
						.setPositiveButton("Delete", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								/* */
								if (song != null) {
									((App)getApplicationContext()).getDatabase().deleteSong(LoadProjectActivity.this, song);
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
						.setTitle("Delete project?")
						.setMessage(song.name)
						.create();

				dialog.show();

				return false;
			}
		});

        /* A short click on an item calls the load dialog */
        listViewProjects.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				System.out.println("blub");
			}
		});


        /* Fetch all saved projects / songs from the database */
		((App)getApplicationContext()).getDatabase().getSongs(this);
    }

	@Override
	public void onMessageReceived(Message message)
	{
		switch (message.what)
		{
			case MESSAGE_TYPE_GET_SONGS:
				projects.clear();
				Parcelable[] songs = message.getData().getParcelableArray("getSongs");
				for (Parcelable song : songs) {
					projects.add((Song)song);
				}
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						projectAdapter.notifyDataSetChanged();
					}
				});
				break;

			case MESSAGE_TYPE_DELETE_SONG_OK:
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						Toast.makeText(LoadProjectActivity.this, "Project deleted", Toast.LENGTH_SHORT).show();
						((App)getApplicationContext()).getDatabase().getSongs(LoadProjectActivity.this);
					}
				});
				break;
		}
	}
}
