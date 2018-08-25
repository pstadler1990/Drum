package de.pstadler.drum.http;

import java.util.List;

public interface IDownloadListener
{
	void onProgress(int p);
	void onDownloadComplete(List<?> result);
}
