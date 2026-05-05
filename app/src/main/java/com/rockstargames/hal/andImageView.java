package com.rockstargames.hal;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;

public class andImageView extends andView {
    private ShapeDrawable overlay;
    private int overlayColour;
    private ImageView overlayImageView;
    private float overlayScale;
    static int staticCount = 0;
    private static Paint caulkPaint = new Paint();

    public andImageView(int handle) {
        super(handle);
        this.overlay = null;
        this.overlayScale = 1.0f;
        this.overlayImageView = null;
        setView(new andImageViewImp());
        staticCount++;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andImageView createView(int handle) {
        return new andImageView(handle);
    }

    private andImageViewImp getImpl() {
        return (andImageViewImp) getView();
    }

    public void SetShapeOverlayScale(float value) {
        if (this.overlay != null) {
            this.overlayScale = value;
            updateOverlayScale();
        }
    }

    public void SetShapeOverlay(int a, int r, int g, int b) {
        this.overlayColour = Color.argb(a, r, g, b);
        if (this.overlay == null) {
            this.overlayImageView = new OverlayImageView();
            updateOverlayScale();
            AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(0, 0, 0, 0);
            this.overlayImageView.setLayoutParams(lp);
            this.container.addView(this.overlayImageView);
            return;
        }
        updateOverlayScale();
    }

    private static class OverlayImageView extends ImageView {
        public OverlayImageView() {
            super(ActivityWrapper.getActivity());
        }
    }

    void updateOverlayScale() {
        if (this.overlayImageView != null) {
            OvalShape shape = new OvalShape();
            this.overlay = new ShapeDrawable(shape);
            this.overlay.getPaint().setColor(this.overlayColour);
            this.overlay.getPaint().setAntiAlias(true);
            this.overlay.setIntrinsicWidth((int) (this.width * this.overlayScale));
            this.overlay.setIntrinsicHeight((int) (this.height * this.overlayScale));
            this.overlayImageView.setImageDrawable(this.overlay);
            AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams) this.overlayImageView.getLayoutParams();
            AbsoluteLayout.LayoutParams containerLayout = (AbsoluteLayout.LayoutParams) this.container.getLayoutParams();
            if (lp == null) {
                lp = new AbsoluteLayout.LayoutParams(0, 0, 0, 0);
            }
            lp.width = this.overlay.getIntrinsicWidth();
            lp.height = this.overlay.getIntrinsicHeight();
            lp.x = (containerLayout.width - lp.width) / 2;
            lp.y = (containerLayout.height - lp.height) / 2;
        }
    }

    @Override // com.rockstargames.hal.andView
    public void setBounds(float x, float y, float w, float h, float rightPadding, float bottomPadding) {
        super.setBounds(x, y, w, h, rightPadding, bottomPadding);
        updateOverlayScale();
    }

    public void setImage(andImage img) {
        if (img == null) {
            getImpl().setPackedImage(null);
            Log.e("andImageView", "NULL image passed in");
            return;
        }
        if (false) {
        }

        getImpl().setPadding(0, 0, 0, 0);
    }

    public void setTiled(boolean tiled) {
        getImpl().setTiled(tiled);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public class andImageViewImp extends View {
        boolean attached;
        Object packedImage;
        boolean tiled;

        public andImageViewImp() {
            super(ActivityWrapper.getActivity());
            this.attached = false;
            setId(andViewManager.genID());
            setOnTouchListener(andImageView.this);
        }

        @Override // android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.attached = true;
        }

        @Override // android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.attached = false;
        }

        public boolean isAttached() {
            return this.attached;
        }

        public void setPackedImage(Object packedImage) {
            if (packedImage == null) {
            }
            this.packedImage = packedImage;
            invalidate();
        }

        public void setTiled(boolean tiled) {
            this.tiled = tiled;
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            if (this.packedImage != null) {

                if (andImageView.this.antiFlicker) {
                    invalidate();
                }
            }
        }
    }
}