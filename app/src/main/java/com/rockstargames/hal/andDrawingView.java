package com.rockstargames.hal;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.view.View;
import java.util.ArrayList;
import java.util.Iterator;

public class andDrawingView extends andView {
    static int staticCount = 0;
    public ArrayList<Shape> shapes;

    public andDrawingView(int handle) {
        super(handle);
        this.shapes = new ArrayList<>();
        setView(new andDrawingViewImpl());
        staticCount++;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.rockstargames.hal.andView
    public void finalize() throws Throwable {
        staticCount--;
        super.finalize();
    }

    public static andDrawingView createView(int handle) {
        return new andDrawingView(handle);
    }

    public void invalidate() {
        ((andDrawingViewImpl) getView()).invalidate();
    }

    @Override // com.rockstargames.hal.andView
    public void setBounds(float x, float y, float w, float h, float rightPadding, float bottomPadding) {
        float oldWidth = this.width;
        float oldHeight = this.height;
        super.setBounds(x, y, w, h, rightPadding, bottomPadding);
        float scaleWidth = this.width / oldWidth;
        float scaleHeight = this.height / oldHeight;
        for (int i = 0; i < this.shapes.size(); i++) {
            Shape s = this.shapes.get(i);
            if (!s.filled) {
                float[] temp = new float[s.points.size() * 2];
                for (int p = 0; p < s.points.size(); p++) {
                    temp[p * 2] = s.points.get(p).x * scaleWidth;
                    temp[(p * 2) + 1] = s.points.get(p).y * scaleHeight;
                }
                s.path = new Path();
                s.points.clear();
                for (int p2 = 0; p2 < temp.length - 1; p2 += 2) {
                    s.addPointInternal(temp[p2], temp[p2 + 1]);
                }
                if (s.pathClosed) {
                    s.path.close();
                }
            }
        }
    }

    public static class Shape {
        public Paint fillPaint;
        public boolean filled;
        public Path path = new Path();
        public boolean pathClosed = false;
        public Paint strokePaint = new Paint();
        public ArrayList<PointF> points = new ArrayList<>();

        public Shape(boolean fill) {
            this.filled = fill;
            this.strokePaint.setStyle(Paint.Style.STROKE);
            if (this.filled) {
                this.fillPaint = new Paint();
                this.fillPaint.setStyle(Paint.Style.FILL);
            }
        }

        public void addPoint(float x, float y) {
            if (this.filled) {
                throw new RuntimeException("Attempting to add a point to a filled shape!");
            }
            addPointInternal(x, y);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void addPointInternal(float x, float y) {
            if (this.points.size() == 0) {
                this.path.moveTo(x, y);
            } else {
                this.path.lineTo(x, y);
            }
            this.points.add(new PointF(x, y));
        }

        public void removePoint(int index) {
            if (this.filled) {
                throw new RuntimeException("Attempting to remove a point from a filled shape!");
            }
            this.points.remove(index);
            this.path.reset();
            boolean first = true;
            Iterator<PointF> it = this.points.iterator();
            while (it.hasNext()) {
                PointF p = it.next();
                if (first) {
                    this.path.moveTo(p.x, p.y);
                    first = false;
                } else {
                    this.path.lineTo(p.x, p.y);
                }
            }
        }

        public void clear() {
            if (this.filled) {
                throw new RuntimeException("Attempting to clear a filled shape!");
            }
            this.points.clear();
            this.path.reset();
            this.pathClosed = false;
        }

        public void setPoints(float[] points) {
            if (!this.filled) {
                throw new RuntimeException("Attempting set all points to a line!");
            }
            this.path = new Path();
            this.points.clear();
            for (int i = 0; i < points.length - 1; i += 2) {
                addPointInternal(points[i], points[i + 1]);
            }
            this.path.close();
            this.pathClosed = true;
        }

        public void draw(Canvas canvas) {
            if (this.points.size() > 0) {
                if (this.fillPaint != null) {
                    canvas.drawPath(this.path, this.fillPaint);
                }
                canvas.drawPath(this.path, this.strokePaint);
            }
        }
    }

    public class andDrawingViewImpl extends View {
        public andDrawingViewImpl() {
            super(ActivityWrapper.getActivity());
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            Iterator<Shape> it = andDrawingView.this.shapes.iterator();
            while (it.hasNext()) {
                Shape s = it.next();
                s.draw(canvas);
            }
        }
    }

    public int createShape(boolean filled) {
        this.shapes.add(new Shape(filled));
        return this.shapes.size() - 1;
    }

    public void setFillColour(int handle, int a, int r, int g, int b) {
        this.shapes.get(handle).fillPaint.setColor(Color.argb(a, r, g, b));
        invalidate();
    }

    public void setStrokeColour(int handle, int a, int r, int g, int b) {
        this.shapes.get(handle).strokePaint.setColor(Color.argb(a, r, g, b));
        invalidate();
    }

    public void setStrokeThickness(int handle, float thickness) {
        this.shapes.get(handle).strokePaint.setStrokeWidth(thickness);
        invalidate();
    }

    public void addPoint(int handle, float x, float y) {
        this.shapes.get(handle).addPoint(x, y);
        invalidate();
    }

    public void removePoint(int handle, int pointIndex) {
        this.shapes.get(handle).removePoint(pointIndex);
        invalidate();
    }

    public void clear(int handle) {
        this.shapes.get(handle).clear();
        invalidate();
    }

    public void setPoints(int handle, float[] points) {
        this.shapes.get(handle).setPoints(points);
        invalidate();
    }
}