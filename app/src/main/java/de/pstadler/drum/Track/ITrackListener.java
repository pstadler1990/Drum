package de.pstadler.drum.Track;

import de.pstadler.drum.Database.Sound;


public interface ITrackListener
{
    void onAddTrackListener(int trackId, Sound sound);
}
