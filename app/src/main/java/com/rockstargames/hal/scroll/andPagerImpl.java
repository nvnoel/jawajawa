package com.rockstargames.hal.scroll;

import android.annotation.SuppressLint;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsoluteLayout;
import com.rockstargames.hal.ActivityWrapper;
import com.rockstargames.hal.ContainerLayout;
import com.rockstargames.hal.andImage;
import com.rockstargames.hal.andScrollView;
import java.util.ArrayList;

public class andPagerImpl extends androidx.viewpager.widget.ViewPager implements andScrollView.ScrollImpl {
    andPagerAdapter adapter;
    private final andScrollView andScrollView;
    int currentPage;
    public FakeContainer fakeContainer;
    private ArrayList<View> items;
    private int lastUpdatePage;
    private int pageFromNative;
    private boolean touchEventsDisabled;

    public class FakeContainer extends ContainerLayout {
        public FakeContainer() {
            super(ActivityWrapper.getActivity());
        }

        @Override // android.view.ViewGroup
        public void addView(View child) {
            andPagerImpl.this.items.add(child);
            andPagerImpl.this.lastUpdatePage = -1;
            if (false) {

            } else {

            }
        }

        @Override // android.view.ViewGroup, android.view.ViewManager
        public void removeView(View view) {
            andPagerImpl.this.items.remove(view);
            andPagerImpl.this.lastUpdatePage = -1;
            if (false) {

            } else {

            }
        }

        @Override // android.view.ViewGroup
        public void removeAllViews() {
            andPagerImpl.this.items.clear();
        }
    }

    private class andPagerAdapter extends PagerAdapter {
        private ArrayList<View> addedViews;

        private andPagerAdapter() {
            this.addedViews = new ArrayList<>();
        }

        public int getCount() {
            return andPagerImpl.this.items.size();
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            int index = ((Integer) arg1).intValue();
            return index < andPagerImpl.this.items.size() && arg0 == andPagerImpl.this.items.get(index);
        }

        public Object instantiateItem(ViewGroup container, int position) {
            return Integer.valueOf(position);
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        public int getItemPosition(Object object) {
            return -2;
        }

        public void finishUpdate(ViewGroup container) {
            if (andPagerImpl.this.lastUpdatePage != andPagerImpl.this.currentPage) {
                container.removeAllViews();
                for (int i = 0; i < andPagerImpl.this.items.size(); i++) {
                    container.addView((View) andPagerImpl.this.items.get(i));
                }
                andPagerImpl.this.lastUpdatePage = andPagerImpl.this.currentPage;
            }

        }
    }

    private class Listener implements ViewPager.OnPageChangeListener {
        private Listener() {
        }

        public void onPageScrollStateChanged(int state) {
            if (state == 0) {
            }
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageSelected(int page) {
            if (!andPagerImpl.this.touchEventsDisabled) {
                andPagerImpl.this.andScrollView.scrollViewPageWillChange(andPagerImpl.this.andScrollView.getHandle(), page);
                if (andPagerImpl.this.currentPage != page) {
                    andPagerImpl.this.currentPage = page;
                    andPagerImpl.this.andScrollView.scrollViewPageDidChange(andPagerImpl.this.andScrollView.getHandle(), andPagerImpl.this.currentPage);
                    andPagerImpl.this.pageFromNative = page;
                }
            }
        }
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void setBackgroundAndImage(andImage img) {
    }

    public andPagerImpl(andScrollView andScrollView) {
        super(ActivityWrapper.getActivity());
        this.touchEventsDisabled = false;
        this.pageFromNative = -1;
        this.items = new ArrayList<>();
        this.currentPage = 0;
        this.lastUpdatePage = -1;
        this.fakeContainer = new FakeContainer();
        this.adapter = new andPagerAdapter();
        this.andScrollView = andScrollView;
        AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(100, 100, 0, 0);
        setLayoutParams(params);

    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void setTouchEventsEnabled(boolean b) {
        this.touchEventsDisabled = !b;
    }

    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (!this.touchEventsDisabled) {
            this.andScrollView.onTouch(null, arg0);
        }
        return super.onInterceptTouchEvent(arg0);
    }

    public boolean onTouchEvent(MotionEvent arg0) {
        if (!this.touchEventsDisabled) {
            this.andScrollView.onTouch(null, arg0);
            View parent = (View) getParent();
            if (parent != null) {
                parent.invalidate();
            }
        }
        return super.onTouchEvent(arg0);
    }

    @SuppressLint({"DrawAllocation"})
    protected void onMeasure(int arg0, int arg1) {
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (!(v.getLayoutParams() instanceof androidx.viewpager.widget.ViewPager.LayoutParams)) {
                Log.e("andPager", "Bad view: " + v.toString());
                androidx.viewpager.widget.ViewPager.LayoutParams lp = new androidx.viewpager.widget.ViewPager.LayoutParams();
                lp.width = v.getLayoutParams().width;
                lp.height = v.getLayoutParams().height;
                v.setLayoutParams(lp);
            }
        }
        Log.e("andPager", "This view's layoutparams: " + getLayoutParams());
        try {
            super.onMeasure(arg0, arg1);
        } catch (Exception ex) {
            Log.e("andPager", "Error measuring", ex);
        }
    }

    public void addView(View child) {
        if (!(child.getLayoutParams() instanceof androidx.viewpager.widget.ViewPager.LayoutParams)) {
            child.setLayoutParams(new androidx.viewpager.widget.ViewPager.LayoutParams());
        }
        super.addView(child);
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void addSubScroll(andScrollView.ScrollImpl v) {
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void touchEvent(MotionEvent mv) {
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public void setPage(int page) {
        setCurrentItem(page);
        this.andScrollView.scrollViewPageWillChange(this.andScrollView.getHandle(), page);
        if (this.currentPage != page) {
            this.currentPage = page;
            this.andScrollView.scrollViewPageDidChange(this.andScrollView.getHandle(), this.currentPage);
            this.pageFromNative = page;
        }
    }

    @Override // com.rockstargames.hal.andScrollView.ScrollImpl
    public ContainerLayout getContainer() {
        return this.fakeContainer;
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
        this.fakeContainer.removeAllViews();
        try {
            setAdapter(null);
        } catch (NullPointerException e) {
            Log.e("andPager", "Unable to remove adapter", e);
        }
    }
}