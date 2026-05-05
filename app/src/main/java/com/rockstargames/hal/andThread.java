package com.rockstargames.hal;

import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Iterator;

public class andThread {
    private static boolean threadsRunning = true;
    private static ArrayList<DelayedNativeRunnable> pausedCache = new ArrayList<>();
    private static int tid = 0;

    /* JADX INFO: Access modifiers changed from: private */
    public static native void runNativeRunnable(int i);

    private static class DelayedNativeRunnable implements Runnable {
        private int delay;
        private int runnableId;

        public DelayedNativeRunnable(int id, int millisDelay) {
            this.runnableId = id;
            this.delay = millisDelay;
        }

        @Override // java.lang.Runnable
        public void run() {
            andThread.runNativeRunnable(this.runnableId);
        }

        public boolean post() {
            ViewGroup vg = ActivityWrapper.getLayout();
            if (vg != null) {
                vg.postDelayed(this, this.delay);
                return true;
            }
            return false;
        }
    }

    public static void setRunning(boolean running) {
        threadsRunning = running;
        if (running) {
            synchronized (pausedCache) {
                Iterator<DelayedNativeRunnable> it = pausedCache.iterator();
                while (it.hasNext()) {
                    DelayedNativeRunnable dnr = it.next();
                    dnr.post();
                }
                pausedCache.clear();
            }
        }
    }

    public static void runOnMainThread(int runnableId, int delay) {
        if (delay <= 0) {
            delay = 1;
        }
        DelayedNativeRunnable dnr = new DelayedNativeRunnable(runnableId, delay);
        if (threadsRunning) {
            dnr.post();
            return;
        }
        synchronized (pausedCache) {
            pausedCache.add(dnr);
        }
    }

    public static void runOnBackgroundThread(final int runnableId, final int delay) {
        new Thread(new Runnable() { // from class: com.rockstargames.hal.andThread.1
            @Override // java.lang.Runnable
            public void run() {
                if (delay > 0) {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ex) {
                        Log.w("HAL", "Interrupted sleep:", ex);
                        return;
                    }
                }
                andThread.runNativeRunnable(runnableId);
            }
        }, "Native background thread").start();
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
        }
    }

    public static int currentThreadId() {
        tid++;
        if (tid == 0) {
            ActivityWrapper.handleException(new Exception("Thread IDs overflowed!"));
            tid++;
        }
        if (ActivityWrapper.getActivity().getMainLooper() == Looper.myLooper()) {
            return 0;
        }
        return tid;
    }
}