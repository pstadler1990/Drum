package de.pstadler.drum.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkHelper
{
	private Context context;

	public NetworkHelper(Context context)
	{
		this.context = context;
	}

	/* Source: https://stackoverflow.com/a/4141610/1794026 */
	public boolean isNetworkAvailable()
	{
		if(context == null) return false;

		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
