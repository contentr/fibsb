package com.szymon.fibaroseekbar;

import android.animation.ObjectAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.StateListDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.animation.DecelerateInterpolator;

public class SeekBarThumb extends StateListDrawable {

    private final ObjectAnimator mScaleAnimator = new ObjectAnimator();

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final float mRadiusMin;
    private final float mRadiusMax;

    private float mRadius = 0;

    private boolean mPressed = false;

    private float mCenterX = 0;
    private float mCenterY = 0;

    public SeekBarThumb(DisplayMetrics metrics) {
        mScaleAnimator.setInterpolator(new DecelerateInterpolator());
        mScaleAnimator.setPropertyName("radius");
        mScaleAnimator.setDuration(300);
        mScaleAnimator.setTarget(this);

        mPaint.setColor(0xffd50000);
        mPaint.setStyle(Paint.Style.FILL);

        float shadowSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,  metrics);
        mPaint.setShadowLayer(shadowSize, 0, shadowSize, 0x42000000);

        mRadiusMin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6,  metrics);
        mRadiusMax = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, metrics);
        mRadius    = mRadiusMin;

        int width  = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, metrics);
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, metrics);
        setBounds(0, 0, width, height);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);

        Rect bounds = getBounds();
        mCenterX = bounds.centerX();
        mCenterY = bounds.centerY();
    }

    @Override
    protected boolean onStateChange(int[] states) {
        for (int state : states) {
            switch (state) {
                case android.R.attr.state_pressed: return setPressed(true);
            }
        }
        return setPressed(false);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);
    }

    public boolean setPressed(boolean pressed) {
        if (mPressed != pressed) {
            mPressed = pressed;

            if (mScaleAnimator.isStarted()) mScaleAnimator.cancel();

            if (mPressed) {
                mScaleAnimator.setFloatValues(mRadiusMax);
            } else {
                mScaleAnimator.setFloatValues(mRadiusMin);
            }
            mScaleAnimator.start();
            return true;
        }
        return false;
    }

    @SuppressWarnings("unused")
    public void setRadius(float radius) {
        if (mRadius != radius) {
            mRadius = radius;
            invalidateSelf();
        }
    }

    @SuppressWarnings("unused")
    public float getRadius() {
        return mRadius;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
        invalidateSelf();
    }

    @Override
    public int getIntrinsicHeight() {
        return getBounds().height();
    }

    @Override
    public int getIntrinsicWidth() {
        return getBounds().width();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
