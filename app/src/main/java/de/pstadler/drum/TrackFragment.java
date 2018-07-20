package de.pstadler.drum;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class TrackFragment extends Fragment implements View.OnClickListener
{
    public static final String TAG = "TRACK";
    public static final int NUMBER_OF_BUTTONS = 8;
    private LinearLayout buttonContainer;
    private TextView instrumentTextView;
    private ArrayList<Button> buttons = new ArrayList<>();
    private int instrumentId;
    private int trackId;
    private String instrumentName;
    private boolean[] buttonStates;

    public TrackFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        buttonStates = new boolean[NUMBER_OF_BUTTONS];

        if(savedInstanceState != null)
        {
            boolean[] tmpStates = savedInstanceState.getBooleanArray("buttonStates");
            buttonStates = (tmpStates == null)? buttonStates : tmpStates;
            trackId = savedInstanceState.getInt("trackId");
            instrumentId = savedInstanceState.getInt("instrumentId");
            Log.d(TAG, "onCreate -> savedInstanceState != null");
        }

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            instrumentId = bundle.getInt("instrumentId");
            trackId = bundle.getInt("trackId");
            instrumentName = bundle.getString("instrumentName", Instrument.getInstrumentName(Instrument.INSTRUMENT_DEFAULT));
            Log.d(TAG, "onCreate -> bundle != null");
        }
        else
        {
            /*Default instrument*/
            instrumentId = Instrument.INSTRUMENT_DEFAULT;
            trackId = -1;
            instrumentName = Instrument.getInstrumentName(Instrument.INSTRUMENT_DEFAULT);
            Log.d(TAG, "onCreate -> bundle == null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_track, container, false);

        buttonContainer = rootView.findViewById(R.id.track_button_container);
        instrumentTextView = rootView.findViewById(R.id.instrument_name);

        setInstrumentText(instrumentName);

        for(int i=1; i<=NUMBER_OF_BUTTONS; i++)
        {
            String buttonId = String.format("button_%d", i);
            int resID = getResources().getIdentifier(buttonId, "id", getContext().getPackageName());
            Button b = rootView.findViewById(resID);
            b.setOnClickListener(this);
            buttons.add(b);
        }

        updateButtonStates();

        Log.d(TAG, "onCreateView");

        return rootView;
    }

    @Override
    public void onClick(View v)
    {
        for(int b=0; b < buttons.size(); b++)
        {
            Button button = buttons.get(b);
            if(button.getId() == v.getId())
            {
                boolean newState = !button.isActivated();
                button.setActivated(newState);
                ensureButtonStatesExist();
                buttonStates[b] = newState;
                break;
            }
        }
    }

    private void ensureButtonStatesExist()
    {
        buttonStates = (buttonStates == null)? new boolean[NUMBER_OF_BUTTONS] : buttonStates;
    }

    public void setInstrumentText(String text)
    {
        instrumentTextView.setText(text);
    }

    public String getInstrumentText()
    {
        return instrumentTextView.getText().toString();
    }

    public int getInstrumentId()
    {
        return instrumentId;
    }

    public int getTrackId()
    {
        return trackId;
    }

    public boolean[] getButtonStates()
    {
        return buttonStates;
    }

    private void updateButtonStates()
    {
        if(buttonStates != null)
        {
            for(int i=0; i<buttons.size(); i++)
            {
                if(buttonStates[i])
                {
                    buttons.get(i).setActivated(buttonStates[i]);
                }
            }
        }
    }

    public void setButtonStates(boolean[] newStates)
    {
        buttonStates = newStates;
        updateButtonStates();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState)
    {
        outState.putInt("trackId", trackId);
        outState.putInt("instrumentId", instrumentId);
        outState.putBooleanArray("buttonStates", buttonStates);

        Log.d(TAG, "onSaveInstanceState");

        super.onSaveInstanceState(outState);
    }
}
