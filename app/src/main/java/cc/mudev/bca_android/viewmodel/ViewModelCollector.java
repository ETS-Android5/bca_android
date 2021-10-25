package cc.mudev.bca_android.viewmodel;

import android.content.Context;

public class ViewModelCollector {
    private boolean isInitialized = false;

    private ViewModelCollector() {

    }

    private static class LazyHolder {
        private static final ViewModelCollector INSTANCE = new ViewModelCollector();
    }

    public static ViewModelCollector getInstance() {
        return LazyHolder.INSTANCE;
    }

    public void initialize(Context context) {
        if (this.isInitialized) {
            return;
        }

        this.isInitialized = true;
        // Do initialization things here
    }
}