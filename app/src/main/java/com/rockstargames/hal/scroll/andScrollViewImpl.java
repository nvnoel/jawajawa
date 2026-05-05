package com.rockstargames.hal.scroll;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsoluteLayout;
import android.widget.ScrollView;
import com.rockstargames.hal.ActivityWrapper;
import com.rockstargames.hal.ContainerLayout;
import com.rockstargames.hal.andImage;
import com.rockstargames.hal.andScrollView;
import com.rockstargames.hal.andViewManager;
import java.lang.ref.SoftReference;
import java.util.ArrayList;

public class andScrollViewImpl extends ScrollView implements andScrollView.ScrollImpl {
    private final andScrollView andScrollView;
    Object backgroundPackedImage;
    private Paint backgroundPaint;
    private Rect backgroundRect;
    private final ContainerLayout container;
    private SoftReference<Bitmap> lastBackgroundBitmap;
    private ArrayList<andScrollView.ScrollImpl> subScrolls;
    private boolean touchEventsDisabled;
    Point touchStart;

    public andScrollViewImpl(andScrollView andScrollView) {
        super(ActivityWrapper.getActivity());
        this.touchEventsDisabled = false;
        this.touchStart = new Point();
        this.subScrolls = new ArrayList<>();
        this.andScrollView = andScrollView;
        setLayoutParams(new AbsoluteLayout.LayoutParams(200, 200, 0, 0));
        setId(andViewManager.genID());
        this.container = new ContainerLayout(ActivityWrapper.getActivity());
        this.container.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        addView(this.container);
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void setBackgroundAndImage(andImage img) {

        this.backgroundRect = new Rect();
        this.backgroundPaint = new Paint();
        android.graphics.Bitmap bmp = null;
        this.lastBackgroundBitmap = new SoftReference<>(bmp);
        BitmapShader bms = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.REPEAT);
        this.backgroundPaint.setShader(bms);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.backgroundPackedImage != null) {
            android.graphics.Bitmap bmp = null;
            if (this.lastBackgroundBitmap == null || bmp != this.lastBackgroundBitmap.get()) {
                this.lastBackgroundBitmap = new SoftReference<>(bmp);
                BitmapShader bms = new BitmapShader(bmp, Shader.TileMode.CLAMP, Shader.TileMode.REPEAT);
                this.backgroundPaint.setShader(bms);
            }
            this.backgroundRect.set(0, 0, canvas.getWidth(), canvas.getHeight() * 2);
            canvas.drawBitmap(bmp, (Rect) null, this.backgroundRect, this.backgroundPaint);
        }
        super.onDraw(canvas);
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void setTouchEventsEnabled(boolean b) {
        this.touchEventsDisabled = !b;
    }

    @Override // android.view.View
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (!this.touchEventsDisabled) {
            this.andScrollView.scrollViewDidScroll(this.andScrollView.getHandle());
        }
    }

    @Override // android.widget.ScrollView, android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case 0:
                this.touchStart.x = (int) ev.getX();
                this.touchStart.y = (int) ev.getY();
                break;
            case 2:
                if (Math.abs(this.touchStart.x - ev.getX()) > Math.abs(this.touchStart.y - ev.getY())) {
                    return false;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override // android.widget.ScrollView, android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);
        if (!this.touchEventsDisabled) {
            this.andScrollView.onTouchEvent(event);
        }
        return ret;
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void touchEvent(MotionEvent mv) {
        if (!this.touchEventsDisabled) {
            onTouchEvent(mv);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (ViewParent v = getParent(); v != null; v = v.getParent()) {
            if (v instanceof andScrollView.ScrollImpl) {
                ((andScrollView.ScrollImpl) v).addSubScroll(this);
            }
        }
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void addSubScroll(andScrollView.ScrollImpl v) {
        this.subScrolls.add(v);
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void setPage(int page) {
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public ContainerLayout getContainer() {
        return this.container;
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void transferContainerContents(andScrollView.ScrollImpl from) {
        ContainerLayout layout = from.getContainer();
        while (layout.getChildCount() > 0) {
            View v = layout.getChildAt(0);
            ViewParent vp = v.getParent();
            if (vp != null && (vp instanceof ViewGroup)) {
                ((ViewGroup) vp).removeView(v);
            }
            layout.addView(v);
        }
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void removeAllSubviews() {
        this.container.removeAllViews();
    }
}