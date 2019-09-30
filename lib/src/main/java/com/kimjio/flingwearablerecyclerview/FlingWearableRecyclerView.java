package com.kimjio.flingwearablerecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.wear.widget.WearableRecyclerView;

import com.kimjio.flingwearablerecyclerview.rotary.RotaryInputReader;
import com.kimjio.flingwearablerecyclerview.rotary.RotaryScrollHandler;
import com.kimjio.flingwearablerecyclerview.rotary.ScrollController;
import com.kimjio.flingwearablerecyclerview.rotary.ScrollHandler;

public class FlingWearableRecyclerView extends WearableRecyclerView {

    public static final int STRENGTH_LOW = 1;
    public static final int STRENGTH_NORMAL = 2;
    public static final int STRENGTH_HIGH = 3;
    private float strength = RotaryScrollHandler.STRENGTH_NORMAL;
    private ScrollHandler handler;

    public FlingWearableRecyclerView(Context context) {
        this(context, null);
    }

    public FlingWearableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlingWearableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
    }

    public FlingWearableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle, int defStyleRes) {
        super(context, attrs, defStyle, defStyleRes);

        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.FlingWearableRecyclerView);
            int i = array.getInt(R.styleable.FlingWearableRecyclerView_flingStrength, 2);
            setStrength(i);
            array.recycle();
        }

        handler = RotaryScrollHandler.getInstance(context, strength,
                new ScrollController() {
                    @Override
                    public boolean fling(int velocity) {
                        return FlingWearableRecyclerView.this.fling(0, velocity);
                    }

                    @Override
                    public void scrollBy(int velocity) {
                        FlingWearableRecyclerView.this.scrollBy(0, velocity);
                    }

                    @Override
                    public void stopScroll() {
                        FlingWearableRecyclerView.this.stopScroll();
                    }
                });
    }

    public void setStrength(@Strength int strength) {
        switch (strength) {
            case STRENGTH_LOW:
                setStrength(RotaryScrollHandler.STRENGTH_LOW);
                break;
            case STRENGTH_NORMAL:
                setStrength(RotaryScrollHandler.STRENGTH_NORMAL);
                break;
            case STRENGTH_HIGH:
                setStrength(RotaryScrollHandler.STRENGTH_HIGH);
                break;
            default:
                throw new IllegalArgumentException("Must be one of: LOW, NORMAL, HIGH");
        }
    }

    public void setStrength(float strength) {
        if (handler != null)
            ((RotaryScrollHandler) handler).setStrength(strength);
        this.strength = strength;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (RotaryInputReader.isRotaryScrollEvent(event)) {
            return handler.handleScrollEvent(event);
        }

        return super.onGenericMotionEvent(event);
    }

    @IntDef({STRENGTH_LOW, STRENGTH_NORMAL, STRENGTH_HIGH})
    private @interface Strength {
    }
}
