package cc.mudev.bca_android.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileDatabaseViewModel extends ViewModel {
    private MutableLiveData<String> currentProfileDBHash;
    public MutableLiveData<String> getCurrentProfileDBHash() {
        if (currentProfileDBHash == null) {
            currentProfileDBHash = new MutableLiveData<String>();
        }
        return currentProfileDBHash;
    }
}