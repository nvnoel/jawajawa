package com.fastman92.main_activity_launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import java.io.File;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Functions {
    public static String oldExternalStorageDirectory;
    public static final Pattern getPathToDirectoryInsideAndroidFromAndroidDir_pattern = Pattern.compile(".*\\/Android(_unprotected)(/)(.*?)(?=/)");
    private static final Pattern getPathToSDcardFromAndroidDir_pattern = Pattern.compile(".*(?=\\/Android(_unprotected)?(?=\\/|$))");
    private static boolean bCodeOnApplicationLaunchExecuted = false;

    public static native void OnApplicationStartup_7548652();

    public static void MkDirIfDoesNotExist(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public static File AndroidDATAdirToUnprotected(File dir_protected) {
        String AndroidUnprotectedDir;
        if (dir_protected == null || (AndroidUnprotectedDir = getPathToAndroidUnprotectedDir(dir_protected.getAbsolutePath())) == null) {
            return null;
        }
        File dir_unprotected = new File(AndroidUnprotectedDir + "/data/" + Settings.AndroidDATAdirectory + "/files");
        return dir_unprotected;
    }

    public static File AndroidOBBdirToUnprotected(File dir_protected) {
        String AndroidUnprotectedDir;
        if (dir_protected == null || (AndroidUnprotectedDir = getPathToAndroidUnprotectedDir(dir_protected.getAbsolutePath())) == null) {
            return null;
        }
        File dir_unprotected = new File(AndroidUnprotectedDir + "/obb/" + Settings.AndroidOBBdirectory);
        return dir_unprotected;
    }

    public static File AndroidAssetPacksDirToUnprotected(File dir_protected) {
        String AndroidUnprotectedDir;
        if (dir_protected == null || (AndroidUnprotectedDir = getPathToAndroidUnprotectedDir(dir_protected.getAbsolutePath())) == null) {
            return null;
        }
        String AssetPacksPath = AndroidUnprotectedDir + "/data/" + Settings.AndroidOBBdirectory + "/files/assetpacks";
        File dir_unprotected = new File(AssetPacksPath);
        return dir_unprotected;
    }

    public static String getPathToAndroidUnprotectedDir(String path) {
        Matcher m = getPathToSDcardFromAndroidDir_pattern.matcher(path);
        if (m.find()) {
            String pathToSDcard = m.group();
            if (pathToSDcard.equals(oldExternalStorageDirectory)) {
                pathToSDcard = Settings.ExternalStorageDirectory;
            }
            return pathToSDcard + "/" + Settings.AndroidDirectory;
        }
        return null;
    }

    public static void ShowMessageBox(Activity activity, String msg, final DialogInterface.OnCancelListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getApplication().getApplicationInfo().loadLabel(activity.getPackageManager()).toString());
        builder.setMessage(msg);
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() { // from class: com.fastman92.main_activity_launcher.Functions$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                listener.onCancel(dialogInterface);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnCancelListener(listener);
        alertDialog.show();
    }

    /*  JADX ERROR: ArrayIndexOutOfBoundsException in pass: ۥ۟ۡۤ۟
        java.lang.ArrayIndexOutOfBoundsException: length=2; index=164
        */
    public static void CodeOnApplicationLaunch(android.content.Context r3) {
        /*
            boolean r0 = com.fastman92.main_activity_launcher.Functions.bCodeOnApplicationLaunchExecuted
            if (r0 == 0) goto L5
            return
        L5:
            if (r3 == 0) goto Lc
            java.lang.String r0 = r3.getPackageName()
            goto Le
        Lc:
            java.lang.String r0 = com.fastman92.main_activity_launcher.SettingsGenerated.packageName
        Le:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Starting application "
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.StringBuilder r1 = r1.append(r0)
            java.lang.String r2 = ", PID: "
            java.lang.StringBuilder r1 = r1.append(r2)
            int r2 = android.os.Process.myPid()
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "fastman92 app launcher"
            android.util.Log.i(r2, r1)
            r1 = 1
            com.fastman92.main_activity_launcher.Functions.bCodeOnApplicationLaunchExecuted = r1
            return
        */
        // throw new UnsupportedOperationException("Method not decompiled: com.fastman92.main_activity_launcher.Functions.CodeOnApplicationLaunch(android.content.Context):void");
    }

    static String[] getAPKExpansionFiles(Context ctx, int mainVersion, int patchVersion) {
        String s = ctx.getPackageName();
        Vector ret = new Vector();
        if (Environment.getExternalStorageState().equals("mounted")) {
            File expPath = new File(Environment.getExternalStorageDirectory().toString() + Settings.OBB_PATH_PREFIX + s);
            if (expPath.exists()) {
                if (mainVersion > 0) {
                    String s1 = expPath + File.separator + "main." + mainVersion + "." + s + ".obb";
                    if (new File(s1).isFile()) {
                        ret.add(s1);
                    }
                }
                if (patchVersion > 0) {
                    String s2 = expPath + File.separator + "patch." + mainVersion + "." + s + ".obb";
                    if (new File(s2).isFile()) {
                        ret.add(s2);
                    }
                }
            }
        }
        String[] retArray = new String[ret.size()];
        ret.toArray(retArray);
        return retArray;
    }

    static String[] getAPKExpansionFiles2(Context ctx, int mainVersion, int patchVersion) {
        return getAPKExpansionFiles(ctx, mainVersion, patchVersion);
    }
}