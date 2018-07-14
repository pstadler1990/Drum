package de.pstadler.drum;

import android.app.Activity;
import android.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;


public class BarFragment extends Fragment implements ITrackListener
{
    private static int barCounter = 0;
    private int barId;
    private LinearLayout trackContainer;
    private TextView textViewId;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_bar, container, false);

        trackContainer = rootView.findViewById(R.id.fragment_track_container);
        textViewId = rootView.findViewById(R.id.fragment_bar_id);
        textViewId.setText("Hello I am: " + barId);

        return rootView;
    }

    public boolean createNewTrack()
    {
        android.support.v4.app.FragmentManager fragmentManager = getChildFragmentManager();
        if(fragmentManager != null)
        {
            TrackFragment trackFragment = new TrackFragment();
            fragmentManager.beginTransaction().add(trackContainer.getId(), trackFragment).commit();
            return true;
        }
        return false;
    }

    @Override
    public void onAddTrackListener(int trackId)
    {
        createNewTrack();
    }
}
