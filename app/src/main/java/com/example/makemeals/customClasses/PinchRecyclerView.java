package com.example.makemeals.customClasses;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.recyclerview.widget.RecyclerView;

// more info https://stackoverflow.com/questions/47550749/scalegesturedetector-doesnot-work-over-recyclerview

/**
 * Extends RecyclerView to support pinch zoom of recycler view items.
 *
 * Overrides the onScale method to adjust the scale factor and overrides the onDraw method to
 * to apply the translation and scale factor
 *
 * Credit: https://stackoverflow.com/questions/47550749/scalegesturedetector-doesnot-work-over-recyclerview
 */
public class PinchRecyclerView extends RecyclerView {
    private ScaleGestureDetector scaleDetector;
    private float scaleFactor = 1.f;
    private float posX;
    private float posY;
    private float width;
    private float height;


    public PinchRecyclerView(Context context) {
        super(context);
        if (!isInEditMode())
            scaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    public PinchRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode())
            scaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    public PinchRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode())
            scaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        super.onTouchEvent(ev);
        scaleDetector.onTouchEvent(ev);
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        canvas.translate(posX, posY);
        canvas.scale(scaleFactor, scaleFactor);
        canvas.restore();
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        canvas.saveLayer(0, 0, width, height, null, Canvas.ALL_SAVE_FLAG);
        if (scaleFactor == 1.0f) {
            posX = 0.0f;
            posY = 0.0f;
        }
        canvas.translate(posX, posY);
        canvas.scale(scaleFactor, scaleFactor);
        super.dispatchDraw(canvas);
        canvas.restore();
        invalidate();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            // adjust the scale factor and apply it to the current scale
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 3.0f));

            invalidate();
            return true;
        }
    }
}