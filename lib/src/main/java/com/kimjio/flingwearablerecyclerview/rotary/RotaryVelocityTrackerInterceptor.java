package com.kimjio.flingwearablerecyclerview.rotary;

import android.view.VelocityTracker;

public final class RotaryVelocityTrackerInterceptor {
    public VelocityTracker mVelocityTracker = null;

    public final void end() {
        if (this.mVelocityTracker != null) {
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }
}
