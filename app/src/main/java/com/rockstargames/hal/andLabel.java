package com.rockstargames.hal;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.util.StringTokenizer;

public class andLabel extends andView {
    private static Typeface defaultFont;
    static int staticCount = 0;
    private int cachedH;
    private int cachedW;
    private boolean hasShadow;
    private float sizeAdjusted;
    private float sizeFromHal;
    protected String text;

    enum TextAlignment {
        TEXT_ALIGNMENT_CENTER,
        TEXT_ALIGNMENT_LEFT,
        TEXT_ALIGNMENT_RIGHT,
        TEXT_ALIGNMENT_JUSTIFY
    }

    public andLabel(int handle) {
        super(handle);
        this.hasShadow = false;
        andLabelImpl l = new andLabelImpl();
        setView(l);
        staticCount++;
    }

    public andLabel(int handle, boolean init) {
        super(handle);
        this.hasShadow = false;
        if (init) {
            setView(new andLabelImpl());
        }
        staticCount++;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andLabel createView(int handle) {
        return new andLabel(handle);
    }

    protected TextView getTextView() {
        return (TextView) this.view;
    }

    public void setText(String text) {
        this.text = text;
        getTextView().setText(text);
    }

    public void setTextSize(float points) {
        this.sizeFromHal = points;
        DisplayMetrics dm = ActivityWrapper.getActivity().getResources().getDisplayMetrics();
        float finalSize = points / dm.density;
        this.sizeAdjusted = finalSize;
        getTextView().setTextSize(finalSize);
    }

    public void setBackgroundTransparent() {
        getTextView().setBackgroundColor(0);
    }

    private void printDebugFontSizeString() {
        DisplayMetrics dm = ActivityWrapper.getActivity().getResources().getDisplayMetrics();
        Log.i("andLabel", "HAL: " + this.sizeFromHal + ", Final: " + this.sizeAdjusted + ", Text: " + this.text + " dens:" + dm.density + " dpi:" + dm.densityDpi + " sd:" + dm.scaledDensity + " height:" + dm.heightPixels + " H/h:" + (this.sizeFromHal / dm.heightPixels) + " H*h:" + (this.sizeFromHal * dm.heightPixels));
    }

    public void setTextColour(int a, int r, int g, int b) {
        getTextView().setTextColor(Color.argb(a, r, g, b));
    }

    public void setAutoResize(int set) {
        setAutoResize(set == 1);
    }

    public void setAutoResize(boolean set) {
        getTextView().setHorizontallyScrolling(true);
        if (set) {
            try {
                TextView tv = getTextView();
                Paint tvp = tv.getPaint();
                Paint.FontMetrics fm = tvp.getFontMetrics();
                Paint p = new Paint();
                p.setTextSize(Math.max(tv.getTextSize(), tvp.getTextSize()));
                p.setTypeface(tv.getTypeface());
                StringTokenizer tok = new StringTokenizer(tv.getText().toString(), "\r\n");
                Point totalSize = new Point(0, 0);
                Rect tempRect = new Rect();
                while (tok.hasMoreTokens()) {
                    String t = tok.nextToken();
                    if (t.length() != 0) {
                        p.getTextBounds(t, 0, t.length(), tempRect);
                        int w = tempRect.width() + 2 + (tempRect.left > 0 ? tempRect.left : 0);
                        tempRect.height();
                        if (w > totalSize.x) {
                            totalSize.x = w;
                        }
                        totalSize.y = (int) (totalSize.y + (fm.bottom - fm.top) + fm.leading);
                    }
                }
                this.cachedW = totalSize.x;
                this.cachedH = totalSize.y;
            } catch (Exception ex) {
                Log.e("andLabel", "Autoresize exception", ex);
            }
        }
    }

    public int getCachedW() {
        return this.cachedW;
    }

    public int getCachedH() {
        return this.cachedH;
    }

    public void setFont(String directory, String filename, String fontname) {
        if (directory != null && directory.length() > 0) {
            filename = directory + "/" + filename;
        }
        Log.i("andLabel", "Attempting to load typeface:" + filename);
        try {
            getTextView().setTypeface(Typeface.createFromAsset(getTextView().getResources().getAssets(), filename));
            Log.i("andLabel", "Successfully loaded typeface (" + filename + ")");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setTextAlignment(int alignment) {
        switch (TextAlignment.values()[alignment]) {
            case TEXT_ALIGNMENT_CENTER:
                getTextView().setGravity(1);
                return;
            case TEXT_ALIGNMENT_LEFT:
                getTextView().setGravity(3);
                return;
            case TEXT_ALIGNMENT_RIGHT:
                getTextView().setGravity(5);
                return;
            case TEXT_ALIGNMENT_JUSTIFY:
                getTextView().setGravity(3);
                return;
            default:
                return;
        }
    }

    public int getWidth() {
        return getContainer().getWidth();
    }

    public int getHeight() {
        return getContainer().getHeight();
    }

    /*  JADX ERROR: ArrayIndexOutOfBoundsException in pass: ۥ۟ۡۤ۟
        java.lang.ArrayIndexOutOfBoundsException: length=2; index=254
        */
    @Override // com.rockstargames.hal.andView
    public void setBounds(float r9, float r10, float r11, float r12, float r13, float r14) {
        /*
            r8 = this;
            r7 = 0
            boolean r0 = r8.hasShadow
            r1 = 1
            if (r0 != r1) goto L8
            r7 = 1083179008(0x40900000, float:4.5)
        L8:
            float r3 = r11 + r7
            float r4 = r12 + r7
            r0 = r8
            r1 = r9
            r2 = r10
            r5 = r13
            r6 = r14
            super.setBounds(r1, r2, r3, r4, r5, r6)
            return
        */
        // throw new UnsupportedOperationException("Method not decompiled: com.rockstargames.hal.andLabel.setBounds(float, float, float, float, float, float):void");
    }

    public void setDropShadow() {
        this.hasShadow = true;
        getTextView().setShadowLayer(3.0f, 1.5f, 1.5f, -16777216);
    }

    public int getTextHeight() {
        int widthSpec = View.MeasureSpec.makeMeasureSpec((int) this.width, 1073741824);
        getTextView().measure(widthSpec, 0);
        return getTextView().getMeasuredHeight();
    }

    public int getTextWidth() {
        int widthSpec = View.MeasureSpec.makeMeasureSpec((int) this.width, 1073741824);
        getTextView().measure(widthSpec, 0);
        return getTextView().getMeasuredWidth();
    }

    public void setTypeFace(boolean bold, boolean italic) {
        if (bold && italic) {
            getTextView().setTypeface(null, 3);
        } else if (bold) {
            getTextView().setTypeface(null, 1);
        } else if (italic) {
            getTextView().setTypeface(null, 2);
        } else {
            getTextView().setTypeface(null, 0);
        }
    }

    public static Typeface getDefaultFont() {
        if (defaultFont == null) {
            try {
                Log.i("andLabel", "Attempting to load typeface:Fonts/HELVETICANEUELTW1G-ROMAN.OTF");
                defaultFont = Typeface.createFromAsset(ActivityWrapper.getActivity().getAssets(), "Fonts/HELVETICANEUELTW1G-ROMAN.OTF");
                Log.i("andLabel", "Successfully loaded typeface (Fonts/HELVETICANEUELTW1G-ROMAN.OTF)");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return defaultFont;
    }

    protected class andLabelImpl extends TextView {
        public andLabelImpl() {
            super(ActivityWrapper.getActivity());
            setText("");
            setTextSize(20.0f);
            setId(andViewManager.genID());
            setHorizontallyScrolling(false);
            setTypeface(andLabel.getDefaultFont());
        }

        @Override // android.widget.TextView, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            andLabel.this.onAttachedToWindow(andLabel.this.getHandle());
        }
    }
}