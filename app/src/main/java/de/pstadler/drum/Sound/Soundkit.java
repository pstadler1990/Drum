package de.pstadler.drum.Sound;

import android.graphics.Bitmap;
import java.io.File;
import java.util.ArrayList;
import de.pstadler.drum.http.DownloadSound;


public class Soundkit
{
	public String name;
	public Bitmap icon;
	public ArrayList<File> files;
	public ArrayList<DownloadSound> downloadSounds;
}
