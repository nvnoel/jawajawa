package com.rockstargames.hal;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsoluteLayout;
import com.rockstargames.hal.andView;

public class andScreen extends andView {
    static int staticCount = 0;
    private View loadingScreenView;
    private float ph;
    private float pw;
    private float px;
    private float py;
    private String screenName;

    public andScreen(int handle) {
        super(handle);
        this.loadingScreenView = null;
        staticCount++;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public void createLoadingScreen(String screenName_) {
        Log.i("andScreen", "Inflating loading screen...");
        this.screenName = screenName_;
        LayoutInflater inflater = (LayoutInflater) ActivityWrapper.getActivity().getSystemService("layout_inflater");
        if (this.screenName.equals("LSCustomsPage")) {
//             int lscustomsloadingscreen = Helpers.GetResourceIdentifier(ActivityWrapper.getApplicationContext(), "lscustomsloadingscreen", "layout");
            this.loadingScreenView = null;
        } else {
//             int loadingscreen = Helpers.GetResourceIdentifier(ActivityWrapper.getApplicationContext(), "loadingscreen", "layout");
            this.loadingScreenView = null;
        }
        ActivityWrapper.getLayout().addView(this.loadingScreenView, 0);
        ActivityWrapper.getLayout().setClipChildren(false);
    }

    public static andScreen createView(int handle) {
        andScreen a = new andScreen(handle);
        a.getClass();
        a.setView(null);
        return a;
    }

    @Override // com.rockstargames.hal.andView
    public void addSubview(andView v) {
        super.addSubview(v);
    }

    @Override // com.rockstargames.hal.andView
    public void setBounds(float x, float y, float w, float h, float rightPadding, float bottomPadding) {
        float x2 = x + w;
        super.setBounds(x2, y, w, h, rightPadding, bottomPadding);
        this.px = x2;
        this.py = y;
        this.pw = w;
        this.ph = h;
        if (this.loadingScreenView != null) {
            this.loadingScreenView.setLayoutParams(new AbsoluteLayout.LayoutParams((int) w, (int) h, ((int) x2) - ((int) w), 0));
            this.loadingScreenView.bringToFront();
        }
    }

    public void showLoadingGrid(boolean b) {
        if (b) {
            if (this.loadingScreenView == null) {
                createLoadingScreen(this.screenName);
            }
            this.loadingScreenView.bringToFront();
            this.loadingScreenView.setLayoutParams(new AbsoluteLayout.LayoutParams((int) this.pw, (int) this.ph, (int) this.px, 0));
            return;
        }
        cleanup();
    }

    public void cleanup() {
        Log.i("andScreen", "Cleaning up a (loading) screen...");
        if (this.loadingScreenView != null) {
            ViewParent vp = this.loadingScreenView.getParent();
            if (vp instanceof ViewGroup) {
                ((ViewGroup) vp).removeView(this.loadingScreenView);
            }
            this.loadingScreenView = null;
        }
        ViewParent vp2 = this.view == null ? null : this.view.getParent();
        if (vp2 != null) {
            if (vp2 instanceof ViewGroup) {
                ((ViewGroup) vp2).removeView(this.view);
            } else {
                Log.e("andScreen", "Unable to remove screen from parent!");
            }
        }
    }

    public void Quit() {
        ActivityWrapper.getActivity().finish();
    }
}