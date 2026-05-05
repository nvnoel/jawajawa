package com.rockstargames.hal;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.rockstargames.hal.scroll.andHorizScrollViewImpl;
import com.rockstargames.hal.scroll.andPagerImpl;
import com.rockstargames.hal.scroll.andScrollViewImpl;

public class andScrollView extends andView {
    static int staticCount = 0;
    private ScrollImpl impl;
    private int pageFromNative;
    public boolean paging;
    private boolean touchEventsDisabled;

    public interface ScrollImpl {
        void addSubScroll(ScrollImpl scrollImpl);

        ContainerLayout getContainer();

        ViewParent getParent();

        void removeAllSubviews();

        void setBackgroundAndImage(andImage andimage);

        void setPage(int i);

        void setTouchEventsEnabled(boolean z);

        void touchEvent(MotionEvent motionEvent);

        void transferContainerContents(ScrollImpl scrollImpl);
    }

    @Override // com.rockstargames.hal.andView
    public native void onTouchEvent(int i, int i2, int i3, float f, float f2);

    public native void scrollViewDidScroll(int i);

    public native void scrollViewPageDidChange(int i, int i2);

    public native void scrollViewPageWillChange(int i, int i2);

    public andScrollView(int handle) {
        super(handle);
        this.touchEventsDisabled = false;
        this.pageFromNative = -1;
        this.paging = false;
        andScrollViewImpl vertical = new andScrollViewImpl(this);
        this.view = vertical;
        this.impl = vertical;
        this.container = vertical.getContainer();
        staticCount++;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andScrollView createView(int handle) {
        return new andScrollView(handle);
    }

    public void setHorizontal(boolean horizontal, boolean paged) {
        this.paging = paged;
        if (horizontal) {
            try {
                if (!paged) {
                    changeImplTo(new andHorizScrollViewImpl(this));
                } else {
                    changeImplTo(new andPagerImpl(this));
                }
            } catch (Exception ex) {
                Log.e("andScrollView", "Exception changing view to horizontal", ex);
            }
        }
    }

    public void changeImplTo(ScrollImpl changeTo) {
        if (this.impl != null) {
            changeTo.transferContainerContents(this.impl);
            ViewParent viewparent = this.impl.getParent();
            if (viewparent != null && (viewparent instanceof ViewGroup)) {
                ((ViewGroup) viewparent).removeView((View) this.impl);
                ((ViewGroup) viewparent).addView((View) changeTo);
            }
        }
        this.container = changeTo.getContainer();
        this.view = (View) changeTo;
        this.impl = changeTo;
    }

    public void setContentSize(int w, int h) {
    }

    public void setPage(int page) {
        if (this.impl != null) {
            this.impl.setPage(page);
        }
    }

    public void setTouchEventsEnabled(boolean b) {
        this.touchEventsDisabled = !b;
        this.impl.setTouchEventsEnabled(b);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.rockstargames.hal.andView
    public View getOuterView() {
        return this.view;
    }

    @Override // com.rockstargames.hal.andView
    protected View getInnerView() {
        return this.container;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.rockstargames.hal.andView
    public ViewGroup getContainer() {
        return this.container;
    }

    @Override // com.rockstargames.hal.andView
    public void removeFromSuperview() {
        ViewParent vp = this.view.getParent();
        if (vp != null && (vp instanceof ViewGroup)) {
            ((ViewGroup) vp).removeView(this.view);
        }
    }

    @Override // com.rockstargames.hal.andView
    public void removeAllSubviews() {
        if (this.impl != null) {
            this.impl.removeAllSubviews();
        }
    }

    public void setBackgroundImage(andImage img) {
        this.impl.setBackgroundAndImage(img);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!this.touchEventsDisabled) {
            int index = event.getActionIndex();
            onTouchEvent(getHandle(), index, event.getAction(), event.getX(index), event.getY(index));
            return true;
        }
        return true;
    }
}