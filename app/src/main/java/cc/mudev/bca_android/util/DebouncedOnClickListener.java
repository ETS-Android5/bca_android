package cc.mudev.bca_android.util;

import android.os.SystemClock;
import android.view.View;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * A Debounced OnClickListener
 * Rejects clicks that are too close together in time.
 * This class is safe to use as an OnClickListener for multiple views, and will debounce each one separately.
 * from https://stackoverflow.com/a/16534470
 */
public abstract class DebouncedOnClickListener implements View.OnClickListener {
    private static final long MINUMUM_INTERVAL_MILLISEC = 2000;

    private final long minimumIntervalMillis;
    private final Map<View, Long> lastClickMap;

    /**
     * Implement this in your subclass instead of onClick
     * @param v The view that was clicked
     */
    public abstract void onDebouncedClick(View v);

    /**
     * The one and only constructor
     * @param minimumIntervalMillis The minimum allowed time between clicks - any click sooner than this after a previous click will be rejected
     */
    public DebouncedOnClickListener(long minimumIntervalMillis) {
        this.minimumIntervalMillis = minimumIntervalMillis;
        this.lastClickMap = new WeakHashMap<>();
    }

    public DebouncedOnClickListener() {
        this.minimumIntervalMillis = MINUMUM_INTERVAL_MILLISEC;
        this.lastClickMap = new WeakHashMap<>();
    }

    @Override
    public void onClick(View clickedView) {
        Long previousClickTimestamp = lastClickMap.get(clickedView);
        long currentTimestamp = SystemClock.uptimeMillis();

        lastClickMap.put(clickedView, currentTimestamp);
        if(previousClickTimestamp == null || Math.abs(currentTimestamp - previousClickTimestamp) > minimumIntervalMillis) {
            onDebouncedClick(clickedView);
        }
    }
}