package de.pstadler.drum.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;


@Entity
public class Sound implements Parcelable
{
	@PrimaryKey(autoGenerate = true)
	public int id;

	@NonNull
	public String name;

	@NonNull
	public String kitName;

	@NonNull
	public String path;


	protected Sound(Parcel in)
	{
		name = in.readString();
		kitName = in.readString();
		path = in.readString();
	}

	public Sound(@NonNull String name, @NonNull String kitName, @NonNull String path)
	{
		this.name = name;
		this.kitName = kitName;
		this.path = path;
	}

	public static final Creator<Sound> CREATOR = new Creator<Sound>()
	{
		@Override
		public Sound createFromParcel(Parcel in)
		{
			return new Sound(in);
		}

		@Override
		public Sound[] newArray(int size)
		{
			return new Sound[size];
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
		dest.writeString(kitName);
		dest.writeString(path);
	}
}
