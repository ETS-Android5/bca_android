package cc.mudev.bca_android.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ChatParticipantDao {
    @Query("SELECT profile_name FROM TB_CHAT_PARTICIPANT WHERE room_id = (:roomId) AND uuid = (:participantId)")
    String getProfileNameUsingParticipantID(int roomId, int participantId);

    @Query("SELECT profile_name FROM TB_CHAT_PARTICIPANT WHERE room_id = (:roomId) AND profile_id = (:profileId)")
    String getProfileNameUsingProfileID(int roomId, int profileId);

    @Query("SELECT room_name FROM TB_CHAT_PARTICIPANT WHERE room_id = (:roomId) AND profile_id = (:profileId)")
    String getRoomNameUsingProfileID(int roomId, int profileId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertParticipant(TB_CHAT_PARTICIPANT participant);
}
