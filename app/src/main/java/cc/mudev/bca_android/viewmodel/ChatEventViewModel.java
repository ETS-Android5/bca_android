package cc.mudev.bca_android.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import cc.mudev.bca_android.database.TB_CHAT_EVENT;
import cc.mudev.bca_android.repository.ChatEventRepository;

public class ChatEventViewModel extends AndroidViewModel {
    private final ChatEventRepository repository;
    LiveData<List<TB_CHAT_EVENT>> roomEvents;

    public ChatEventViewModel(Application application, int roomId) {
        super(application);
        this.repository = new ChatEventRepository(application, roomId);
        this.roomEvents = this.repository.getAllRoomEvents();
    }

    public LiveData<List<TB_CHAT_EVENT>> getAllRoomEvents() {
        return roomEvents;
    }

    void insertEvent(TB_CHAT_EVENT event) {
        repository.insert(event);
    }
}
