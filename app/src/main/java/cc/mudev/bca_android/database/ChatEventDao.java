package cc.mudev.bca_android.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChatEventDao {
    @Query("SELECT * FROM TB_CHAT_EVENT WHERE room_id = (:roomId)")
    LiveData<List<TB_CHAT_EVENT>> getAllRoomEventsLive(int roomId);

    @Query("SELECT * FROM TB_CHAT_EVENT WHERE room_id = (:roomId) ORDER BY uuid")
    List<TB_CHAT_EVENT> getAllRoomEvents(int roomId);

    @Query("SELECT * FROM TB_CHAT_EVENT WHERE room_id = (:roomId) AND event_type = \"MESSAGE_POSTED\" ORDER BY uuid LIMIT 1")
    TB_CHAT_EVENT getLatestMessage(int roomId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEvent(TB_CHAT_EVENT event);
}
