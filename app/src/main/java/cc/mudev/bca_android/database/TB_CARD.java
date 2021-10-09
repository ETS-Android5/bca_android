package cc.mudev.bca_android.database;

import static androidx.room.ForeignKey.NO_ACTION;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "TB_CARD",
        foreignKeys = @ForeignKey(
                entity = TB_PROFILE.class,
                parentColumns = "uuid",
                childColumns = "profile_id",
                onDelete = NO_ACTION))
public class TB_CARD {

    @PrimaryKey(autoGenerate = false)
    public int uuid;

    @NonNull
    public String name;
    @NonNull
    public String data;
    @NonNull
    public String preview_url;

    @NonNull
    public String commit_id;
    public int created_at;
    public int modified_at;
    public Integer deleted_at;
    public String why_deleted;

    @ColumnInfo(name = "private")
    public int is_private;

    public int profile_id;

    public TB_CARD(
            int uuid,
            @NonNull String name,
            @NonNull String data,
            @NonNull String preview_url,

            @NonNull String commit_id,
            int created_at,
            int modified_at,
            Integer deleted_at,
            String why_deleted,

            int is_private,
            int profile_id) {
        this.uuid = uuid;
        this.profile_id = profile_id;
        this.name = name;
        this.data = data;
        this.preview_url = preview_url;
        this.deleted_at = deleted_at;
        this.why_deleted = why_deleted;
        this.is_private = is_private;
        this.created_at = created_at;
        this.modified_at = modified_at;
        this.commit_id = commit_id;
    }
}