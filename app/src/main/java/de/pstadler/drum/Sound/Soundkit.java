package de.pstadler.drum.Sound;

import android.graphics.Bitmap;
import java.util.ArrayList;
import de.pstadler.drum.Database.Sound;
import de.pstadler.drum.http.DownloadSound;


public class Soundkit
{
	public String name;
	public Bitmap icon;
	public ArrayList<Sound> sounds;
	public ArrayList<DownloadSound> downloadSounds;
}
