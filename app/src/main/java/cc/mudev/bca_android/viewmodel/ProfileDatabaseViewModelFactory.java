package cc.mudev.bca_android.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ProfileDatabaseViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("팩토리 런타임 에러");
        } catch (InstantiationException e) {
            e.printStackTrace();
            throw new RuntimeException("팩토리 런타임 에러2");
        }
    }
}