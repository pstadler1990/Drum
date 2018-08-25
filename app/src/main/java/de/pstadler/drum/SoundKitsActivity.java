package de.pstadler.drum;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.pstadler.drum.Sound.SoundkitsDownloadFragment;

public class SoundKitsActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sound_kits);

		SoundkitsDownloadFragment soundkitsDownloadFragment = new SoundkitsDownloadFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.soundkits_available_online_container, soundkitsDownloadFragment).commit();

	}
}
