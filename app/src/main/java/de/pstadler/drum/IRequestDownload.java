package de.pstadler.drum;

import de.pstadler.drum.http.DownloadSound;

@SuppressWarnings("unchecked")
public interface IRequestDownload<T>
{
	void requestDownload(T...sounds);
}
