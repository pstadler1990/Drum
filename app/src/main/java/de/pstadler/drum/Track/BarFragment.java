package de.pstadler.drum.Track;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import java.util.List;
import de.pstadler.drum.Database.Sound;
import de.pstadler.drum.MainActivity;
import de.pstadler.drum.R;


public class BarFragment extends Fragment implements ITrackListener
{
    public static String TAG = "BarFragment";
    public static int barCounter = 0;
    private int trackCount = 0;
    private int barId;
    private LinearLayout trackContainer;
    private Context context;


	public BarFragment()
	{
		barId = barCounter++;
	}

	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /*Restore button states for all tracks in this bar*/
        if(savedInstanceState != null)
        {
            restoreTrackInformation(savedInstanceState);
        }
		setRetainInstance(true);
    }

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		this.context = context;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		((MainActivity)context).barFragmentIsReady(this, barId);
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_bar, container, false);

        trackContainer = rootView.findViewById(R.id.fragment_track_container);

        return rootView;
    }

	public boolean createNewTrack(int trackId, Sound sound)
    {
		android.support.v4.app.FragmentManager fragmentManager = getChildFragmentManager();
		if (fragmentManager != null)
		{
			TrackFragment trackFragment = new TrackFragment();

			Bundle bundle = new Bundle();
			bundle.putInt("trackId", trackId);
			bundle.putParcelable("sound", sound);
			trackFragment.setArguments(bundle);

			fragmentManager.beginTransaction().add(trackContainer.getId(), trackFragment).commitNow();
			trackCount++;
			return true;
		}

		return false;
    }

    public int getTrackCount()
    {
        return trackCount;
    }

    public List<Fragment> getTracks()
    {
    	if(isAdded())
		{
			android.support.v4.app.FragmentManager fragmentManager = getChildFragmentManager();
			return fragmentManager.getFragments();
		}
		else {
    		return null;
		}
    }

    @Override
    public void onAddTrackListener(int trackId, Sound sound)
    {
        createNewTrack(trackId, sound);
    }

    public void restoreTrackInformation(Bundle bundle)
    {
        List<Fragment> tracks = getTracks();
        if(tracks != null)
		{
			for (int i = 0; i < getTracks().size(); i++)
			{
				TrackFragment tf = (TrackFragment) tracks.get(i);
				String key = String.format("buttonStates_%d", i);
				boolean[] savedStates = bundle.getBooleanArray(key);
				tf.setButtonStates(savedStates);
			}
		}
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        List<Fragment> tracks = getTracks();

        int i = 0;
        for(Fragment f : tracks)
        {
        	if(f instanceof TrackFragment)
			{
				TrackFragment tf = (TrackFragment) f;
				boolean[] buttonStates = tf.getButtonStates();
				String bId = String.format("buttonStates_%d", i);
				outState.putBooleanArray(bId, buttonStates);
			}
			i++;
        }

        super.onSaveInstanceState(outState);
    }
}
