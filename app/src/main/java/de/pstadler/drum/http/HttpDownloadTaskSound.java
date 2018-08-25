package de.pstadler.drum.http;

import android.os.AsyncTask;
import java.io.IOException;
import java.util.ArrayList;


public class HttpDownloadTaskSound extends AsyncTask<String, Integer, ArrayList<byte[]>>
{
	private IDownloadListener downloadListener;

	public HttpDownloadTaskSound(IDownloadListener downloadListener)
	{
		this.downloadListener = downloadListener;
	}

	@Override
	protected ArrayList<byte[]> doInBackground(String... urls)
	{
		ArrayList<byte[]> result = new ArrayList<>();

		int numberOfDownloads = urls.length;
		int currentNumber = 0;

		for(String url : urls)
		{
			byte[] bytesFile = new byte[HttpDownloader.BUFFER_SIZE];

			try {
				bytesFile = HttpDownloader.getBytesFromUrl(url);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			result.add(bytesFile);

			/*Publish progress*/
			int percentage = (int)((currentNumber / numberOfDownloads) * 100.0f);
			publishProgress(percentage);
		}
		return result;
	}

	@Override
	protected void onPostExecute(ArrayList<byte[]> byteFiles)
	{
		downloadListener.onDownloadComplete(byteFiles);
	}

	@Override
	protected void onProgressUpdate(Integer... values)
	{
		downloadListener.onProgress(values[0]);
	}
}
