package com.rockstargames.hal;

import android.app.Activity;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.VideoView;

public class andVideo extends Activity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, View.OnTouchListener {
    private static andVideo m_video = null;
    private static int m_videoPosition = 0;
    private String m_file;
    private RelativeLayout m_relativeLayoutView;
    private VideoView m_videoView;

    public native void VideoFinishedCB();

    public static void PlayVideoFile(String file) {
        m_video = new andVideo(file);
        m_video.Play();
    }

    public static boolean IsVideoPlaying() {
        if (m_video != null) {
            return m_video.IsPlaying();
        }
        return false;
    }

    public static void Suspend() {
        if (m_video != null) {
            m_video.Pause();
            m_videoPosition = m_video.GetPosition();
        }
    }

    public static void Resume() {
        if (m_video != null) {
            m_video.SetPosition(m_videoPosition);
            m_video.Play();
        }
    }

    public static void StopVideo() {
        if (m_video != null) {
            m_video.Stop();
        }
    }

    public andVideo(String file) {
        this.m_videoView = null;
        this.m_relativeLayoutView = null;
        this.m_file = file;
        try {
            this.m_videoView = new VideoView(ActivityWrapper.getActivity().getApplicationContext());
            this.m_videoView.setVideoURI(null);
            this.m_videoView.setOnCompletionListener(this);
            this.m_videoView.setOnErrorListener(this);
            this.m_videoView.requestFocus();
            this.m_videoView.setOnTouchListener(this);
            RelativeLayout.LayoutParams videoviewlp = new RelativeLayout.LayoutParams(-1, -1);
            this.m_relativeLayoutView = new RelativeLayout(ActivityWrapper.getActivity());
            this.m_relativeLayoutView.setBackgroundColor(-16777216);
            this.m_relativeLayoutView.setLayoutParams(videoviewlp);
            videoviewlp.addRule(14, -1);
            videoviewlp.addRule(15, -1);
            this.m_videoView.setLayoutParams(videoviewlp);
            this.m_relativeLayoutView.addView(this.m_videoView, videoviewlp);
            ActivityWrapper.getLayout().addView(this.m_relativeLayoutView);
        } catch (Exception e) {
            Log.e("andVideo", "Unable to get video file: " + file, e);
            if (this.m_videoView != null) {
                this.m_videoView = null;
            }
        }
    }

    public boolean IsPlaying() {
        try {
            if (this.m_videoView != null) {
                return this.m_videoView.isPlaying();
            }
        } catch (Exception e) {
            Log.e("andVideo", "Unable to query playing state for: " + this.m_file, e);
        }
        return false;
    }

    public void Play() {
        if (this.m_videoView != null) {
            this.m_videoView.start();
        }
    }

    public void Pause() {
        if (this.m_videoView != null) {
            this.m_videoView.pause();
        }
    }

    public void Stop() {
        if (this.m_videoView != null && this.m_relativeLayoutView != null) {
            ActivityWrapper.getLayout().removeView(this.m_relativeLayoutView);
            this.m_videoView.stopPlayback();
            this.m_videoView = null;
            this.m_relativeLayoutView = null;
        }
    }

    public int GetPosition() {
        if (this.m_videoView != null) {
            return this.m_videoView.getCurrentPosition();
        }
        return 0;
    }

    public void SetPosition(int position) {
        if (this.m_videoView != null) {
            this.m_videoView.seekTo(position);
        }
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        VideoComplete();
        return true;
    }

    @Override // android.media.MediaPlayer.OnCompletionListener
    public void onCompletion(MediaPlayer mp) {
        VideoComplete();
    }

    private void VideoComplete() {
        if (this.m_videoView != null && this.m_relativeLayoutView != null) {
            Stop();
            VideoFinishedCB();
        }
    }

    /*  JADX ERROR: ArrayIndexOutOfBoundsException in pass: ۥ۟ۡۤ۟
        java.lang.ArrayIndexOutOfBoundsException: length=2; index=229
        */
    @Override // android.media.MediaPlayer.OnErrorListener
    public boolean onError(android.media.MediaPlayer r4, int r5, int r6) { return false;
        /*
            r3 = this;
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "ERROR: Video '"
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
            java.lang.String r1 = "andVideo"
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
            java.lang.String r2 = "Malformed video!"
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
        // throw new UnsupportedOperationException("Method not decompiled: com.rockstargames.hal.andVideo.onError(android.media.MediaPlayer, int, int):boolean");
    }
}