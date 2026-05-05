package com.rockstargames.hal.scroll;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsoluteLayout;
import android.widget.HorizontalScrollView;
import com.rockstargames.hal.ActivityWrapper;
import com.rockstargames.hal.ContainerLayout;
import com.rockstargames.hal.andImage;
import com.rockstargames.hal.andScrollView;
import java.util.ArrayList;

public class andHorizScrollViewImpl extends HorizontalScrollView implements andScrollView.ScrollImpl {
    private final andScrollView andScrollView;
    private final ContainerLayout container;
    private ArrayList<andScrollView.ScrollImpl> subScrolls;
    private boolean touchEventsDisabled;

    public andHorizScrollViewImpl(andScrollView andScrollView) {
        super(ActivityWrapper.getActivity());
        this.touchEventsDisabled = false;
        this.subScrolls = new ArrayList<>();
        this.andScrollView = andScrollView;
        setLayoutParams(new AbsoluteLayout.LayoutParams(0, 0, 100, 100));
        setHorizontalScrollBarEnabled(true);
        this.container = new ContainerLayout(ActivityWrapper.getActivity());
        this.container.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        addView(this.container);
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void setBackgroundAndImage(andImage img) {
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

    @Override // android.widget.HorizontalScrollView, android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        int action;
        super.onTouchEvent(ev);
        if (!this.touchEventsDisabled && ((action = ev.getAction()) == 1 || action == 3)) {
            int x = getScrollX();
            int w = getWidth();
            int mod = x % w;
            if (x < w / 2) {
                scrollTo(x - mod, getScrollY());
            } else {
                scrollTo((w - mod) + x, getScrollY());
            }
        }
        return true;
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