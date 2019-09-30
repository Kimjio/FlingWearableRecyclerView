package com.kimjio.flingwearablerecyclerview.rotary;

import android.content.Context;
import android.view.MotionEvent;

import com.google.android.wearable.input.RotaryEncoderHelper;

public final class RotaryInputReader {
    private static RotaryInputReader INSTANCE;
    private final float mScaledScrollFactor;

    private RotaryInputReader(Context context) {
        this.mScaledScrollFactor = getScaledScrollFactor(context);
    }

    public static RotaryInputReader getInstance(Context context) {
        if (INSTANCE == null)
            synchronized (RotaryInputReader.class) {
                if (INSTANCE == null)
                    INSTANCE = new RotaryInputReader(context);
            }

        return INSTANCE;
    }

    private float getScaledScrollFactor(Context context) {
        try {
            return RotaryEncoderHelper.getScaledScrollFactor(context);
        } catch (RuntimeException e) {
            return 64F;
        }
    }

    public static boolean isRotaryScrollEvent(MotionEvent motionEvent) {
        return RotaryEncoderHelper.isFromRotaryEncoder(motionEvent) && motionEvent.getAction() == MotionEvent.ACTION_SCROLL;
    }

    public final float getScrollDistance(MotionEvent motionEvent) {
        return (-RotaryEncoderHelper.getRotaryAxisValue(motionEvent)) * this.mScaledScrollFactor;
    }
}
