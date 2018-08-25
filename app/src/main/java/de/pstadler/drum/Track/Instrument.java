package de.pstadler.drum.Track;

public abstract class Instrument
{
    public static final int INSTRUMENT_MIN = 0;
    public static final int INSTRUMENT_MAX = 8;

    public static final int INSTRUMENT_DEFAULT = 0;
    public static final int INSTRUMENT_KICK = 1;
    public static final int INSTRUMENT_SNARE = 2;
    public static final int INSTRUMENT_TOM = 3;
    public static final int INSTRUMENT_CLAP = 4;
    public static final int INSTRUMENT_COWBELL = 5;
    public static final int INSTRUMENT_CLOSED_HH = 6;
    public static final int INSTRUMENT_OPEN_HH = 7;
    public static final int INSTRUMENT_CYMBAL = 8;

    private static String[] instruments = new String[] {
            "empty",
            "Kick",
            "Snare",
            "Tom",
            "Clap",
            "Cowbell",
            "Closed HH",
            "Open HH",
            "Cymbal"
    };


    public static String getInstrumentName(int instrumentId)
    {
        return instruments[instrumentId];
    }

}
