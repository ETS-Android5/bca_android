package cc.mudev.bca_android.factory;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import cc.mudev.bca_android.viewmodel.ChatEventViewModel;

public class ChatEventViewModelFactory implements ViewModelProvider.Factory {
    private final Application mApplication;
    private final int roomId;

    public ChatEventViewModelFactory(Application application, int roomId) {
        this.mApplication = application;
        this.roomId = roomId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ChatEventViewModel(mApplication, roomId);
    }
}
