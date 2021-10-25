package cc.mudev.bca_android.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TB_CHAT_ROOM")
public class TB_CHAT_ROOM {
    @PrimaryKey(autoGenerate = false)
    public int uuid;

    @NonNull
    public String name;
    public String description;

    @NonNull
    public int created_by_profile_id;

    @NonNull
    public String commit_id;
    @NonNull
    public int created_at;
    @NonNull
    public int modified_at;
    public Integer deleted_at;

    @NonNull
    @ColumnInfo(name = "private")
    public int is_private;
    @NonNull
    public int encrypted;

    @NonNull
    public int last_seen_message_id;

    public TB_CHAT_ROOM(
            int uuid,
            String name, String description,
            int created_by_profile_id,
            String commit_id, int created_at, int modified_at, Integer deleted_at,
            int is_private, int encrypted
    ) {
        this.uuid = uuid;

        this.name = name;
        this.description = description;

        this.created_by_profile_id = created_by_profile_id;

        this.commit_id = commit_id;
        this.created_at = created_at;
        this.modified_at = modified_at;
        this.deleted_at = deleted_at;

        this.is_private = is_private;
        this.encrypted = encrypted;

        this.last_seen_message_id = 0;
    }
}
