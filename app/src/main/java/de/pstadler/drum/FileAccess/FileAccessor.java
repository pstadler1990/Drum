package de.pstadler.drum.FileAccess;

import android.content.Context;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import de.pstadler.drum.R;
import static android.content.Context.MODE_PRIVATE;


public class FileAccessor
{
	private static final int BUFFER_SIZE = 1024;

	public static String writeFileToDisk(Context context, String directory, String fileNameWithExt, byte[] bytes)
	{
		FileOutputStream outputStream;

		if(context != null)
		{
			File file = new File(context.getDir(directory, MODE_PRIVATE), fileNameWithExt);
			file.setWritable(true);
			file.setReadable(true);

			try
			{
				outputStream = new FileOutputStream(file);
				outputStream.write(bytes);
				outputStream.close();
				return file.getAbsolutePath();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	public static File[] getFilesInDirectory(Context context, String directory)
	{
		File filesDir = context.getDir(directory, MODE_PRIVATE);
		return filesDir.listFiles();
	}

	public static byte[] readFileFromDisk(String absolutePath)
	{
		byte[] buf = new byte[BUFFER_SIZE];

		File file = new File(absolutePath);
		try
		{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			InputStream inputStream = new FileInputStream(file);
			int bytesRead;

			while ((bytesRead = inputStream.read(buf)) > 0)
			{
				outputStream.write(buf, 0, bytesRead);
			}
			inputStream.close();

			return outputStream.toByteArray();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static byte[] readFileFromDisk(String directory, String fileNameWithExt)
	{
		String absolutePath = new File(directory, fileNameWithExt).getAbsolutePath();
		return readFileFromDisk(absolutePath);
	}

	public static boolean deleteFileFromDisk(String absolutePath)
	{
		File file = new File(absolutePath);
		return file.delete();
	}

	public static void deleteDirectoryRecursivelyFromDisk(Context context, String directory)
	{
		File[] files = getFilesInDirectory(context, directory);
		for(File file : files)
		{
			file.delete();
		}
	}

	/* Convert the sound name to a standardized file name => <sound_name>.wav */
	public static String getWavFilename(Context context, String fileNameWithoutExt)
	{
		return context.getString(R.string.res_sound_name_template, fileNameWithoutExt);
	}

}
