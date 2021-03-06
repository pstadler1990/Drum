package de.pstadler.drum;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.pstadler.drum.Database.IDBHandler;
import de.pstadler.drum.Database.Sound;
import de.pstadler.drum.Sound.ISoundSelected;
import de.pstadler.drum.Sound.Soundkit;
import de.pstadler.drum.Sound.SoundkitDownloadedFragment;
import static de.pstadler.drum.Database.DB.MESSAGE_TYPE_GET_SOUNDKITS;


public class DialogInstrument extends DialogFragment implements IChildFragment, IDBHandler, ISoundSelected
{
	private ISoundSelected soundHandler;
	private SoundkitDownloadedFragment soundkitDownloadedFragment;

	@Override
	public void onAttach(Context context)
	{
		super.onAttach(context);
		try
		{
			soundHandler = (ISoundSelected) getTargetFragment();
		}
		catch (ClassCastException e)
		{
			throw new ClassCastException("Context must implement ISoundSelected interface");
		}

	}

	@Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.dialog_instrument, container, false);

		soundkitDownloadedFragment = new SoundkitDownloadedFragment();
		soundkitDownloadedFragment.setChildHandler(this, false);
		soundkitDownloadedFragment.setSoundHandler(this);
		getChildFragmentManager().beginTransaction().add(R.id.dialog_instrument_container_kits, soundkitDownloadedFragment, "FRAGMENT_DOWNLOADED_SOUNDKITS").commit();

        return v;
    }

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onChildCreated(String tag)
	{
		if(tag.equals("FRAGMENT_DOWNLOADED_SOUNDKITS"))
		{
			((App)getActivity().getApplicationContext()).getDatabase().getSoundkits(this);
		}
	}

	@Override
	public void onSoundSelected(Sound sound)
	{
		dismiss();
		soundHandler.onSoundSelected(sound);
	}

	@Override
	public void onMessageReceived(Message message)
	{
		switch (message.what)
		{
			case MESSAGE_TYPE_GET_SOUNDKITS:
				/* Add the sounds to the SoundkitDownloadedFragment's listview
				and call notifyDatasetChanged()! */
				Soundkit[] soundkitsDownloaded = (Soundkit[]) message.getData().getSerializable("getSoundkits");
				soundkitDownloadedFragment.onSoundkitRefresh(soundkitsDownloaded);
				break;
		}
	}
}
