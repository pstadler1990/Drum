package de.pstadler.drum;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import java.util.ArrayList;


public class TrackFragment extends Fragment implements View.OnClickListener
{
    public static final String TAG = "TRACK";
    private LinearLayout buttonContainer;
    private ArrayList<Button> buttons;

    public TrackFragment()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_track, container, false);

        buttonContainer = rootView.findViewById(R.id.track_button_container);

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
                Log.v(TAG, "Pressed: " + button.getId());
                break;
            }
        }
    }
}
