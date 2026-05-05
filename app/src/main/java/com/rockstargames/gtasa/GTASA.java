package com.rockstargames.gtasa;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import com.fastman92.main_activity_launcher.SettingsGenerated;
import com.rockstargames.hal.ActivityWrapper;

import com.wardrumstudios.utils.WarDownloaderService;
import com.wardrumstudios.utils.WarMedia;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GTASA extends WarMedia {
    static boolean UseAndroidHal = true;
    private static int debugStaticCheck = 0;
    public static GTASA gtasaSelf = null;
    static String vmVersion;
    boolean UseExpansionPack = true;
    boolean IsAmazonBuild = false;
    public boolean isInSocialClub = true;
    boolean IsGermanBuild = false;
    final boolean UseADX = true;
    final String[] hapt_names = {"bounce.hapt", "bounce_light.hapt", "bump.hapt", "bump_light.hapt", "bumpy_texture.hapt", "crunchy_texture.hapt", "double_click.hapt", "double_click_light.hapt", "engine.hapt", "engine_light.hapt", "fast_pulse.hapt", "heartbeat.hapt", "long_explosion.hapt", "long_ramp_down.hapt", "long_ramp_down_light.hapt", "long_ramp_up.hapt", "long_ramp_up_light.hapt", "machine_gun.hapt", "pop.hapt", "pop_light.hapt", "short_explosion.hapt", "short_ramp_down.hapt", "short_ramp_down_light.hapt", "short_ramp_up.hapt", "short_ramp_up_light.hapt", "slow_pulse.hapt", "tick.hapt", "tick_light.hapt", "triple_click.hapt", "triple_click_light.hapt"};
    final int[] hapt_ids = {2130968576, 2130968577, 2130968578, 2130968579, 2130968580, 2130968581, 2130968582, 2130968583, 2130968584, 2130968585, 2130968586, 2130968587, 2130968588, 2130968589, 2130968590, 2130968591, 2130968592, 2130968593, 2130968594, 2130968595, 2130968596, 2130968597, 2130968598, 2130968599, 2130968600, 2130968601, 2130968602, 2130968603, 2130968604, 2130968605};

    public native void main();

    public native void setCurrentScreenSize(int i, int i2);

    static {
        vmVersion = null;
        System.out.println("**** Loading SO's");
        try {
            vmVersion = System.getProperty("java.vm.version");
            System.out.println("vmVersion " + vmVersion);
            System.loadLibrary("ImmEmulatorJ");
        } catch (ExceptionInInitializerError e) {
        } catch (UnsatisfiedLinkError e2) {
        }
        System.loadLibrary("SCAnd");
        System.loadLibrary("GTASA");
        System.loadLibrary("threadfix");
        System.loadLibrary("AML");
    }

    public void onCreate(Bundle savedInstanceState) {

        gtasaSelf = this;












        String externalDirectory = Environment.getExternalStorageDirectory() + "/GTASA/";
        String testExpansionFileName = "";
        File buildobb = new File(testExpansionFileName);
        if (!this.IsAmazonBuild && buildobb.exists()) {
            this.UseExpansionPack = false;


        }
        if (this.IsAmazonBuild) {
            this.UseExpansionPack = false;
        }


        if (this.UseExpansionPack) {



        }

        this.wantsMultitouch = true;
        this.wantsAccelerometer = true;


        if (UseAndroidHal) {
            this.delaySetContentView = true;
        }
        super.onCreate(savedInstanceState);
        SetReportPS3As360(false);
        if (!UseAndroidHal) {
            this.isInSocialClub = false;
        }
    }

    private String copyHaptsToInternalStorage(String[] haptFileName) {
        FileOutputStream fos = null;
        byte[] tempBuffer = new byte[1024];
        for (int i = 0; i < 30; i++) {
            InputStream is = getResources().openRawResource(this.hapt_ids[i]);
            try {
                try {
                    fos = openFileOutput(haptFileName[i], 0);
                    for (int curLen = is.available(); curLen > 0; curLen = is.available()) {
                        int curLen2 = is.read(tempBuffer);
                        fos.write(tempBuffer, 0, curLen2);
                    }
                    fos.close();
                    is.close();
                    try {
                        fos.close();
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (FileNotFoundException fnfe) {
                    Log.e("OSWrapper", "Haptic Files not found\n" + fnfe);
                    try {
                        fos.close();
                        is.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                } catch (IOException ioe) {
                    Log.e("OSWrapper", "IOException when loading .hapts." + ioe);
                    try {
                        fos.close();
                        is.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
            } catch (Throwable th) {
                try {
                    fos.close();
                    is.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                }
                throw th;
            }
        }
        File file = getFilesDir();
        String path = file.getAbsolutePath() + File.separator;
        return path;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4 && this.isInSocialClub) {
            ActivityWrapper.getInstance().onBackPressed();
            return true;
        } else if (this.isInSocialClub && (keyCode == 96 || keyCode == 97 || keyCode == 99 || keyCode == 100 || keyCode == 109 || keyCode == 108 || keyCode == 103 || keyCode == 102)) {
            ActivityWrapper.getInstance().onExitSC();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    public void AfterDownloadFunction() {
        try {
            if (UseAndroidHal) {
                getWindow().setFlags(1024, 1024);
                ActivityWrapper.setActivity(this);
                if (ActivityWrapper.getTransitioning()) {
                    Log.i("StartupActivity", "*** MainActivity onCreate() transitioning (" + debugStaticCheck + ") ***)");
                    ActivityWrapper.setTransitioning(false);
                } else {
                    Log.i("StartupActivity", "*** MainActivity onCreate() not transitioning (" + debugStaticCheck + ") ***)");
                }
                debugStaticCheck++;
                Display d = getWindowManager().getDefaultDisplay();
                if ((getResources().getConfiguration().orientation == 1) == (d.getWidth() < d.getHeight())) {
                    ActivityWrapper.runMain(d.getWidth(), d.getHeight());
                } else {
                    ActivityWrapper.runMain(d.getHeight(), d.getWidth());
                }

            }
            DoResumeEvent();
        } catch (Exception ex) {
            new AlertDialog.Builder(this).setTitle("Exception").setMessage(ex.getLocalizedMessage()).show();
        }
    }


    public boolean ServiceAppCommand(String cmd, String args) {
        if (cmd.equalsIgnoreCase("ForceGermanBuild")) {
            return this.IsGermanBuild;
        }
        if (cmd.equalsIgnoreCase("SetLocale")) {

            return false;
        } else if (cmd.equalsIgnoreCase("IsAmazonBuild")) {
            return this.IsAmazonBuild;
        } else {
            if (cmd.equalsIgnoreCase("Download")) {
                return false;
            }
            if (cmd.equalsIgnoreCase("IsDownloaded")) {
                try {
                    String[] part = args.split(";");
                    File f = new File("", part[0]);
                    int len = Integer.parseInt(part[1]);
                    return f.length() == ((long) len);
                } catch (Exception e) {
                    return false;
                }
            } else if (cmd.equalsIgnoreCase("ADXEvent")) {

                return false;
            } else {
                return false;
            }
        }
    }


    public int ServiceAppCommandValue(String cmd, String args) {
        if (cmd.equalsIgnoreCase("GetDownloadBytes")) {
            return 0;
        }
        if (cmd.equalsIgnoreCase("GetDownloadState")) {
            return 4;
        }
        return 0;
    }

    public void onStart() {
        super.onStart();
        if (this.isInSocialClub) {
            ActivityWrapper.onStartCallback();
            Log.i("StartupActivity", "*** MainActivity onStart() (" + debugStaticCheck + ") ***)");
            debugStaticCheck++;
        }
    }

    public void onRestart() {
        super.onRestart();
        if (this.isInSocialClub) {
            Log.i("StartupActivity", "*** MainActivity onRestart() (" + debugStaticCheck + ") ***)");
            debugStaticCheck++;
        }
    }

    public void onResume() {
        super.onResume();
        if (false) {
            ActivityWrapper.onResumeCallback();
            Log.i("StartupActivity", "*** MainActivity onResume() (" + debugStaticCheck + ") ***)");
            debugStaticCheck++;
        }
    }

    public void onPause() {
        super.onPause();
        if (this.isInSocialClub) {
            ActivityWrapper.onPauseCallback();
            Log.i("StartupActivity", "*** MainActivity onPause() (" + debugStaticCheck + ") ***)");
            debugStaticCheck++;
        }
    }

    public void onStop() {
        super.onStop();
        if (this.isInSocialClub) {

            Log.i("StartupActivity", "*** MainActivity onStop() (" + debugStaticCheck + ") ***)");
            debugStaticCheck++;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.isInSocialClub) {
            Log.i("StartupActivity", "*** MainActivity onDestroy() (" + debugStaticCheck + ") ***)");
            debugStaticCheck++;
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.isInSocialClub) {
            Log.i("StartupActivity", "*** MainActivity onConfigurationChanged() (" + debugStaticCheck + ") ***)");
            debugStaticCheck++;
            Display d = getWindowManager().getDefaultDisplay();
            if ((getResources().getConfiguration().orientation == 1) == (d.getWidth() < d.getHeight())) {
                ActivityWrapper.getInstance().setCurrentScreenSize(d.getWidth(), d.getHeight());
            } else {
                ActivityWrapper.getInstance().setCurrentScreenSize(d.getHeight(), d.getWidth());
            }
        }
    }

    @Override // com.wardrumstudios.utils.WarBilling, com.wardrumstudios.utils.WarBase, android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void EnterSocialClub() {
        runOnUiThread(new Runnable() { // from class: com.rockstargames.gtasa.GTASA.1
            @Override // java.lang.Runnable
            public void run() {
                GTASA.this.isInSocialClub = true;
                ActivityWrapper.setActivity(GTASA.gtasaSelf);
                Display d = GTASA.this.getWindowManager().getDefaultDisplay();
                if ((GTASA.this.getResources().getConfiguration().orientation == 1) == (d.getWidth() < d.getHeight())) {
                    ActivityWrapper.runMain(d.getWidth(), d.getHeight());
                } else {
                    ActivityWrapper.runMain(d.getHeight(), d.getWidth());
                }
            }
        });
    }

    public void ExitSocialClub() {
        this.isInSocialClub = false;
        if (this.view.getParent() != null) {
            setContentView((View) this.view.getParent());
        } else {
            setContentView(this.view);
        }
        setRequestedOrientation(6);
    }

    public static void staticExitSocialClub() {
        gtasaSelf.ExitSocialClub();
    }

    public static void staticEnterSocialClub() {
        gtasaSelf.EnterSocialClub();
    }


    public boolean CustomLoadFunction() {
        return false;
    }
}