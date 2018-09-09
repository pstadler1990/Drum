package de.pstadler.drum.http;

import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class HttpDownloadTaskJSON extends AsyncTask<String, Integer, ArrayList<JSONArray>>
{
	private WeakReference<Context> context;
	private IDownloadListener downloadListener;

	public HttpDownloadTaskJSON(WeakReference<Context> context, IDownloadListener downloadListener)
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
	protected ArrayList<JSONArray> doInBackground(String... urls)
	{
		ArrayList<JSONArray> result = new ArrayList<>();

		int numberOfDownloads = urls.length;
		int currentNumber = 0;

		for(String url : urls)
		{
			JSONArray jsonArray = HttpDownloader.downloadJSONFromUrl(url);
			result.add(jsonArray);

			/*Publish progress*/
			int percentage = (int)((currentNumber / numberOfDownloads) * 100.0f);
			publishProgress(percentage);
		}
		return result;
	}

	@Override
	protected void onCancelled()
	{
		downloadListener.onDownloadCanceled();
	}

	@Override
	protected void onPostExecute(ArrayList<JSONArray> jsonObjects)
	{
		downloadListener.onDownloadComplete(jsonObjects);
	}

	@Override
	protected void onProgressUpdate(Integer... values)
	{
		downloadListener.onDownloadProgress(values[0]);
	}
}
