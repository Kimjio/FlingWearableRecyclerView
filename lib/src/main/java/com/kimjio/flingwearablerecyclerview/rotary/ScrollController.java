package com.kimjio.flingwearablerecyclerview.rotary;

public interface ScrollController {
    boolean fling(int velocity);

    void scrollBy(int velocity);

    void stopScroll();
}
