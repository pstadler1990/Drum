package de.pstadler.drum.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpDownloader
{
	public static final int BUFFER_SIZE = 1024;

	/*Some parts (modified) taken from Android Programming (Phillips et. al)*/
	public static byte[] getBytesFromUrl(String urlString) throws IOException
	{
		URL url = new URL(urlString);
		HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

		try
		{
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			InputStream inputStream = httpURLConnection.getInputStream();
			if(httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK)
			{
				throw new IOException(httpURLConnection.getResponseMessage());
			}
			int bytesRead = 0;
			byte[] buf = new byte[BUFFER_SIZE];
			while ((bytesRead = inputStream.read(buf)) > 0)
			{
				outputStream.write(buf, 0, bytesRead);
			}
			outputStream.close();
			return outputStream.toByteArray();
		}
		finally
		{
			httpURLConnection.disconnect();
		}
	}

	private static String getStringFromUrl(String urlString) throws IOException
	{
		return new String(getBytesFromUrl(urlString));
	}

	public static JSONArray downloadJSONFromUrl(String urlString)
	{
		try
		{
			String jsonString = getStringFromUrl(urlString);
			JSONArray jsonArray = new JSONArray(jsonString);
			return jsonArray;
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/*TODO: Add HttpDownloadSample class for downloading the music files from a kit */


}
