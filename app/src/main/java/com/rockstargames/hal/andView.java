package com.rockstargames.hal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.ScrollView;
import java.util.ArrayList;
import java.util.Iterator;

public class andView implements View.OnTouchListener {
    static int staticCount = 0;
    protected ContainerLayout container;
    public String debugString;
    protected int handle;
    protected float height;
    protected float originX;
    protected float originY;
    protected float paddingB;
    protected float paddingR;
    public andView parent;
    protected View view;
    protected float width;
    protected boolean positionOverridden = false;
    public ArrayList<andView> children = new ArrayList<>();
    private float alpha = 1.0f;
    protected boolean antiFlicker = false;
    protected boolean userInteractionEnabled = true;

    /* JADX INFO: Access modifiers changed from: package-private */
    public native void onAttachedToWindow(int i);

    native void onTouchEvent(int i, int i2, int i3, float f, float f2);

    native void setPlatformSize(int i, int i2, int i3);

    public andView(int handle) {
        this.handle = handle;
        staticCount++;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andView createView(int handle) {
        andView a = new andView(handle);
        a.getClass();

        a.setBounds(0.0f, 0.0f, 200.0f, 200.0f, 0.0f, 0.0f);
        return a;
    }

    public int getHandle() {
        return this.handle;
    }

    public void setView(View view) {
        this.view = view;
        if (this.container == null) {
            this.container = new ContainerLayout(view.getContext());
            this.container.setId(andViewManager.genID());
            this.container.setPadding(0, 0, 0, 0);
        }
        view.setLayoutParams(new AbsoluteLayout.LayoutParams(100, 100, 0, 0));
        this.container.addView(view);
    }

    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View v, MotionEvent event) {
        if (!this.userInteractionEnabled) {
            return false;
        }
        int index = event.getActionIndex();
        onTouchEvent(getHandle(), index, event.getAction(), event.getX(index), event.getY(index));
        return true;
    }

    public void clipChildren(boolean clip) {
        getContainer().setClipChildren(clip);
    }

    public void sendViewToBack(andView view) {
        View outer = view.getOuterView();
        getContainer().removeView(outer);
        getContainer().addView(outer, 0);
    }

    public View getView() {
        return this.view;
    }

    public void setBounds(float x, float y, float w, float h, float rightPadding, float bottomPadding) {
        if (w <= 0.0f) {
            w = 600.0f;
        }
        if (h <= 0.0f) {
            h = 600.0f;
        }
        if (!this.positionOverridden) {
            this.originX = x;
            this.originY = y;
        }
        this.width = w;
        this.height = h;
        this.paddingR = rightPadding;
        this.paddingB = bottomPadding;
        recalcLayout();
    }

    protected void recalcLayout() {
        float scale = ActivityWrapper.getScale();
        int x = (int) (this.originX * scale);
        int y = (int) (this.originY * scale);
        int w = (int) (this.width * scale);
        int h = (int) (this.height * scale);
        View outerView = getOuterView();
        ViewGroup.LayoutParams vglp = outerView.getLayoutParams();
        AbsoluteLayout.LayoutParams lp = null;
        if (outerView instanceof ContainerLayout) {
            ((ContainerLayout) outerView).setBounds(x, y, w, h);
        } else {
            try {
                lp = (AbsoluteLayout.LayoutParams) vglp;
            } catch (Exception e) {
                Log.e("andView", "LayoutParams failed: " + vglp + " + " + outerView);
            }
            if (lp == null) {
                lp = new AbsoluteLayout.LayoutParams(w, h, x, y);
            } else {
                lp.x = x;
                lp.y = y;
                lp.width = w;
                lp.height = h;
            }
            outerView.setLayoutParams(lp);
        }
        View innerView = getInnerView();
        if (innerView != outerView) {
            if (innerView instanceof ContainerLayout) {
                ((ContainerLayout) innerView).setBounds(0, 0, w, h);
                return;
            }
            AbsoluteLayout.LayoutParams lp2 = null;
            ViewGroup.LayoutParams vglp2 = innerView.getLayoutParams();
            if (vglp2 != null) {
                try {
                    lp2 = (AbsoluteLayout.LayoutParams) vglp2;
                } catch (Exception e2) {
                    if (vglp2 == null || !(vglp2 instanceof ViewGroup.LayoutParams)) {
                        Log.e("andView", "LayoutParams failed: " + vglp2 + " + " + outerView);
                    } else {
                        return;
                    }
                }
            }
            if (lp2 == null) {
                lp2 = new AbsoluteLayout.LayoutParams(w, h, 0, 0);
            } else {
                lp2.x = 0;
                lp2.y = 0;
                lp2.width = w;
                lp2.height = h;
            }
            innerView.setLayoutParams(lp2);
        }
    }

    public void setBackgroundColour(int a, int r, int g, int b) {
        this.view.setBackgroundColor(Color.argb(a, r, g, b));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public ViewGroup getContainer() {
        return this.container;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public View getOuterView() {
        return getContainer();
    }

    protected View getInnerView() {
        return getView();
    }

    public void addSubview(andView v) {
        try {
            if (v instanceof andLabel) {
            }
            v.parent = this;
            this.container.addView(v.getOuterView());
            this.children.add(v);
        } catch (Exception ex) {
            Log.e("andView", "Exception adding view: " + v.getClass().getSimpleName(), ex);
        }
    }

    public void removeFromSuperview() {
        if (this.parent != null) {
            this.parent.getContainer().removeView(getOuterView());
            this.parent.children.remove(this);
            return;
        }
        Log.e("andView", "Attempting to remove view with no parent!");
    }

    public void removeAllSubviews() {
        while (!this.children.isEmpty()) {
            andView child = this.children.get(0);
            child.removeFromSuperview();
            this.children.remove(child);
        }
    }

    public andImage toImage(String path) {
        Bitmap viewBitmap = Bitmap.createBitmap((int) this.width, (int) this.height, Bitmap.Config.ARGB_8888);
        View v = getOuterView();
        v.measure((int) this.width, (int) this.height);
        v.layout(0, 0, (int) this.width, (int) this.height);
        v.draw(new Canvas(viewBitmap));
        float widthScale = 114.0f / this.width;
        float heightScale = 114.0f / this.height;
        float scale = Math.min(widthScale, heightScale);
        Matrix scaleMat = new Matrix();
        scaleMat.reset();
        scaleMat.setScale(scale, scale);
        Bitmap shrunkBitmap = Bitmap.createBitmap(viewBitmap, 0, 0, (int) this.width, (int) this.height, scaleMat, true);
        Bitmap finalBitmap = Bitmap.createBitmap(114, 114, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(finalBitmap);
        c.drawBitmap(shrunkBitmap, (114.0f - (this.width * scale)) * 0.5f, (114.0f - (this.height * scale)) * 0.5f, (Paint) null);











        Object image = null;
        return new andImage();
    }

    protected class andViewImpl extends ScrollView {
        public andViewImpl(Context context) {
            super(context);
            setId(andViewManager.genID());
            setOnTouchListener(andView.this);
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            andView.this.onAttachedToWindow(andView.this.getHandle());
        }
    }

    public void printHierarchy(int indent) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < indent; i++) {
            sb.append("    ");
        }
        sb.append(getDebugId()).append(": ").append(getClass().getSimpleName());
        sb.append("(").append(this.originX).append(", ").append(this.originY).append(") ").append(this.width).append(" x ").append(this.height);
        View cont = getContainer();
        sb.append(" / (").append(cont.getLeft()).append(", ").append(cont.getTop()).append(") ").append(cont.getWidth()).append(" x ").append(cont.getHeight());
        Log.d("andView", sb.toString());
        Iterator<andView> it = this.children.iterator();
        while (it.hasNext()) {
            andView c = it.next();
            c.printHierarchy(indent + 1);
        }
    }

    public void invalidateHierarchy() {
        if (this.view != null) {
            this.view.invalidate();
        }
        Iterator<andView> it = this.children.iterator();
        while (it.hasNext()) {
            andView c = it.next();
            c.invalidateHierarchy();
        }
    }

    public String getDebugId() {
        return this.debugString != null ? this.debugString : "Handle " + this.handle;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        getOuterView().setAlpha(Math.min(Math.max(alpha, 0.0f), 1.0f));
    }

    public float getAlpha() {
        return this.alpha;
    }

    public void setHidden(boolean hidden) {
        getOuterView().setVisibility(hidden ? 4 : 0);
    }

    public boolean getHidden() {
        return getOuterView().getVisibility() == 4;
    }

    public void setTransform(float a, float b, float c, float d, float tx, float ty) {
        this.container.setTransform(a, b, c, d, tx, ty);
    }

    public void setDebugString(String debugstr) {
        this.debugString = debugstr + " (handle " + this.handle + ")";
    }

    public void setAntiFlicker() {
        this.antiFlicker = true;
        Iterator<andView> it = this.children.iterator();
        while (it.hasNext()) {
            andView v = it.next();
            v.setAntiFlicker();
        }
    }

    public void setUserInteractionEnabled(boolean on) {
        this.userInteractionEnabled = on;
    }
}