package de.pstadler.drum;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class MainMenuActivity extends AppCompatActivity implements View.OnClickListener
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        Button buttonNewTrack = findViewById(R.id.button_new_track);
        buttonNewTrack.setOnClickListener(this);
        Button buttonManageKits = findViewById(R.id.button_manage_kits);
        buttonManageKits.setOnClickListener(this);
        Button buttonLoadProjects = findViewById(R.id.button_load_track);
        buttonLoadProjects.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
    	switch(v.getId())
		{
			case R.id.button_new_track:
				Intent intentNewTrack = new Intent(this, MainActivity.class);
				startActivity(intentNewTrack);
				break;
			case R.id.button_manage_kits:
				Intent intentManageKits = new Intent(this, SoundKitsActivity.class);
				startActivity(intentManageKits);
				break;
			case R.id.button_load_track:
				Intent intentProjects = new Intent(this, LoadProjectActivity.class);
				startActivity(intentProjects);
		}

    }
}
