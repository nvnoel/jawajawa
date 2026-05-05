package com.wardrumstudios.utils;

import android.annotation.TargetApi;
import android.app.NativeActivity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.util.Log;

@TargetApi(9)
public class NDKHelper {
    private static boolean loadedSO = false;
    NativeActivity activity;

    public native void RunOnUiThreadHandler(long j);

    public NDKHelper(NativeActivity act) {
        this.activity = act;
    }

    public void loadLibrary(String soname) {
        if (!soname.isEmpty()) {
            System.loadLibrary(soname);
            loadedSO = true;
        }
    }

    public static Boolean checkSOLoaded() {
        if (loadedSO) {
            return true;
        }
        Log.e("NDKHelper", "--------------------------------------------\n.so has not been loaded. To use JUI helper, please initialize with \nNDKHelper::Init( ANativeActivity* activity, const char* helper_class_name, const char* native_soname);\n--------------------------------------------\n");
        return false;
    }

    private int nextPOT(int i) {
        int pot = 1;
        while (pot < i) {
            pot <<= 1;
        }
        return pot;
    }

    private Bitmap scaleBitmap(Bitmap bitmapToScale, float newWidth, float newHeight) {
        if (bitmapToScale == null) {
            return null;
        }
        int width = bitmapToScale.getWidth();
        int height = bitmapToScale.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(newWidth / width, newHeight / height);
        return Bitmap.createBitmap(bitmapToScale, 0, 0, bitmapToScale.getWidth(), bitmapToScale.getHeight(), matrix, true);
    }

    /*  JADX ERROR: ArrayIndexOutOfBoundsException in pass: ۥ۟ۡۤ۟
        java.lang.ArrayIndexOutOfBoundsException: length=2; index=110
        */
    public boolean loadTexture(java.lang.String r9) { return false;
        /*
            r8 = this;
            r4 = 0
            r0 = 0
            r3 = r9
            java.lang.String r5 = "/"
            boolean r5 = r9.startsWith(r5)     // Catch: java.lang.Exception -> L55
            if (r5 != 0) goto L1e
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch: java.lang.Exception -> L55
            r5.<init>()     // Catch: java.lang.Exception -> L55
            java.lang.String r6 = "/"
            java.lang.StringBuilder r5 = r5.append(r6)     // Catch: java.lang.Exception -> L55
            java.lang.StringBuilder r5 = r5.append(r9)     // Catch: java.lang.Exception -> L55
            java.lang.String r3 = r5.toString()     // Catch: java.lang.Exception -> L55
        L1e:
            java.io.File r2 = new java.io.File     // Catch: java.lang.Exception -> L55
            android.app.NativeActivity r5 = r8.activity     // Catch: java.lang.Exception -> L55
            r6 = 0
            java.io.File r5 = r5.getExternalFilesDir(r6)     // Catch: java.lang.Exception -> L55
            r2.<init>(r5, r3)     // Catch: java.lang.Exception -> L55
            boolean r5 = r2.canRead()     // Catch: java.lang.Exception -> L55
            if (r5 == 0) goto L42
            java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch: java.lang.Exception -> L55
            r5.<init>(r2)     // Catch: java.lang.Exception -> L55
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeStream(r5)     // Catch: java.lang.Exception -> L55
        L39:
            if (r0 == 0) goto L40
            r5 = 3553(0xde1, float:4.979E-42)
            android.opengl.GLUtils.texImage2D(r5, r4, r0, r4)
        L40:
            r4 = 1
        L41:
            return r4
        L42:
            android.app.NativeActivity r5 = r8.activity     // Catch: java.lang.Exception -> L55
            android.content.res.Resources r5 = r5.getResources()     // Catch: java.lang.Exception -> L55
            android.content.res.AssetManager r5 = r5.getAssets()     // Catch: java.lang.Exception -> L55
            java.io.InputStream r5 = r5.open(r9)     // Catch: java.lang.Exception -> L55
            android.graphics.Bitmap r0 = android.graphics.BitmapFactory.decodeStream(r5)     // Catch: java.lang.Exception -> L55
            goto L39
        L55:
            r1 = move-exception
            java.lang.String r5 = "NDKHelper"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Coundn't load a file:"
            java.lang.StringBuilder r6 = r6.append(r7)
            java.lang.StringBuilder r6 = r6.append(r9)
            java.lang.String r6 = r6.toString()
            android.util.Log.w(r5, r6)
            goto L41
        */
        // throw new UnsupportedOperationException("Method not decompiled: com.wardrumstudios.utils.NDKHelper.loadTexture(java.lang.String):boolean");
    }

    public Bitmap openBitmap(String path, boolean iScalePOT) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(this.activity.getResources().getAssets().open(path));
            if (iScalePOT) {
                int originalWidth = getBitmapWidth(bitmap);
                int originalHeight = getBitmapHeight(bitmap);
                int width = nextPOT(originalWidth);
                int height = nextPOT(originalHeight);
                if (originalWidth != width || originalHeight != height) {
                    return scaleBitmap(bitmap, width, height);
                }
                return bitmap;
            }
            return bitmap;
        } catch (Exception e) {
            Log.w("NDKHelper", "Coundn't load a file:" + path);
            return null;
        }
    }

    public int getBitmapWidth(Bitmap bmp) {
        return bmp.getWidth();
    }

    public int getBitmapHeight(Bitmap bmp) {
        return bmp.getHeight();
    }

    public void getBitmapPixels(Bitmap bmp, int[] pixels) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
    }

    public void closeBitmap(Bitmap bmp) {
        bmp.recycle();
    }

    public String getNativeLibraryDirectory(Context appContext) {
        ApplicationInfo ai = this.activity.getApplicationInfo();
        Log.w("NDKHelper", "ai.nativeLibraryDir:" + ai.nativeLibraryDir);
        return ((ai.flags & 128) != 0 || (ai.flags & 1) == 0) ? ai.nativeLibraryDir : "/system/lib/";
    }

    public String getApplicationName() {
        ApplicationInfo ai;
        PackageManager pm = this.activity.getPackageManager();
        try {
            ai = pm.getApplicationInfo(this.activity.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            ai = null;
        }
        String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        return applicationName;
    }

    @TargetApi(17)
    public int getNativeAudioBufferSize() {
        int SDK_INT = Build.VERSION.SDK_INT;
        if (SDK_INT >= 17) {
            AudioManager am = (AudioManager) this.activity.getSystemService("audio");
            String framesPerBuffer = am.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER");
            return Integer.parseInt(framesPerBuffer);
        }
        return 0;
    }

    public int getNativeAudioSampleRate() {
        return AudioTrack.getNativeOutputSampleRate(1);
    }

    public void runOnUIThread(final long p) {
        if (checkSOLoaded().booleanValue()) {
            this.activity.runOnUiThread(new Runnable() { // from class: com.wardrumstudios.utils.NDKHelper.1
                @Override // java.lang.Runnable
                public void run() {
                    NDKHelper.this.RunOnUiThreadHandler(p);
                }
            });
        }
    }
}