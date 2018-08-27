package de.pstadler.drum;

@SuppressWarnings("unchecked")
public interface IRequestDownload<T>
{
	void requestDownload(T...sounds);
}
