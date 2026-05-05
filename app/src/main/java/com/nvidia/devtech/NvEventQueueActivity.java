package com.nvidia.devtech;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL11;

public abstract class NvEventQueueActivity extends Activity implements SensorEventListener {
    private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
    private static final int EGL_OPENGL_ES2_BIT = 4;
    private static int EGL_OPENGL_ES3_BIT = 64;
    private static final int EGL_RENDERABLE_TYPE = 12352;
    protected SurfaceHolder holder;
    protected SurfaceHolder movieTextHolder;
    protected SurfaceView movieTextView;
    SharedPreferences prefs;
    public View splashView;
    protected SurfaceHolder vidHolder;
    protected SurfaceView vidView;
    protected SurfaceView view;
    public boolean isNativeApp = false;
    public Handler handler = null;
    public boolean paused = false;
    protected boolean wantsMultitouch = false;
    protected boolean supportPauseResume = true;
    protected boolean GetGLExtensions = false;
    protected boolean isFailedError = false;
    protected boolean delayInputForStore = false;
    protected long lastResumeTime = 0;
    private boolean inputPaused = false;
    private AssetManager assetMgr = null;
    int SwapBufferSkip = 0;
    protected boolean IsShowingKeyboard = false;
    boolean capsLockOn = false;
    protected boolean isShieldTV = false;
    protected int maxDisplayWidth = 1920;
    protected int maxDisplayHeight = 1080;
    public boolean delaySetContentView = false;
    boolean Use2Touches = true;
    protected String movieText = "Loading...";
    int vidViewWidth = 1024;
    int vidViewHeight = 600;
    int mVideoWidth = 640;
    int mVideoHeight = 480;
    boolean InVideview = false;
    ViewGroup.LayoutParams myLayout = new ViewGroup.LayoutParams(-2, -2);
    protected boolean wantsAccelerometer = false;
    protected SensorManager mSensorManager = null;
    protected int mSensorDelay = 1;
    protected Display display = null;
    EGL10 egl = null;
    protected GL11 gl = null;
    private boolean ranInit = false;
    protected EGLSurface eglSurface = null;
    protected EGLDisplay eglDisplay = null;
    protected EGLContext eglContext = null;
    protected EGLConfig eglConfig = null;
    protected SurfaceView warView = null;
    protected boolean vidViewCreated = false;
    protected boolean vidViewIsActive = false;
    protected boolean viewIsActive = false;
    protected boolean movieIsStopping = false;
    protected boolean noVidSurface = true;
    protected boolean movieIsStarting = false;
    protected boolean creatingMediaplayer = false;
    protected boolean movieTextViewCreated = false;
    protected boolean movieTextViewIsActive = false;
    protected boolean movieTextViewFirstDestroy = false;
    public String glExtensions = null;
    protected String glVendor = null;
    protected String glRenderer = null;
    protected String glVersion = null;
    public SurfaceHolder cachedSurfaceHolder = null;
    protected int surfaceWidth = 0;
    protected int surfaceHeight = 0;
    protected boolean ResumeEventDone = false;
    protected boolean UseSubtitles = false;
    public boolean HasGLExtensions = false;
    boolean waitingForResume = false;
    protected int redSize = 5;
    protected int greenSize = 6;
    protected int blueSize = 5;
    protected int alphaSize = 0;
    protected int stencilSize = 0;
    protected int[] configAttrs = null;
    protected int[] contextAttrs = null;

    public native boolean accelerometerEvent(float f, float f2, float f3);

    public native void cleanup();

    public native void imeClosed();

    public native boolean init(boolean z);

    public native void jniNvAPKInit(Object obj);

    public native boolean keyEvent(int i, int i2, int i3, int i4, KeyEvent keyEvent);

    public native void lowMemoryEvent();

    public native boolean multiTouchEvent(int i, int i2, int i3, int i4, int i5, int i6, MotionEvent motionEvent);

    public native boolean multiTouchEvent4(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, MotionEvent motionEvent);

    public native void pauseEvent();

    public native void quitAndWait();

    public native void resumeEvent();

    public native void setWindowSize(int i, int i2);

    public native boolean touchEvent(int i, int i2, int i3, MotionEvent motionEvent);

    /* JADX INFO: Access modifiers changed from: protected */
    public class gSurfaceView extends SurfaceView {
        NvEventQueueActivity myActivity;

        public gSurfaceView(Context context) {
            super(context);
            this.myActivity = null;
        }

        @Override // android.view.View
        public boolean onKeyPreIme(int keyCode, KeyEvent event) {
            if (event.getAction() == 0 && keyCode == 4 && NvEventQueueActivity.this.IsShowingKeyboard) {
                this.myActivity.imeClosed();
                NvEventQueueActivity.this.IsShowingKeyboard = false;
            }
            return false;
        }
    }

    public View GetMainView() {
        return this.view;
    }

    public void nativeCrashed() {
        System.err.println("nativeCrashed");
        if (this.prefs != null) {
            try {
                System.err.println("saved game was:\n" + this.prefs.getString("savedGame", ""));
            } catch (Exception e) {
            }
        }
        new RuntimeException("crashed here (native trace should follow after the Java trace)").printStackTrace();
    }

    public class RawData {
        public byte[] data;
        public int length;

        public RawData() {
        }
    }

    public class RawTexture extends RawData {
        public int height;
        public int width;

        public RawTexture() {
            super();
        }
    }

    public RawData loadFile(String filename) {
        InputStream is = null;
        RawData ret = new RawData();
        try {
            try {
                is = new FileInputStream("/data/" + filename);
            } catch (Exception e) {
                try {
                    is = getAssets().open(filename);
                } catch (Exception e2) {
                }
            }
            try {
                int size = is.available();
                ret.length = size;
                ret.data = new byte[size];
                is.read(ret.data);
            } catch (IOException e3) {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e4) {
                    }
                }
            }
            return ret;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e5) {
                }
            }
        }
    }

    public RawTexture loadTexture(String filename) {
        RawTexture ret = new RawTexture();
        InputStream is = null;
        try {
            InputStream is2 = new FileInputStream("/data/" + filename);
            is = is2;
        } catch (Exception e) {
            try {
                is = getAssets().open(filename);
            } catch (Exception e2) {
            }
        }
        try {
            Bitmap bmp = BitmapFactory.decodeStream(is);
            ret.width = bmp.getWidth();
            ret.height = bmp.getHeight();
            int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
            bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
            int[] tmp = new int[bmp.getWidth()];
            int w = bmp.getWidth();
            int h = bmp.getHeight();
            for (int i = 0; i < (h >> 1); i++) {
                System.arraycopy(pixels, i * w, tmp, 0, w);
                System.arraycopy(pixels, ((h - 1) - i) * w, pixels, i * w, w);
                System.arraycopy(tmp, 0, pixels, ((h - 1) - i) * w, w);
            }
            ret.length = pixels.length * 4;
            ret.data = new byte[ret.length];
            int pos = 0;
            int bpos = 0;
            int y = 0;
            while (y < h) {
                int x = 0;
                int bpos2 = bpos;
                while (x < w) {
                    int p = pixels[pos];
                    int bpos3 = bpos2 + 1;
                    ret.data[bpos2] = (byte) ((p >> 16) & 255);
                    int bpos4 = bpos3 + 1;
                    ret.data[bpos3] = (byte) ((p >> 8) & 255);
                    int bpos5 = bpos4 + 1;
                    ret.data[bpos4] = (byte) ((p >> 0) & 255);
                    bpos2 = bpos5 + 1;
                    ret.data[bpos5] = (byte) ((p >> 24) & 255);
                    x++;
                    pos++;
                }
                y++;
                bpos = bpos2;
            }
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return ret;
    }

    public boolean IsPortrait() {
        return false;
    }

    public void setGameWindowSize(int w, int h) {
        if ((IsPortrait() && w > h) || (!IsPortrait() && h > w)) {
            setWindowSize(h, w);
        } else {
            setWindowSize(w, h);
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        System.out.println("**** NvEventQueueActivity onCreate");
        NvUtil.getInstance().setActivity(this);
        super.onCreate(savedInstanceState);
        this.handler = new Handler();
        if (!this.isFailedError) {
            if (this.wantsAccelerometer && this.mSensorManager == null) {
                this.mSensorManager = (SensorManager) getSystemService("sensor");
            }

            NvAPKFile file = new NvAPKFile();
            file.is = null;
            try {
                this.assetMgr = getAssets();
                jniNvAPKInit(this.assetMgr);
            } catch (UnsatisfiedLinkError e) {
            }
            this.display = ((WindowManager) getSystemService("window")).getDefaultDisplay();
            systemInit();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onResume() {
        this.lastResumeTime = SystemClock.uptimeMillis() + 300;
        super.onResume();
        if (this.mSensorManager != null) {
            this.mSensorManager.registerListener(this, this.mSensorManager.getDefaultSensor(1), this.mSensorDelay);
        }
        this.paused = false;
        this.inputPaused = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onRestart() {
        super.onRestart();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onPause() {
        super.onPause();
        if (this.ResumeEventDone) {
            pauseEvent();
        }
        this.paused = true;
        this.inputPaused = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Activity
    public void onStop() {
        if (this.mSensorManager != null) {
            this.mSensorManager.unregisterListener(this);
        }
        super.onStop();
    }

    @Override // android.app.Activity
    public void onDestroy() {
        if (this.supportPauseResume) {
            quitAndWait();
            finish();
        }
        super.onDestroy();
        systemCleanup();
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == 1) {
            float roll = 0.0f;
            float pitch = 0.0f;
            switch (this.display.getRotation()) {
                case 0:
                    roll = -event.values[0];
                    pitch = event.values[1];
                    break;
                case 1:
                    roll = event.values[1];
                    pitch = event.values[0];
                    break;
                case 2:
                    roll = event.values[0];
                    pitch = event.values[1];
                    break;
                case 3:
                    roll = -event.values[1];
                    pitch = event.values[0];
                    break;
            }
            accelerometerEvent(roll, pitch, event.values[2]);
        }
    }

    /*  JADX ERROR: ArrayIndexOutOfBoundsException in pass: ۥ۟ۡۤ۟
        java.lang.ArrayIndexOutOfBoundsException: length=4; index=606
        */
    @Override // android.app.Activity
    public boolean onTouchEvent(android.view.MotionEvent event) { return false; }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean ret = false;
        if (keyCode == 24 || keyCode == 25) {
            return super.onKeyDown(keyCode, event);
        }
        if (keyCode == 89 || keyCode == 85 || keyCode == 90) {
            return false;
        }
        if (keyCode != 82 && keyCode != 4) {
            ret = super.onKeyDown(keyCode, event);
        }
        if (!ret) {
            ret = keyEvent(event.getAction(), keyCode, event.getUnicodeChar(), event.getMetaState(), event);
        }
        return ret;
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == 115 && Build.VERSION.SDK_INT >= 11) {
            this.capsLockOn = event.isCapsLockOn();
            keyEvent(this.capsLockOn ? 3 : 4, 115, 0, 0, event);
        }
        if (keyCode == 89 || keyCode == 85 || keyCode == 90) {
            return false;
        }
        boolean ret = super.onKeyUp(keyCode, event);
        if (!ret) {
            return keyEvent(event.getAction(), keyCode, event.getUnicodeChar(), event.getMetaState(), event);
        }
        return ret;
    }

    public void GetGLExtensions() {
        if (!this.HasGLExtensions && this.gl != null && this.cachedSurfaceHolder != null) {
            this.glVendor = this.gl.glGetString(7936);
            this.glExtensions = this.gl.glGetString(7939);
            this.glRenderer = this.gl.glGetString(7937);
            this.glVersion = this.gl.glGetString(7938);
            System.out.println("Vendor: " + this.glVendor);
            System.out.println("Extensions " + this.glExtensions);
            System.out.println("Renderer: " + this.glRenderer);
            System.out.println("glVersion: " + this.glVersion);
            if (this.glVendor != null) {
                this.HasGLExtensions = true;
            }
        }
    }

    public boolean InitEGLAndGLES2(int EGLVersion) {
        System.out.println("InitEGLAndGLES2");
        if (this.cachedSurfaceHolder == null) {
            System.out.println("InitEGLAndGLES2 failed, cachedSurfaceHolder is null");
            return false;
        }
        boolean eglInitialized = true;
        if (this.eglContext == null) {
            eglInitialized = false;
            if (EGLVersion >= 3) {
                try {
                    eglInitialized = initEGL(3, 24);
                } catch (Exception e) {
                }
                System.out.println("initEGL 3 " + eglInitialized);
            }
            if (!eglInitialized) {
                this.configAttrs = null;
                try {
                    eglInitialized = initEGL(2, GetDepthBits());
                } catch (Exception e2) {
                }
                System.out.println("initEGL 2 " + eglInitialized);
                if (!eglInitialized) {
                    eglInitialized = initEGL(2, 16);
                    System.out.println("initEGL 2 " + eglInitialized);
                }
            }
        }
        if (eglInitialized) {
            System.out.println("Should we create a surface?");
            if (!this.viewIsActive) {
                System.out.println("Yes! Calling create surface");
                createEGLSurface(this.cachedSurfaceHolder);
                System.out.println("Done creating surface");
            }
            this.viewIsActive = true;
            this.SwapBufferSkip = 1;
            return true;
        }
        System.out.println("initEGLAndGLES2 failed, core EGL init failure");
        return false;
    }

    public void GamepadReportSurfaceCreated(SurfaceHolder holder) {
    }

    public void mSleep(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
        }
    }

    public void DoResumeEvent() {
        if (!this.waitingForResume) {
            new Thread(new Runnable() { // from class: com.nvidia.devtech.NvEventQueueActivity.1
                @Override // java.lang.Runnable
                public void run() {
                    NvEventQueueActivity.this.waitingForResume = true;
                    while (NvEventQueueActivity.this.cachedSurfaceHolder == null) {
                        NvEventQueueActivity.this.mSleep(1000L);
                    }
                    NvEventQueueActivity.this.waitingForResume = false;
                    NvEventQueueActivity.this.resumeEvent();
                    NvEventQueueActivity.this.ResumeEventDone = true;
                }
            }).start();
        }
    }

    protected boolean systemInit() {
        System.out.println("In systemInit");
        if (!this.GetGLExtensions && this.supportPauseResume) {
            init(this.GetGLExtensions);
        }
        if (this.warView == null) {
            this.view = new gSurfaceView(this);
            ((gSurfaceView) this.view).myActivity = this;
        } else {
            this.view = this.warView;
        }
        this.holder = this.view.getHolder();
        this.holder.setType(2);
        this.holder.setKeepScreenOn(true);
        if (this.isShieldTV) {
            this.holder.setFixedSize(this.maxDisplayWidth, this.maxDisplayHeight);
        }
        this.holder.addCallback(new SurfaceHolder.Callback() { // from class: com.nvidia.devtech.NvEventQueueActivity.2
            @Override // android.view.SurfaceHolder.Callback
            public void surfaceCreated(SurfaceHolder holder) {
                boolean firstRun = NvEventQueueActivity.this.cachedSurfaceHolder == null;
                NvEventQueueActivity.this.cachedSurfaceHolder = holder;
                if (!firstRun && NvEventQueueActivity.this.ResumeEventDone) {
                    NvEventQueueActivity.this.resumeEvent();
                }
                NvEventQueueActivity.this.ranInit = true;
                if (NvEventQueueActivity.this.supportPauseResume || !NvEventQueueActivity.this.init(NvEventQueueActivity.this.GetGLExtensions)) {
                }
                System.out.println("surfaceCreated: w:" + NvEventQueueActivity.this.surfaceWidth + ", h:" + NvEventQueueActivity.this.surfaceHeight);
                NvEventQueueActivity.this.setGameWindowSize(NvEventQueueActivity.this.surfaceWidth, NvEventQueueActivity.this.surfaceHeight);
                if (NvEventQueueActivity.this.GetGLExtensions && NvEventQueueActivity.this.supportPauseResume && firstRun) {
                    NvEventQueueActivity.this.init(NvEventQueueActivity.this.GetGLExtensions);
                }
                if (firstRun) {
                    NvEventQueueActivity.this.GamepadReportSurfaceCreated(holder);
                }
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                System.out.println("Surface changed: " + width + ", " + height);
                NvEventQueueActivity.this.surfaceWidth = width;
                NvEventQueueActivity.this.surfaceHeight = height;
                NvEventQueueActivity.this.setGameWindowSize(NvEventQueueActivity.this.surfaceWidth, NvEventQueueActivity.this.surfaceHeight);
                NvEventQueueActivity.this.hideSystemUI();
            }

            @Override // android.view.SurfaceHolder.Callback
            public void surfaceDestroyed(SurfaceHolder holder) {
                NvEventQueueActivity.this.pauseEvent();
                NvEventQueueActivity.this.destroyEGLSurface();
                NvEventQueueActivity.this.viewIsActive = false;
            }
        });
        if (!this.delaySetContentView) {
            if (this.view.getParent() != null) {
                System.out.println("view.getParent() != null");
                setContentView((View) this.view.getParent());
            } else {
                setContentView(this.view);
            }
        }
        this.view.setFocusable(true);
        this.view.setFocusableInTouchMode(true);
        if (!this.noVidSurface) {
            this.vidView = new SurfaceView(this);
            this.vidHolder = this.vidView.getHolder();
            this.vidHolder.addCallback(new SurfaceHolder.Callback() { // from class: com.nvidia.devtech.NvEventQueueActivity.3
                @Override // android.view.SurfaceHolder.Callback
                public void surfaceCreated(SurfaceHolder holder) {
                    NvEventQueueActivity.this.vidViewIsActive = true;
                    if (!NvEventQueueActivity.this.vidViewCreated) {
                        if (NvEventQueueActivity.this.UseSubtitles) {
                            NvEventQueueActivity.this.movieTextView.setVisibility(0);
                        } else {
                            NvEventQueueActivity.this.vidView.setVisibility(4);
                        }
                        NvEventQueueActivity.this.vidViewCreated = true;
                    } else if (NvEventQueueActivity.this.UseSubtitles) {
                        NvEventQueueActivity.this.movieTextView.setVisibility(0);
                    }
                    NvEventQueueActivity.this.InVideview = false;
                }

                @Override // android.view.SurfaceHolder.Callback
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                    NvEventQueueActivity.this.vidViewWidth = width;
                    NvEventQueueActivity.this.vidViewHeight = height;
                }

                @Override // android.view.SurfaceHolder.Callback
                public void surfaceDestroyed(SurfaceHolder holder) {
                    if (NvEventQueueActivity.this.UseSubtitles) {
                        NvEventQueueActivity.this.movieTextView.setVisibility(4);
                    }
                    NvEventQueueActivity.this.vidViewIsActive = false;
                    NvEventQueueActivity.this.movieIsStopping = false;
                    NvEventQueueActivity.this.InVideview = false;
                }
            });
            this.vidHolder.setType(3);
            if (this.UseSubtitles) {
                this.movieTextView = new SurfaceView(this);
                this.movieTextHolder = this.movieTextView.getHolder();
                this.movieTextHolder.addCallback(new SurfaceHolder.Callback() { // from class: com.nvidia.devtech.NvEventQueueActivity.4
                    @Override // android.view.SurfaceHolder.Callback
                    public final void surfaceCreated(SurfaceHolder holder) {
                        System.out.println("surface2222222Created called - subView");
                        Canvas canvas = NvEventQueueActivity.this.movieTextHolder.lockCanvas();
                        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                        NvEventQueueActivity.this.movieTextHolder.unlockCanvasAndPost(canvas);
                        if (!NvEventQueueActivity.this.movieTextViewCreated) {
                            NvEventQueueActivity.this.movieTextViewCreated = true;
                        }
                        NvEventQueueActivity.this.movieTextViewIsActive = true;
                    }

                    @Override // android.view.SurfaceHolder.Callback
                    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                        System.out.println("surfaceChanged called - movieTextView");
                    }

                    @Override // android.view.SurfaceHolder.Callback
                    public void surfaceDestroyed(SurfaceHolder holder) {
                        System.out.println("surfaceDestroyed called - movieTextView");
                        NvEventQueueActivity.this.movieTextViewFirstDestroy = true;
                        NvEventQueueActivity.this.movieTextViewIsActive = false;
                    }
                });
                this.movieTextHolder.setType(0);
            }
        }
        if (!this.noVidSurface) {
            this.vidView.setZOrderOnTop(true);
            this.vidHolder.setFormat(-3);
            LinearLayout.LayoutParams myParams = new LinearLayout.LayoutParams(-2, -2);
            myParams.gravity = 17;
            addContentView(this.vidView, myParams);
            if (this.UseSubtitles) {
                this.movieTextHolder.setFormat(-3);
                addContentView(this.movieTextView, new ViewGroup.LayoutParams(-1, -1));
            }
        }
        return true;
    }

    public void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= 19 && this.view != null) {
            try {
                this.view.setSystemUiVisibility(5894);
            } catch (Exception e) {
            }
        }
    }

    public void showSystemUI() {
        if (Build.VERSION.SDK_INT >= 19 && this.view != null) {
            try {
                this.view.setSystemUiVisibility(1792);
            } catch (Exception e) {
            }
        }
    }

    public int GetDepthBits() {
        return 16;
    }

    protected boolean initEGL(int esVersion, int depthBits) {
        int i;
        int eglErr;
        if (esVersion > 2 && Build.VERSION.SDK_INT < 21) {
            return false;
        }
        if (this.configAttrs == null) {
            this.configAttrs = new int[]{12344};
        }
        int[] oldConf = this.configAttrs;
        this.configAttrs = new int[(oldConf.length + 3) - 1];
        int i2 = 0;
        while (i2 < oldConf.length - 1) {
            this.configAttrs[i2] = oldConf[i2];
            i2++;
        }
        int i3 = i2 + 1;
        this.configAttrs[i2] = EGL_RENDERABLE_TYPE;
        if (esVersion == 3) {
            i = i3 + 1;
            this.configAttrs[i3] = EGL_OPENGL_ES3_BIT;
        } else {
            i = i3 + 1;
            this.configAttrs[i3] = 4;
        }
        int i4 = i + 1;
        this.configAttrs[i] = 12344;
        this.contextAttrs = new int[]{EGL_CONTEXT_CLIENT_VERSION, esVersion, 12344};
        if (this.configAttrs == null) {
            this.configAttrs = new int[]{12344};
        }
        int[] oldConfES2 = this.configAttrs;
        this.configAttrs = new int[(oldConfES2.length + 13) - 1];
        int i5 = 0;
        while (i5 < oldConfES2.length - 1) {
            this.configAttrs[i5] = oldConfES2[i5];
            i5++;
        }
        int i6 = i5 + 1;
        this.configAttrs[i5] = 12324;
        int i7 = i6 + 1;
        this.configAttrs[i6] = this.redSize;
        int i8 = i7 + 1;
        this.configAttrs[i7] = 12323;
        int i9 = i8 + 1;
        this.configAttrs[i8] = this.greenSize;
        int i10 = i9 + 1;
        this.configAttrs[i9] = 12322;
        int i11 = i10 + 1;
        this.configAttrs[i10] = this.blueSize;
        int i12 = i11 + 1;
        this.configAttrs[i11] = 12321;
        int i13 = i12 + 1;
        this.configAttrs[i12] = this.alphaSize;
        int i14 = i13 + 1;
        this.configAttrs[i13] = 12326;
        int i15 = i14 + 1;
        this.configAttrs[i14] = this.stencilSize;
        int i16 = i15 + 1;
        this.configAttrs[i15] = 12325;
        int i17 = i16 + 1;
        this.configAttrs[i16] = depthBits;
        int i18 = i17 + 1;
        this.configAttrs[i17] = 12344;
        this.egl = (EGL10) EGLContext.getEGL();
        this.egl.eglGetError();
        this.eglDisplay = this.egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        System.out.println("eglDisplay: " + this.eglDisplay + ", err: " + this.egl.eglGetError());
        int[] version = new int[2];
        boolean ret = this.egl.eglInitialize(this.eglDisplay, version);
        System.out.println("EglInitialize returned: " + ret);
        if (!ret || (eglErr = this.egl.eglGetError()) != 12288) {
            return false;
        }
        System.out.println("eglInitialize err: " + eglErr);
        EGLConfig[] config = new EGLConfig[20];
        int[] num_configs = new int[1];
        this.egl.eglChooseConfig(this.eglDisplay, this.configAttrs, config, config.length, num_configs);
        System.out.println("eglChooseConfig err: " + this.egl.eglGetError());
        System.out.println("num_configs " + num_configs[0]);
        int score = 16777216;
        int[] val = new int[1];
        for (int i19 = 0; i19 < num_configs[0]; i19++) {
            boolean cont = true;
            int j = 0;
            while (true) {
                if (j >= ((oldConfES2.length - 1) >> 1)) {
                    break;
                }
                this.egl.eglGetConfigAttrib(this.eglDisplay, config[i19], this.configAttrs[j * 2], val);
                if ((val[0] & this.configAttrs[(j * 2) + 1]) == this.configAttrs[(j * 2) + 1]) {
                    j++;
                } else {
                    cont = false;
                    break;
                }
            }
            if (cont) {
                this.egl.eglGetConfigAttrib(this.eglDisplay, config[i19], 12324, val);
                int r = val[0];
                this.egl.eglGetConfigAttrib(this.eglDisplay, config[i19], 12323, val);
                int g = val[0];
                this.egl.eglGetConfigAttrib(this.eglDisplay, config[i19], 12322, val);
                int b = val[0];
                this.egl.eglGetConfigAttrib(this.eglDisplay, config[i19], 12321, val);
                int a = val[0];
                this.egl.eglGetConfigAttrib(this.eglDisplay, config[i19], 12325, val);
                int d = val[0];
                this.egl.eglGetConfigAttrib(this.eglDisplay, config[i19], 12326, val);
                int s = val[0];
                int currScore = ((((Math.abs(r - this.redSize) + Math.abs(g - this.greenSize)) + Math.abs(b - this.blueSize)) + Math.abs(a - this.alphaSize)) << 16) + (Math.abs(d - depthBits) << 8) + Math.abs(s - this.stencilSize);
                if (currScore < score) {
                    for (int j2 = 0; j2 < ((this.configAttrs.length - 1) >> 1); j2++) {
                        this.egl.eglGetConfigAttrib(this.eglDisplay, config[i19], this.configAttrs[j2 * 2], val);
                    }
                    score = currScore;
                    this.eglConfig = config[i19];
                }
            }
        }
        if (this.eglConfig == null) {
            this.configAttrs = null;
            return false;
        }
        this.eglContext = this.egl.eglCreateContext(this.eglDisplay, this.eglConfig, EGL10.EGL_NO_CONTEXT, this.contextAttrs);
        System.out.println("eglCreateContext: " + this.egl.eglGetError());
        this.gl = (GL11) this.eglContext.getGL();
        return true;
    }

    protected boolean createEGLSurface(SurfaceHolder surface) {
        this.eglSurface = this.egl.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, surface, null);
        System.out.println("eglSurface: " + this.eglSurface + ", err: " + this.egl.eglGetError());
        int[] sizes = new int[1];
        this.egl.eglQuerySurface(this.eglDisplay, this.eglSurface, 12375, sizes);
        this.surfaceWidth = sizes[0];
        this.egl.eglQuerySurface(this.eglDisplay, this.eglSurface, 12374, sizes);
        this.surfaceHeight = sizes[0];
        System.out.println("checking glVendor == null?");
        if (this.glVendor == null) {
            System.out.println("Making current and back");
            makeCurrent();
            unMakeCurrent();
        }
        System.out.println("Done. Making current and back");
        return true;
    }

    protected void destroyEGLSurface() {
        if (this.eglDisplay != null && this.eglSurface != null) {
            this.egl.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        }
        if (this.eglSurface != null) {
            this.egl.eglDestroySurface(this.eglDisplay, this.eglSurface);
        }
        this.eglSurface = null;
    }

    protected void cleanupEGL() {
        destroyEGLSurface();
        if (this.eglDisplay != null) {
            this.egl.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
        }
        if (this.eglContext != null) {
            this.egl.eglDestroyContext(this.eglDisplay, this.eglContext);
        }
        if (this.eglDisplay != null) {
            this.egl.eglTerminate(this.eglDisplay);
        }
        this.eglDisplay = null;
        this.eglContext = null;
        this.eglSurface = null;
        this.ranInit = false;
        this.eglConfig = null;
        this.cachedSurfaceHolder = null;
        this.surfaceWidth = 0;
        this.surfaceHeight = 0;
    }

    public boolean swapBuffers() {
        if (this.SwapBufferSkip > 0) {
            this.SwapBufferSkip--;
            System.out.println("swapBuffer wait");
            return true;
        } else if (this.eglSurface == null) {
            System.out.println("eglSurface is NULL");
            return false;
        } else if (this.egl.eglSwapBuffers(this.eglDisplay, this.eglSurface)) {
            return true;
        } else {
            System.out.println("eglSwapBufferrr: " + this.egl.eglGetError());
            return false;
        }
    }

    public boolean getSupportPauseResume() {
        return this.supportPauseResume;
    }

    public int getSurfaceWidth() {
        return this.surfaceWidth;
    }

    public int getSurfaceHeight() {
        return this.surfaceHeight;
    }

    public boolean makeCurrent() {
        if (this.eglContext == null) {
            System.out.println("eglContext is NULL");
            return false;
        } else if (this.eglSurface == null) {
            System.out.println("eglSurface is NULL");
            return false;
        } else {
            if (!this.egl.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                System.out.println("eglMakeCurrent err: " + this.egl.eglGetError());
                if (!this.egl.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext)) {
                    return false;
                }
            }
            GetGLExtensions();
            return true;
        }
    }

    public int getOrientation() {
        return this.display.getOrientation();
    }

    public boolean unMakeCurrent() {
        if (this.egl.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)) {
            return true;
        }
        System.out.println("egl(Un)MakeCurrent err: " + this.egl.eglGetError());
        return false;
    }

    protected void systemCleanup() {
        if (this.ranInit) {
            cleanup();
        }
        cleanupEGL();
    }
}