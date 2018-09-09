package de.pstadler.drum.http;

import android.content.Context;
import android.os.AsyncTask;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class HttpDownloadTaskSound extends AsyncTask<DownloadSound, Integer, ArrayList<DownloadSound>>
{
	private WeakReference<Context> context;
	private IDownloadListener downloadListener;

	public HttpDownloadTaskSound(WeakReference<Context> context, IDownloadListener downloadListener)
	{
		this.context = context;
		this.downloadListener = downloadListener;
	}

	@Override
	protected void onPreExecute()
	{
		NetworkHelper helper = new NetworkHelper(context.get());
		if(helper.isNetworkAvailable()) {
			downloadListener.onDownloadStart();
		}
		else {
			cancel(true);
		}
	}

	@Override
	protected void onCancelled()
	{
		downloadListener.onDownloadCanceled();
	}

	@Override
	protected ArrayList<DownloadSound> doInBackground(DownloadSound... downloadSounds)
	{
		ArrayList<DownloadSound> result = new ArrayList<>();

		int numberOfDownloads = downloadSounds.length;
		int currentNumber = 0;

		for(DownloadSound sound : downloadSounds)
		{
			byte[] bytesFile = new byte[HttpDownloader.BUFFER_SIZE];

			try {
				bytesFile = HttpDownloader.getBytesFromUrl(sound.url);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			sound.bytes = bytesFile;
			result.add(sound);

			/*Publish progress*/
			int percentage = (int)((currentNumber / numberOfDownloads) * 100.0f);
			publishProgress(percentage);
		}
		return result;
	}

	@Override
	protected void onPostExecute(ArrayList<DownloadSound> downloadedSounds)
	{
		downloadListener.onDownloadComplete(downloadedSounds);
	}

	@Override
	protected void onProgressUpdate(Integer... values)
	{
		downloadListener.onDownloadProgress(values[0]);
	}
}
