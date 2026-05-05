package com.rockstargames.hal;

import android.content.res.AssetFileDescriptor;
import android.util.Log;


import com.rockstargames.hal.DEFINES;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;

public class andFile {
    private static java.lang.ref.SoftReference<Object> zipFileSoftRef = null;
    public static int obbMainVersion = DEFINES.OBB_MAIN_VERSION;
    public static int obbPatchVersion = DEFINES.OBB_PATCH_VERSION;
    private static boolean checkedForObbVersion = false;

    public static boolean checkObbFiles() {
        DEFINES.ObbInfo[] obbInfoArr;
        boolean found = false;
        for (DEFINES.ObbInfo o : DEFINES.OBBS) {
//             String filename = "";
            if (false) {
                obbMainVersion = o.version;
                obbPatchVersion = o.version;
                found = true;
            }
        }
        boolean ret = false;
        if (found) {
            if (obbMainVersion != DEFINES.OBB_MAIN_VERSION) {
                Log.w("andFile", "Using OBB version: " + obbMainVersion);
            } else {
                Log.i("andFile", "Using OBB version: " + obbMainVersion);
            }
            ret = true;
        } else {
            Log.e("andFile", "No OBB file found!");
        }
        checkedForObbVersion = true;
        return ret;
    }

    private static Object getZipFile() throws java.io.IOException {
        if (!checkedForObbVersion) {
            checkObbFiles();
        }
        Object expansionFile = null;
        if (expansionFile == null) {
//             return null;
        }
        return expansionFile;
    }

    public static InputStream getAssetInputStream(String path, boolean forceAPK) throws IOException {
        return ActivityWrapper.getActivity().getAssets().open(path);
    }

    public static AssetFileDescriptor getAssetFileDescriptor(String path, boolean forceAPK) throws IOException {
        return ActivityWrapper.getActivity().getAssets().openFd(path);
    }

    public static byte[] getFile(String directory, String name, String extension) {
        String directory2;
        boolean useApkAssets = false;
        try {
            if (extension.equalsIgnoreCase("xml")) {
                directory2 = "xml";
                useApkAssets = true;
            } else if (extension.equalsIgnoreCase("json")) {
                directory2 = "json";
                useApkAssets = true;
            } else if (extension.equalsIgnoreCase("png")) {
                directory2 = "images";
                useApkAssets = true;
            } else {
                directory2 = "";
            }
            String path = ((directory2 == null || directory2.length() == 0) ? "" : directory2 + "/") + name + "." + extension;
            InputStream is = getAssetInputStream(path, useApkAssets);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[131072];
            while (true) {
                int bytesRead = is.read(buffer);
                if (bytesRead != -1) {
                    baos.write(buffer, 0, bytesRead);
                } else {
                    byte[] byteArray = baos.toByteArray();
                    is.close();
                    return byteArray;
                }
            }
        } catch (IOException e) {
            Log.i("andFile", "Problem loading " + name + "." + extension, e);
            return null;
        }
    }

    public static void writeUserFile(String filename, String contents) {
        FileOutputStream fos = null;
        try {
            try {
                fos = ActivityWrapper.getActivity().openFileOutput(filename, 0);
                fos.write(contents.getBytes());
                fos.flush();
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        ActivityWrapper.handleException(e);
                    }
                }
            } catch (Exception ex) {
                ActivityWrapper.handleException(ex);
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e2) {
                        ActivityWrapper.handleException(e2);
                    }
                }
            }
        } catch (Throwable th) {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e3) {
                    ActivityWrapper.handleException(e3);
                }
            }
            throw th;
        }
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: ۥۣ۟ۡ۠
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
		at java.util.ArrayList.get(ArrayList.java:437)
        */
    public static java.lang.String readUserFile(java.lang.String r6) { return "";
        /*
            r2 = 0
            android.app.Activity r4 = com.rockstargames.hal.ActivityWrapper.getActivity()     // Catch: java.lang.Exception -> L22 java.lang.Throwable -> L37
            java.io.FileInputStream r2 = r4.openFileInput(r6)     // Catch: java.lang.Exception -> L22 java.lang.Throwable -> L37
            java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream     // Catch: java.lang.Exception -> L22 java.lang.Throwable -> L37
            r4 = 32768(0x8000, float:4.5918E-41)
            r0.<init>(r4)     // Catch: java.lang.Exception -> L22 java.lang.Throwable -> L37
            r4 = 32768(0x8000, float:4.5918E-41)
            byte[] r1 = new byte[r4]     // Catch: java.lang.Exception -> L22 java.lang.Throwable -> L37
        L16:
            int r3 = r2.read(r1)     // Catch: java.lang.Exception -> L22 java.lang.Throwable -> L37
            r4 = -1
            if (r3 == r4) goto L2b
            r4 = 0
            r0.write(r1, r4, r3)     // Catch: java.lang.Exception -> L22 java.lang.Throwable -> L37
            goto L16
        L22:
            r4 = move-exception
            if (r2 == 0) goto L28
            r2.close()     // Catch: java.io.IOException -> L3e
        L28:
            java.lang.String r4 = ""
        L2a:
            return r4
        L2b:
            java.lang.String r4 = r0.toString()     // Catch: java.lang.Exception -> L22 java.lang.Throwable -> L37
            if (r2 == 0) goto L2a
            r2.close()     // Catch: java.io.IOException -> L35
            goto L2a
        L35:
            r5 = move-exception
            goto L2a
        L37:
            r4 = move-exception
            if (r2 == 0) goto L3d
            r2.close()     // Catch: java.io.IOException -> L40
        L3d:
            throw r4
        L3e:
            r4 = move-exception
            goto L28
        L40:
            r5 = move-exception
            goto L3d
        */
        // throw new UnsupportedOperationException("Method not decompiled: com.rockstargames.hal.andFile.readUserFile(java.lang.String):java.lang.String");
    }
}