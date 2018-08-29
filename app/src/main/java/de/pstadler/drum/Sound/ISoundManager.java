package de.pstadler.drum.Sound;

public interface ISoundManager
{
	void onSoundkitRefresh(Soundkit...soundkit);
	void onSoundkitAdd(Soundkit...soundkit);
}
