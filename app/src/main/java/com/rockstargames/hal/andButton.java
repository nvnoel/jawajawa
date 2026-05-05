package com.rockstargames.hal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class andButton extends andLabel {
    private static StateListDrawable blankDrawable;
    static int staticCount = 0;
    Object pressedPacked;
    private TextView textView;
    Object unpressedPacked;

    public native void onClick(int i);

    @Override // com.rockstargames.hal.andView
    public native void onTouchEvent(int i, int i2, int i3, float f, float f2);

    public andButton(int handle) {
        super(handle, false);
        setView(new andButtonImpl());
        ContainerLayout containerLayout = this.container;
        TextView textView = new TextView(ActivityWrapper.getActivity());
        this.textView = textView;
        containerLayout.addView(textView);
        AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(100, 100, 0, 0);
        this.textView.setLayoutParams(lp);
        this.textView.setGravity(17);
        if (blankDrawable == null) {
            blankDrawable = new StateListDrawable();
            blankDrawable.addState(new int[0], new ColorDrawable(0));
        }
        getImageButton().setBackgroundDrawable(blankDrawable);
        staticCount++;
    }

    @Override // com.rockstargames.hal.andLabel, com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andButton createView(int handle) {
        return new andButton(handle);
    }

    protected andButtonImpl getImageButton() {
        return (andButtonImpl) this.view;
    }

    @Override // com.rockstargames.hal.andLabel
    public void setText(String text) {
        this.textView.setText(text);
    }

    public void setFont(String font) {
    }

    @Override // com.rockstargames.hal.andLabel
    public TextView getTextView() {
        return this.textView;
    }

    @Override // com.rockstargames.hal.andLabel
    public void setTextSize(float size) {
        getTextView().setTextSize(size / getTextView().getResources().getDisplayMetrics().scaledDensity);
    }

    public void setTextColour(int colour) {
        this.textView.setTextColor(colour);
    }

    @Override // com.rockstargames.hal.andLabel, com.rockstargames.hal.andView
    public void setBounds(float x, float y, float w, float h, float rightPadding, float bottomPadding) {
        super.setBounds(x, y, w, h, rightPadding, bottomPadding);
        AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams) getTextView().getLayoutParams();
        lp.x = 0;
        lp.y = 0;
        lp.width = (int) w;
        lp.height = (int) h;
        getTextView().setLayoutParams(lp);
    }

    public void setBackgroundImages(andImage unpressed, andImage pressed) {
        if (unpressed == null) {
            if (pressed != null) {
                unpressed = pressed;
            } else {
                return;
            }
        }

        getImageButton().setPackedImage(this.unpressedPacked);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public class andButtonImpl extends ImageButton implements View.OnTouchListener {
        private boolean attached;
        Object packedImage;
        ColorFilter pressedFilter;

        public andButtonImpl() {
            super(ActivityWrapper.getActivity());
            this.pressedFilter = null;
            this.attached = false;
            setOnTouchListener(this);
            setId(andViewManager.genID());
            setAdjustViewBounds(false);
            setScaleType(ImageView.ScaleType.FIT_XY);
        }

        @Override // android.widget.ImageView, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.attached = true;
        }

        @Override // android.widget.ImageView, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.attached = false;
        }

        boolean isAttached() {
            return this.attached;
        }

        @Override // android.view.View
        public boolean dispatchTouchEvent(MotionEvent event) {
            if (event.getAction() == 1 || event.getAction() == 3 || event.getAction() == 4) {
                this.pressedFilter = null;
                invalidate();
            }
            if (andButton.this.userInteractionEnabled) {
                int eventaction = event.getAction();
                switch (eventaction) {
                    case 0:
                        andButton.this.onClick(andButton.this.handle);
                        ColorFilter filter = new LightingColorFilter(-6710887, 0);
                        this.pressedFilter = filter;
                        invalidate();
                        break;
                }
                return super.dispatchTouchEvent(event);
            }
            return false;
        }

        @Override // android.view.View.OnTouchListener
        public boolean onTouch(View v, MotionEvent event) {
            if (!andButton.this.userInteractionEnabled) {
                return false;
            }
            int index = event.getActionIndex();
            andButton.this.onTouchEvent(andButton.this.handle, index, event.getAction(), event.getX(index), event.getY(index));
            return true;
        }

        public void setPackedImage(Object packedImage) {
            this.packedImage = packedImage;
            invalidate();
        }

        @Override // android.widget.ImageView, android.view.View
        public void onDraw(Canvas canvas) {
            if (this.packedImage != null) {
                android.graphics.Bitmap bmp = null;
                if (bmp == null || bmp.isRecycled()) {
                    invalidate();
                }

            }
        }
    }
}