package de.pstadler.drum;

import android.content.Context;
import android.app.Activity;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;


public class SoundPlayer extends Activity {
    private static MediaPlayer mediaPlayer;
    private static double frequenzMultiplier = 1.0;
    private static Timer frequenze;

    /*
    private static AudioAttributes attributes;
    private static SoundPool soundpool;
    private static int frequency;
    private static Context context;
    //private int volume;

    public Sound(){

        context = getApplicationContext(); // Todo
        attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundpool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
        playSound();

    }
    public void playSound(){

        Log.v("Soundplayer","Init Soundplayer");
        final int testSound = soundpool.load(context,R.raw.sound4,1);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                    soundpool.play(testSound, 1, 1, 1, 0, 1f);
                    Log.v("Soundplayer", "Played Sound");
            }
        },0,5000);
    }
    */
    public SoundPlayer(){
        frequenze = new Timer();
    }
    public static void setFrequenzMultiplier(double arg){
        frequenzMultiplier = arg;
    }
    public static double getFrequenzMultiplier(){
        return frequenzMultiplier;
    }
    public static void playSound(Context context) {
        Log.v("Sound", "Initializing sounds...");
        mediaPlayer = MediaPlayer.create(context, R.raw.sound4);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        PlaySound test = new PlaySound();
        test.execute();
    }

    //TODO: Change Soundfiles, current are only for test purpose

    private static class PlaySound extends AsyncTask<Long, String, Long>{
        @Override
        protected Long doInBackground(Long...arg0){
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Log.v("Sound", "Playing sound...");
                    mediaPlayer.start();
                    Log.v("Sound", "Played sound...!");
                }
            },0, (long) (3000*SoundPlayer.getFrequenzMultiplier()));
            return null;
        }

        @Override
        protected void onPostExecute(Long result) {
            //TODO: Async
        }
    }
}
