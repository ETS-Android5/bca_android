package cc.mudev.bca_android.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import cc.mudev.bca_android.database.ChatDatabase;
import cc.mudev.bca_android.database.ChatEventDao;
import cc.mudev.bca_android.database.TB_CHAT_EVENT;

public class ChatEventRepository {
    public int roomId;
    private final ChatEventDao chatEventDao;
    private final LiveData<List<TB_CHAT_EVENT>> roomEvents;

    public ChatEventRepository(Application application, int roomId) {
        ChatDatabase chatDatabase = ChatDatabase.getInstance(application.getBaseContext());
        this.roomId = roomId;
        this.chatEventDao = chatDatabase.eventDao();
        this.roomEvents = this.chatEventDao.getAllRoomEventsLive(roomId);
    }

    public LiveData<List<TB_CHAT_EVENT>> getAllRoomEvents() {
        return this.roomEvents;
    }

    public void insert(TB_CHAT_EVENT event) {
        ChatDatabase.databaseWriteExecutor.execute(() -> {
            chatEventDao.insertEvent(event);
        });
    }
}
