package com.rockstargames.hal;

import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;

public class andAudioSample extends Activity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private static int idGen = 0;
    private String m_file;
    private MediaPlayer m_mediaPlayer = null;
    private String m_category = null;
    private boolean m_autoRemove = true;
    private float m_volume = 1.0f;
    private int m_loopCount = 0;
    private int id = -1;

    public andAudioSample(String file, float volume, String category, boolean autoRemove, int loopCount) {
    }

    public int getId() {
        return this.id;
    }

    public void Mute(boolean mute) {
        if (this.m_mediaPlayer != null && IsPlaying()) {
            if (mute) {
                this.m_mediaPlayer.setVolume(0.0f, 0.0f);
            } else {
                this.m_mediaPlayer.setVolume(this.m_volume, this.m_volume);
            }
        }
    }

    public String GetCategory() {
        return this.m_category;
    }

    public boolean AutoRemove() {
        return this.m_autoRemove;
    }

    public void Play() {
        if (this.m_mediaPlayer != null) {
            if (this.m_loopCount == -1) {
                this.m_mediaPlayer.setLooping(true);
            }
            if (andAudio.m_audioMuted) {
                this.m_mediaPlayer.setVolume(0.0f, 0.0f);
            } else {
                this.m_mediaPlayer.setVolume(this.m_volume, this.m_volume);
            }
            this.m_mediaPlayer.start();
        }
    }

    public void Stop() {
        if (IsPlaying()) {
            this.m_mediaPlayer.stop();
        }
        if (this.m_mediaPlayer != null) {
            this.m_mediaPlayer.release();
            this.m_mediaPlayer = null;
        }
    }

    public boolean IsPlaying() {
        try {
            if (this.m_mediaPlayer != null) {
                return this.m_mediaPlayer.isPlaying();
            }
        } catch (Exception e) {
            Log.e("andAudio", "Unable to query playing state for: " + this.m_file);
        }
        return false;
    }

    public MediaPlayer GetMediaPlayer() {
        return this.m_mediaPlayer;
    }

    @Override // android.media.MediaPlayer.OnCompletionListener
    public void onCompletion(MediaPlayer mp) {
        if (mp != null) {
            if (mp.isPlaying()) {
                mp.stop();
            }
            mp.release();
        }
    }

    @Override // android.media.MediaPlayer.OnPreparedListener
    public void onPrepared(MediaPlayer mp) {
        if (mp != null) {
            mp.start();
        }
    }

    /*  JADX ERROR: ArrayIndexOutOfBoundsException in pass: ۥ۟ۡۤ۟
        java.lang.ArrayIndexOutOfBoundsException: length=2; index=212
        */
    @Override // android.media.MediaPlayer.OnErrorListener
    public boolean onError(android.media.MediaPlayer r4, int r5, int r6) { return false;
        /*
            r3 = this;
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "ERROR: Sample '"
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = r3.m_file
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = "': "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r0 = r1.toString()
            r1 = 100
            if (r5 != r1) goto L4f
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.StringBuilder r1 = r1.append(r0)
            java.lang.String r2 = "Server Died, "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r0 = r1.toString()
        L32:
            switch(r6) {
                case -1010: goto L8b;
                case -1007: goto L77;
                case -1004: goto L63;
                case -110: goto L9f;
                default: goto L35;
            }
        L35:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.StringBuilder r1 = r1.append(r0)
            java.lang.String r2 = "Unknown error!"
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r0 = r1.toString()
        L48:
            java.lang.String r1 = "andAudio"
            android.util.Log.e(r1, r0)
            r1 = 1
            return r1
        L4f:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.StringBuilder r1 = r1.append(r0)
            java.lang.String r2 = "Unknown error, "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r0 = r1.toString()
            goto L32
        L63:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.StringBuilder r1 = r1.append(r0)
            java.lang.String r2 = "IO Error!"
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r0 = r1.toString()
            goto L48
        L77:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.StringBuilder r1 = r1.append(r0)
            java.lang.String r2 = "Malformed sample!"
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r0 = r1.toString()
            goto L48
        L8b:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.StringBuilder r1 = r1.append(r0)
            java.lang.String r2 = "Unsupported format!"
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r0 = r1.toString()
            goto L48
        L9f:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.StringBuilder r1 = r1.append(r0)
            java.lang.String r2 = "Timed out!"
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r0 = r1.toString()
            goto L48
        */
        // throw new UnsupportedOperationException("Method not decompiled: com.rockstargames.hal.andAudioSample.onError(android.media.MediaPlayer, int, int):boolean");
    }
}