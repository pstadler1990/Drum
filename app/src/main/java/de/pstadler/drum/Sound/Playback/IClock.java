package de.pstadler.drum.Sound.Playback;

public interface IClock
{
	void onClockUpdate(int barId, int stepId);
	void onStartPlayback();
	void onStopPlayback();
}
