package com.rockstargames.hal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;

public class ContainerLayout extends AbsoluteLayout {
    protected RectF boundsRect;
    boolean canInvalidate;
    boolean clippingChildren;
    protected Matrix matrix;
    protected float[] matrixValues;
    protected Matrix offsetMatrix;
    protected float[] offsetMatrixValues;
    protected PointF transformOffset;
    protected RectF transformedRect;

    public ContainerLayout(Context context) {
        super(context);
        this.boundsRect = new RectF();
        this.transformedRect = new RectF();
        this.transformOffset = new PointF();
        this.canInvalidate = true;
        this.clippingChildren = true;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        if (this.offsetMatrix != null) {
            canvas.save();
            canvas.concat(this.offsetMatrix);
        }
        try {
            super.dispatchDraw(canvas);
        } catch (Throwable t) {
            Log.e("ContainerLayout", "Error attempting to dispatch draw:", t);
        }
        if (this.offsetMatrix != null) {
            canvas.restore();
        }
    }

    public void setBounds(int x, int y, int width, int height) {
        float r = x + width;
        float b = y + height;
        if (this.boundsRect.left != x || this.boundsRect.top != y || this.boundsRect.right != r || this.boundsRect.bottom != b) {
            this.boundsRect.left = x;
            this.boundsRect.top = y;
            this.boundsRect.right = r;
            this.boundsRect.bottom = b;
            updateLayoutParams();
        }
    }

    public void setTransform(float a, float b, float c, float d, float tx, float ty) {
        if (this.matrix == null) {
            this.matrix = new Matrix();
            this.matrixValues = new float[16];
            this.offsetMatrix = new Matrix();
            this.offsetMatrixValues = new float[16];
        }
        this.transformOffset.x = tx;
        this.transformOffset.y = ty;
        this.matrixValues[0] = a;
        this.matrixValues[1] = c;
        this.matrixValues[2] = tx;
        this.matrixValues[3] = b;
        this.matrixValues[4] = d;
        this.matrixValues[5] = ty;
        this.matrixValues[6] = 0.0f;
        this.matrixValues[7] = 0.0f;
        this.matrixValues[8] = 1.0f;
        this.matrix.setValues(this.matrixValues);
        updateLayoutParams();
        if (this.canInvalidate) {
            invalidate();
            this.canInvalidate = false;
        }
    }

    protected void updateLayoutParams() {
        if (this.matrix == null) {
            this.transformedRect.set(this.boundsRect);
        } else {
            this.transformedRect.set(0.0f, 0.0f, this.boundsRect.width(), this.boundsRect.height());
            System.arraycopy(this.matrixValues, 0, this.offsetMatrixValues, 0, 9);
            float[] fArr = this.offsetMatrixValues;
            fArr[2] = fArr[2] + this.boundsRect.left;
            float[] fArr2 = this.offsetMatrixValues;
            fArr2[5] = fArr2[5] + this.boundsRect.top;
            this.offsetMatrix.setValues(this.offsetMatrixValues);
            this.offsetMatrix.mapRect(this.transformedRect);
            float[] fArr3 = this.offsetMatrixValues;
            fArr3[2] = fArr3[2] - this.transformedRect.left;
            float[] fArr4 = this.offsetMatrixValues;
            fArr4[5] = fArr4[5] - this.transformedRect.top;
            this.offsetMatrix.setValues(this.offsetMatrixValues);
        }
        ViewGroup.LayoutParams lp = getLayoutParams();
        if (lp == null) {
            setLayoutParams(new AbsoluteLayout.LayoutParams((int) this.transformedRect.width(), (int) this.transformedRect.height(), (int) this.transformedRect.left, (int) this.transformedRect.top));
        } else if (lp instanceof AbsoluteLayout.LayoutParams) {
            AbsoluteLayout.LayoutParams alp = (AbsoluteLayout.LayoutParams) lp;
            int w = (int) this.transformedRect.width();
            int h = (int) this.transformedRect.height();
            int x = (int) this.transformedRect.left;
            int y = (int) this.transformedRect.top;
            if (alp.width != w || alp.height != h || alp.x != x || alp.y != y) {
                lp.width = w;
                lp.height = h;
                ((AbsoluteLayout.LayoutParams) lp).x = x;
                ((AbsoluteLayout.LayoutParams) lp).y = (int) this.transformedRect.top;
                setLayoutParams(lp);
            }
        } else {
            lp.width = (int) this.boundsRect.width();
            lp.height = (int) this.boundsRect.height();
            setLayoutParams(lp);
        }
    }

    @Override // android.view.ViewGroup
    public void setClipChildren(boolean clipChildren) {
        super.setClipChildren(clipChildren);
        this.clippingChildren = clipChildren;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.matrix != null) {
            canvas.save();
            canvas.concat(this.matrix);
        }
        super.onDraw(canvas);
        if (this.matrix != null) {
            canvas.restore();
        }
        this.canInvalidate = true;
        if (this.clippingChildren) {
            invalidate();
        }
    }
}