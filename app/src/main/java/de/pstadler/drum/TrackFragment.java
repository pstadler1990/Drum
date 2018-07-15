package de.pstadler.drum;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
    private LinearLayout buttonContainer;
    private TextView instrumentTextView;
    private ArrayList<Button> buttons;
    private int instrumentId;
    private int trackId;
    private String instrumentName;

    public TrackFragment() { }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null)
        {
            instrumentId = bundle.getInt("instrumentId");
            trackId = bundle.getInt("trackId");
            instrumentName = bundle.getString("instrumentName", Instrument.getInstrumentName(Instrument.INSTRUMENT_DEFAULT));
        }
        else
        {
            /*Default instrument*/
            instrumentId = Instrument.INSTRUMENT_DEFAULT;
            trackId = -1;
            instrumentName = Instrument.getInstrumentName(Instrument.INSTRUMENT_DEFAULT);
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

        buttons = new ArrayList<>();

        Button b1 = rootView.findViewById(R.id.button_1);
        b1.setOnClickListener(this);
        buttons.add(b1);
        Button b2 = rootView.findViewById(R.id.button_2);
        b2.setOnClickListener(this);
        buttons.add(b2);
        Button b3 = rootView.findViewById(R.id.button_3);
        b3.setOnClickListener(this);
        buttons.add(b3);
        Button b4 = rootView.findViewById(R.id.button_4);
        b4.setOnClickListener(this);
        buttons.add(b4);
        Button b5 = rootView.findViewById(R.id.button_5);
        b5.setOnClickListener(this);
        buttons.add(b5);
        Button b6 = rootView.findViewById(R.id.button_6);
        b6.setOnClickListener(this);
        buttons.add(b6);
        Button b7 = rootView.findViewById(R.id.button_7);
        b7.setOnClickListener(this);
        buttons.add(b7);
        Button b8 = rootView.findViewById(R.id.button_8);
        b8.setOnClickListener(this);
        buttons.add(b8);

        return rootView;
    }

    @Override
    public void onClick(View v)
    {
        for(Button button : buttons)
        {
            if(button.getId() == v.getId())
            {
                boolean newState = !button.isActivated();
                button.setActivated(newState);
                break;
            }
        }
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
}
