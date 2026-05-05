package com.rockstargames.hal;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.ViewGroup;
import java.util.Locale;

public class ActivityWrapper {
    private static Context applicationContext;
    private static Object atlasCache;
    private static ActivityWrapper instance;
    private static ContainerLayout layout;
    private static String linkedAccountResult;
    private static Activity mainActivity;
    private static int windowHeight;
    private static int windowWidth;
    private static boolean transitioning = false;
    private static float scale = 1.0f;

    private native void main();

    private native void onPauseApp();

    private native void onResumeApp(String str);

    private native void onStartApp();

    /* JADX INFO: Access modifiers changed from: private */
    public native void runUpdateCallback();

    private native void setLanguage(String str, String str2);

    private native void setVersionNumber(String str);

    public native String getLocalisedString(String str);

    public native void onBackPressed();

    public native void onExitSC();

    public native void setCurrentScreenSize(int i, int i2);

    public static Activity getActivity() {
        return mainActivity;
    }

    public static ViewGroup getLayout() {
        return layout;
    }

    public static ActivityWrapper getInstance() {
        if (instance == null) {
            instance = new ActivityWrapper();
        }
        return instance;
    }

    public static Object getTextureAtlasCache() { return null; }

    public static void setApplicationContext(Context context) {
        applicationContext = context;
    }

    public static Context getApplicationContext() {
        return applicationContext;
    }

    public static void setActivity(Activity activity) {
        mainActivity = activity;
        applicationContext = activity.getApplicationContext();
        layout = new ContainerLayout(activity);
        layout.setBackgroundColor(-16777216);
        activity.setContentView(layout);
        andSecureData.Init(activity);
        if (atlasCache == null) {
            int cacheMemory = (int) ((Runtime.getRuntime().maxMemory() * 50) / 100);

            try {
                byte[] bArr = new byte[cacheMemory];
            } catch (OutOfMemoryError e) {
                Log.i("ActivityWrapper", "Running low on memory!");
            }
        }
    }

    public static void handleException(final Exception ex) {
        try {
            getActivity().runOnUiThread(new Runnable() { // from class: com.rockstargames.hal.ActivityWrapper.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        String message = "Exception: " + ex.getClass().getSimpleName();
                        Log.e("ActivityWrapper", message, ex);
                    } catch (Exception e) {
                        Log.e("ActivityWrapper", "Unable to report error as toast!", e);
                    }
                }
            });
        } catch (Exception e) {
            Log.e("ActivityWrapper", "Unable to report error as toast!", e);
        }
    }

    public static void showToast(String message, boolean longDuration) {
    }

    public static void logError(String heading, String logMessage, Exception e) {
        showToast(heading + " Exception", false);
    }

    public static boolean getTransitioning() {
        return transitioning;
    }

    public static void setTransitioning(boolean trans) {
        transitioning = trans;
    }

    public static void runMain(int width, int height) {
        windowWidth = width;
        windowHeight = height;
        Log.e("HAL", "Using window size of " + windowWidth + "x" + windowHeight);
        ActivityWrapper wrapper = getInstance();
        setupLocale();
        wrapper.setVersionNumber(getVersion());
        wrapper.setCurrentScreenSize(width, height);
        wrapper.main();
    }

    public static void setupLocale() {
        getInstance().setLanguage(Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
    }

    public static String getVersion() {
        try {
            String app_ver = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
            return app_ver;
        } catch (PackageManager.NameNotFoundException e) {
            return "nope";
        }
    }

    public static long getTotalMemoryBytes() {
        return Runtime.getRuntime().maxMemory();
    }

    public static long getUsedMemoryBytes() {
        return Runtime.getRuntime().totalMemory();
    }

    public static long getFreeMemoryBytes() {
        return Runtime.getRuntime().freeMemory();
    }

    public static String getManagedStaticCounts() {
        String debugString = "" + " " + andViewManager.getStaticCounts();
        return debugString;
    }

    public static void onStartCallback() {
        getInstance().onStartApp();
    }

    public static void onRestartCallback() {
        getInstance().onStartApp();
    }

    public static void onResumeCallback() {
        andAudio.MuteAllAudio(false);
        andVideo.Resume();
        getInstance().onResumeApp(linkedAccountResult);
        andViewManager.invalidateHierarchy();
    }

    public static void onPauseCallback() {
        andAudio.MuteAllAudio(true);
        andVideo.Suspend();
        getInstance().onPauseApp();
    }

    public static void onStopCallback() {
    }

    public static void onDestroyCallback() {
        atlasCache = null;
    }

    public static void addUpdateCallback() {
        getActivity().getWindow().getDecorView().postDelayed(new Runnable() { // from class: com.rockstargames.hal.ActivityWrapper.3
            @Override // java.lang.Runnable
            public void run() {
                new ActivityWrapper().runUpdateCallback();
            }
        }, 30L);
    }

    public static void SetLinkedAccountResult(String result) {
        linkedAccountResult = result;
    }

    public static float getScale() {
        return scale;
    }

    public static int getWindowWidth() {
        return windowWidth;
    }

    public static int getWindowHeight() {
        return windowHeight;
    }
}