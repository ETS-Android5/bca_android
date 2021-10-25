package cc.mudev.bca_android.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChatRoomDao {
    @Query("SELECT * FROM TB_CHAT_ROOM WHERE uuid = (:roomId)")
    TB_CHAT_ROOM getChatRoomById(int roomId);

    @Query("SELECT * FROM TB_CHAT_ROOM WHERE uuid = (SELECT room_id FROM TB_CHAT_PARTICIPANT WHERE profile_id = (:profileId))")
    List<TB_CHAT_ROOM> getAllChatRoom(int profileId);

    @Query("SELECT COUNT(*) FROM TB_CHAT_EVENT WHERE room_id = (:roomId) AND event_type = \"MESSAGE_POSTED\" AND event_index > (SELECT last_seen_message_id FROM TB_CHAT_ROOM WHERE uuid = (:roomId))")
    int getUnreadMessageCountOnRoomById(int roomId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRoom(TB_CHAT_ROOM newRoom);

    @Query("UPDATE TB_CHAT_ROOM SET last_seen_message_id = (:messageId) WHERE uuid = (:roomId)")
    void updateLastSeenMessageId(int roomId, int messageId);
}
