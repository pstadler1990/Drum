package de.pstadler.drum.Sound.Playback;

public class Player implements IClock
{
	static int players = 0;
	private int playerId;

	public Player()
	{
		playerId = players++;
	}

	@Override
	public void onClockUpdate(int barId, int stepId)
	{
		System.out.println("Beep (" + playerId + ") @ " + System.currentTimeMillis());
	}
}
