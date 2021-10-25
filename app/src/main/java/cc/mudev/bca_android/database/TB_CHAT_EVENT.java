package cc.mudev.bca_android.database;

import static androidx.room.ForeignKey.NO_ACTION;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "TB_CHAT_EVENT",
        foreignKeys = {
                @ForeignKey(
                        entity = TB_CHAT_ROOM.class,
                        parentColumns = "uuid",
                        childColumns = "room_id",
                        onDelete = NO_ACTION),
                @ForeignKey(
                        entity = TB_CHAT_PARTICIPANT.class,
                        parentColumns = "uuid",
                        childColumns = "caused_by_participant_id",
                        onDelete = NO_ACTION),
        })
public class TB_CHAT_EVENT {
    @PrimaryKey(autoGenerate = false)
    public int uuid;

    @NonNull
    public int event_index;
    @NonNull
    public String event_type;

    @NonNull
    public int room_id;
    @NonNull
    public String message;

    @NonNull
    public int caused_by_profile_id;
    @NonNull
    public int caused_by_participant_id;

    @NonNull
    public String commit_id;
    @NonNull
    public int created_at;
    @NonNull
    public int modified_at;

    @NonNull
    public int encrypted;

    public TB_CHAT_EVENT(
            int uuid,
            int event_index, String event_type,
            int room_id, String message,
            int caused_by_profile_id, int caused_by_participant_id,
            String commit_id, int created_at, int modified_at,
            int encrypted
    ) {
        this.uuid = uuid;
        this.event_index = event_index;
        this.event_type = event_type;

        this.room_id = room_id;
        this.message = message;

        this.caused_by_profile_id = caused_by_profile_id;
        this.caused_by_participant_id = caused_by_participant_id;

        this.commit_id = commit_id;
        this.created_at = created_at;
        this.modified_at = modified_at;

        this.encrypted = encrypted;
    }

    public TB_CHAT_EVENT_TYPE getEventTypeAsEnum() {
        return TB_CHAT_EVENT_TYPE.valueOf(this.event_type);
    }
}
