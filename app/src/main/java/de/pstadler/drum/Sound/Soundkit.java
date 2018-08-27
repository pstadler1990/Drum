package de.pstadler.drum.Sound;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import de.pstadler.drum.Database.Sound;
import de.pstadler.drum.http.DownloadSound;


public class Soundkit implements Parcelable
{
	public String name;
	public Bitmap icon;
	public ArrayList<Sound> sounds;
	public ArrayList<DownloadSound> downloadSounds;
	public int elements;

	public Soundkit()
	{
	}

	protected Soundkit(Parcel in)
	{
		name = in.readString();
		icon = in.readParcelable(Bitmap.class.getClassLoader());
		sounds = in.createTypedArrayList(Sound.CREATOR);
	}

	public static final Creator<Soundkit> CREATOR = new Creator<Soundkit>()
	{
		@Override
		public Soundkit createFromParcel(Parcel in)
		{
			return new Soundkit(in);
		}

		@Override
		public Soundkit[] newArray(int size)
		{
			return new Soundkit[size];
		}
	};

	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(name);
		dest.writeParcelable(icon, flags);
		dest.writeTypedList(sounds);
	}
}
