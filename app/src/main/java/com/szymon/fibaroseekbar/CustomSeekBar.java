package com.szymon.fibaroseekbar;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.SeekBar;

import java.util.concurrent.TimeUnit;

public class CustomSeekBar extends SeekBar
        implements SeekBar.OnSeekBarChangeListener {

    private final ObjectAnimator mAnimator  = new ObjectAnimator();
    private final TextPaint      mTimePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Handler        mHandler   = new Handler();

    private int   mScaledTouchSlop = 0;
    private float mTouchDownX      = 0;

    private float  mTimePosX    = 0;
    private float  mTimePosY    = 0;
    private String mTimeElapsed = "0";
    private long   mTimeBase    = 0;

    public CustomSeekBar(Context context) {
        super(context);
        initialize(context, null, 0, 0);
    }

    public CustomSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0, 0);
    }

    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr, 0);
    }

    @SuppressLint("NewApi")
    public CustomSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setOnSeekBarChangeListener(this);

        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.setPropertyName("progress");
        mAnimator.setDuration(300);
        mAnimator.setTarget(this);

        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int timeTextColor = a.getColor(0, 0);
        a.recycle();

        a = context.obtainStyledAttributes(attrs, R.styleable.CustomSeekBar, defStyleAttr, defStyleRes);
        timeTextColor = a.getColor(R.styleable.CustomSeekBar_timerTextColor, timeTextColor);
        a.recycle();

        DisplayMetrics dm = getResources().getDisplayMetrics();

        mTimePaint.setColor(timeTextColor);
        mTimePaint.setTextAlign(Paint.Align.RIGHT);
        mTimePaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 11, dm));

        setThumb(new SeekBarThumb(dm));

        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (w != oldw) setMax(w);

        mTimePosX = w / 4;
        mTimePosY = h * 0.9f;
    }

    @Override
    protected synchronized void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText(mTimeElapsed, mTimePosX, mTimePosY, mTimePaint);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mHandler.removeCallbacks(mTimerRunnable);
        mTimeBase = System.nanoTime();
        mHandler.post(mTimerRunnable);
    }

    private Runnable mTimerRunnable = new Runnable() {

        @Override
        public void run() {
            mTimeElapsed = String.valueOf(TimeUnit.SECONDS.convert(
                    System.nanoTime() - mTimeBase, TimeUnit.NANOSECONDS));
            invalidate();
            mHandler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int percentage = (int)((float)getProgress() * 100.0f / (float)getMax());

        if (percentage < 100) {
            if (percentage < 75) {
                if (percentage < 25) {
                    mAnimator.setIntValues(0);
                } else {
                    mAnimator.setIntValues(getMax() / 2);
                }
            } else {
                mAnimator.setIntValues(getMax());
            }
            mAnimator.start();
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (super.onTouchEvent(event)) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTouchDownX = event.getX();
                    mHandler.postDelayed(mCheckForLongPressRunnable, 1000);
                    break;

                default:
                    if (Math.abs(event.getX() - mTouchDownX) > mScaledTouchSlop) {
                        mHandler.removeCallbacks(mCheckForLongPressRunnable);
                    }
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onDragEvent(DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                setVisibility(INVISIBLE);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                setVisibility(VISIBLE);
                break;
        }
        return false;
    }

    @SuppressWarnings("unused")
    public void setTimeTextColor(int color) {
        if (mTimePaint.getColor() != color) {
            mTimePaint.setColor(color);
            invalidate();
        }
    }

    private Runnable mCheckForLongPressRunnable = new Runnable() {

        @Override
        public void run() {
            if (isPressed()) performLongClick();
        }
    };

    @Override
    public boolean performLongClick() {
        startDrag(ClipData.newPlainText("", ""), new View.DragShadowBuilder(this), this, 0);
        return true;
    }
}
