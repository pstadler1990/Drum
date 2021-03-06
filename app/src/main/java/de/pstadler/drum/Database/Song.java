package de.pstadler.drum.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;


@Entity
public class Song implements Parcelable
{
	@NonNull
	@PrimaryKey
	public String name;

	public int tracks;

	public int bars;

	public int bpm;

	/* Store the track information:
	   sounds[] contains a sound object for each track 0..n
	   playbackArrays[] contains a boolean array for each track 0..n
	   Each sound and playback array is later mapped to a newly created page / bar and track */
	public Sound[] sounds;

	public List<String> playbackStrings;


	public Song(@NonNull String name)
	{
		this.name = name;
	}

	protected Song(Parcel in)
	{
		name = in.readString();
		tracks = in.readInt();
		bars = in.readInt();
		bpm = in.readInt();
		sounds = in.createTypedArray(Sound.CREATOR);
		playbackStrings = new ArrayList<>();
		in.readList(playbackStrings, null);
	}

	public static final Creator<Song> CREATOR = new Creator<Song>()
	{
		@Override
		public Song createFromParcel(Parcel in)
		{
			return new Song(in);
		}

		@Override
		public Song[] newArray(int size)
		{
			return new Song[size];
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
		dest.writeInt(tracks);
		dest.writeInt(bars);
		dest.writeInt(bpm);
		dest.writeTypedArray(sounds, 0);
		dest.writeList(playbackStrings);
	}
}
