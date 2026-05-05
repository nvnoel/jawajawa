package com.fastman92.main_activity_launcher;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {
    private static ComponentName activityToBeLoadedComponentName = null;
    public static final String log_tag = "fastman92 app launcher";
    private final int SETTINGS_REQUEST_CODE = 1;
    private static boolean originalActivityAlreadyStarted = false;
    public static int debugStaticIntCheck = 0;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (originalActivityAlreadyStarted) {
            GoToOriginalActivity();
            return;
        }
        try {
            ActivityInfo ai = getPackageManager().getActivityInfo(getComponentName(), 128);
            Bundle metaData = ai.metaData;
            if (metaData == null) {
                throw new Exception("Activity's metaData is null.");
            }
            String activityToLaunchStr = metaData.getString("ACTIVITY_TO_LAUNCH");
            if (activityToLaunchStr == null) {
                throw new Exception("meta-data ACTIVITY_TO_LAUNCH not found in activity.");
            }
            activityToBeLoadedComponentName = new ComponentName(getPackageName(), activityToLaunchStr);
            Intent settingsIntent = new Intent(this, SettingsLoader.class);
            startActivityForResult(settingsIntent, 1);
        } catch (Exception e) {
            HandleException(e);
        }
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 1) {
            return;
        }
        if (data == null) {
            Log.e(log_tag, "Unable to start SettingsLoader activity.");
            finish();
            return;
        }
        try {
            if (resultCode == -1) {
                Log.i(log_tag, "Launching an original activity.");
                GoToOriginalActivity();
                return;
            }
            String errorMessage = data.getStringExtra("errorMessage");
            if (errorMessage == null) {
                errorMessage = "Unknown error from settings loader";
            }
            throw new Exception(errorMessage);
        } catch (Exception e) {
            HandleException(e);
        }
    }

    void HandleException(Exception e) {
        ShowErrorMessage(e.toString());
        LauncherActivityHasGotNothingMoreToDo();
    }

    void ShowErrorMessage(String msg) {
        Log.e(log_tag, msg);
        Functions.ShowMessageBox(this, msg, new DialogInterface.OnCancelListener() { // from class: com.fastman92.main_activity_launcher.MainActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                MainActivity.this.m0lambda$ShowErrorMessage$0$comfastman92main_activity_launcherMainActivity(dialogInterface);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$ShowErrorMessage$0$com-fastman92-main_activity_launcher-MainActivity  reason: not valid java name */
    public  void m0lambda$ShowErrorMessage$0$comfastman92main_activity_launcherMainActivity(DialogInterface dialogInterface) {
        finish();
    }

    private void LauncherActivityHasGotNothingMoreToDo() {
    }

    private void GoToOriginalActivity() {
        Intent originalActivityIntent = new Intent();
        originalActivityIntent.setComponent(activityToBeLoadedComponentName);
        originalActivityIntent.addFlags(67108864);
        startActivity(originalActivityIntent);
        finish();
        originalActivityAlreadyStarted = true;
    }
}