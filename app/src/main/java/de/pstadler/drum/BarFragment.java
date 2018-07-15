package de.pstadler.drum;


import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


public class BarFragment extends Fragment implements ITrackListener
{
    private static int barCounter = 0;
    private int trackCount = 0;
    private int barId;
    private LinearLayout trackContainer;

    public BarFragment()
    {
        barId = barCounter++;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_bar, container, false);

        trackContainer = rootView.findViewById(R.id.fragment_track_container);

        return rootView;
    }

    public boolean createNewTrack(int trackId, int instrumentId, String...instrumentName)
    {
        android.support.v4.app.FragmentManager fragmentManager = getChildFragmentManager();
        if(fragmentManager != null)
        {
            TrackFragment trackFragment = new TrackFragment();

            Bundle bundle = new Bundle();
            bundle.putInt("trackId", trackId);
            bundle.putInt("instrumentId", instrumentId);
            if(instrumentName.length > 0)
                bundle.putString("instrumentName", instrumentName[0]);
            trackFragment.setArguments(bundle);

            fragmentManager.beginTransaction().add(trackContainer.getId(), trackFragment).commit();
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
        android.support.v4.app.FragmentManager fragmentManager = getChildFragmentManager();
        return fragmentManager.getFragments();
    }

    @Override
    public void onAddTrackListener(int trackId, int instrumentId)
    {
        createNewTrack(trackId, instrumentId);
    }
}
