package cc.mudev.bca_android.database;

import static androidx.room.ForeignKey.NO_ACTION;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "TB_CHAT_PARTICIPANT",
        foreignKeys = @ForeignKey(
                entity = TB_CHAT_ROOM.class,
                parentColumns = "uuid",
                childColumns = "room_id",
                onDelete = NO_ACTION))
public class TB_CHAT_PARTICIPANT {
    @PrimaryKey(autoGenerate = false)
    public int uuid;

    @NonNull
    public int room_id;
    @NonNull
    public String room_name;

    @NonNull
    public int profile_id;
    @NonNull
    public String profile_name;

    @NonNull
    public String commit_id;
    @NonNull
    public int created_at;
    @NonNull
    public int modified_at;

    public TB_CHAT_PARTICIPANT(
            int uuid,
            int room_id, String room_name,
            int profile_id, String profile_name,
            String commit_id, int created_at, int modified_at
    ) {
        this.uuid = uuid;

        this.room_id = room_id;
        this.room_name = room_name;

        this.profile_id = profile_id;
        this.profile_name = profile_name;

        this.commit_id = commit_id;
        this.created_at = created_at;
        this.modified_at = modified_at;
    }
}
