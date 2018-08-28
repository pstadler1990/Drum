package de.pstadler.drum.http;

import java.util.List;

public interface IDownloadListener
{
	void onDownloadStart();
	void onDownloadProgress(int p);
	void onDownloadComplete(List<?> result);
	void onDownloadCanceled();
}
