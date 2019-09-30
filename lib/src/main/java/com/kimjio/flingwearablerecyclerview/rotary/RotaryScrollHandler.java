package com.kimjio.flingwearablerecyclerview.rotary;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

import java.util.Objects;

public final class RotaryScrollHandler implements ScrollHandler {

    public static final float STRENGTH_LOW = 0.75f;
    public static final float STRENGTH_NORMAL = 1.25f;
    public static final float STRENGTH_HIGH = 2.45f;

    private static final float MIN_VELOCITY = 2000f;
    private static final float MAX_VELOCITY = 8000f;

    private static final int DEFAULT_POSITION = 1000;

    private final Handler mHandler;
    private final int mMaxFlingVelocity;
    private final RotaryInputReader mRotaryInputReader;
    private final RotaryVelocityTrackerInterceptor mRotaryVelocityTracker;
    private float strength;
    private int mCurrentPosition;
    private boolean mInRotaryMode = false;
    private final Runnable mEndRotaryScrollRunnable = new Runnable() {
        @Override
        public void run() {
            RotaryScrollHandler.this.mInRotaryMode = false;
            RotaryScrollHandler.this.mRotaryVelocityTracker.end();
        }
    };
    private boolean mIsExpectingFling = false;
    private boolean mIsFlingGuardEnabled = false;
    private final Runnable mFlingGuardRunnable = new Runnable() {
        @Override
        public void run() {
            RotaryScrollHandler.this.mIsFlingGuardEnabled = false;
        }
    };
    private boolean mIsSmoothScrolling = false;
    private float mLastVelocity;
    private final Runnable mFlingTriggerRunnable = new Runnable() {
        @Override
        public void run() {
            fling();
        }
    };
    private ScrollController mController;

    private RotaryScrollHandler(Handler handler, RotaryInputReader rotaryInputReader, RotaryVelocityTrackerInterceptor rotaryVelocityTrackerInterceptor, int maxFlingVelocity, float strength, ScrollController controller) {
        this.mHandler = Objects.requireNonNull(handler);
        this.mRotaryInputReader = Objects.requireNonNull(rotaryInputReader);
        this.mRotaryVelocityTracker = Objects.requireNonNull(rotaryVelocityTrackerInterceptor);
        this.mCurrentPosition = DEFAULT_POSITION;
        this.mMaxFlingVelocity = maxFlingVelocity;
        this.strength = strength;
        this.mController = controller;
    }

    public static RotaryScrollHandler getInstance(Context context, float sensitivity, ScrollController controller) {
        return new RotaryScrollHandler(new Handler(), RotaryInputReader.getInstance(context), new RotaryVelocityTrackerInterceptor(), ViewConfiguration.get(context).getScaledMaximumFlingVelocity(), sensitivity, controller);
    }

    public void setStrength(float strength) {
        this.strength = strength;
    }

    private static float clamp(float f, float f2, float f3) {
        if (f < f2) {
            return f2;
        }
        if (f > f3) {
            return f3;
        }
        return f;
    }

    private void fling() {
        this.mController.fling((int) (this.mLastVelocity * strength));
        this.mInRotaryMode = false;
        this.mIsFlingGuardEnabled = true;
        this.mIsSmoothScrolling = true;
        this.mIsExpectingFling = false;
        this.mRotaryVelocityTracker.end();
        this.mHandler.postDelayed(this.mFlingGuardRunnable, 200);
    }

    @Override
    public boolean handleScrollEvent(MotionEvent event) {
        float velocity;
        boolean clamping = false;

        if (mIsSmoothScrolling) {
            mController.stopScroll();
            mIsSmoothScrolling = false;
        }
        if (mIsFlingGuardEnabled) {
            mHandler.removeCallbacks(mFlingGuardRunnable);
        }
        int round = Math.round(mRotaryInputReader.getScrollDistance(event));
        if (!mInRotaryMode) {
            mInRotaryMode = true;
            mIsExpectingFling = false;
            mCurrentPosition = DEFAULT_POSITION;
            mLastVelocity = 0.0f;
            RotaryVelocityTrackerInterceptor rotaryVelocityTrackerInterceptor = mRotaryVelocityTracker;
            Point point = new Point(0, mCurrentPosition);
            if (rotaryVelocityTrackerInterceptor.mVelocityTracker == null) {
                rotaryVelocityTrackerInterceptor.mVelocityTracker = VelocityTracker.obtain();
            } else {
                rotaryVelocityTrackerInterceptor.mVelocityTracker.clear();
            }
            MotionEvent obtain = MotionEvent.obtain(event);
            obtain.setAction(MotionEvent.ACTION_DOWN);
            obtain.setLocation((float) point.x, (float) point.y);
            rotaryVelocityTrackerInterceptor.mVelocityTracker.addMovement(obtain);
            obtain.recycle();
        }
        mController.scrollBy(round);
        mCurrentPosition = round + mCurrentPosition;
        RotaryVelocityTrackerInterceptor rotaryVelocityTrackerInterceptor2 = mRotaryVelocityTracker;
        Point point2 = new Point(0, mCurrentPosition);
        if (rotaryVelocityTrackerInterceptor2.mVelocityTracker != null && (point2.x >= 0 || point2.y >= 0)) {
            MotionEvent obtain2 = MotionEvent.obtain(event);
            obtain2.setAction(MotionEvent.ACTION_MOVE);
            obtain2.setLocation((float) point2.x, (float) point2.y);
            rotaryVelocityTrackerInterceptor2.mVelocityTracker.addMovement(obtain2);
            rotaryVelocityTrackerInterceptor2.mVelocityTracker.computeCurrentVelocity(DEFAULT_POSITION);
            obtain2.recycle();
        }
        RotaryVelocityTrackerInterceptor rotaryVelocityTrackerInterceptor3 = mRotaryVelocityTracker;
        if (rotaryVelocityTrackerInterceptor3.mVelocityTracker != null) {
            velocity = rotaryVelocityTrackerInterceptor3.mVelocityTracker.getYVelocity();
        } else {
            velocity = 0.0f;
        }
        mHandler.removeCallbacks(mEndRotaryScrollRunnable);
        float clamp = clamp(velocity, (float) (-mMaxFlingVelocity), (float) mMaxFlingVelocity);
        if (Math.abs(clamp) > MIN_VELOCITY) {
            mHandler.removeCallbacks(mFlingTriggerRunnable);
            if (mLastVelocity == 0.0f || Math.abs(clamp) >= Math.abs(mLastVelocity)) {
                mLastVelocity = clamp;
                if (clamp > MAX_VELOCITY) {
                    clamping = true;
                } else {
                    mHandler.postDelayed(mFlingTriggerRunnable, 20);
                    mIsExpectingFling = true;
                }
            } else {
                clamping = true;
            }
        }
        if (clamping) {
            fling();
        } else if (!mIsExpectingFling) {
            mHandler.postDelayed(mEndRotaryScrollRunnable, 300);
        }
        return true;
    }
}
