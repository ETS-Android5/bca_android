package cc.mudev.bca_alter.database;

import static androidx.room.ForeignKey.NO_ACTION;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "TB_PROFILE_RELATION",
        foreignKeys = {
                @ForeignKey(
                        entity = cc.mudev.bca_alter.database.TB_PROFILE.class,
                        parentColumns = "uuid",
                        childColumns = "from_profile_id",
                        onDelete = NO_ACTION),
                @ForeignKey(
                        entity = cc.mudev.bca_alter.database.TB_PROFILE.class,
                        parentColumns = "uuid",
                        childColumns = "to_profile_id",
                        onDelete = NO_ACTION),
        })
public class TB_PROFILE_RELATION {
    @PrimaryKey(autoGenerate = false)
    public int uuid;

    @NonNull
    public String commit_id;
    public int created_at;
    public int modified_at;

    public int status;

    public int from_profile_id;
    public int to_profile_id;

    public TB_PROFILE_RELATION(
            int uuid,

            @NonNull String commit_id,
            int created_at,
            int modified_at,

            int status,

            int from_profile_id,
            int to_profile_id) {
        this.uuid = uuid;
        this.commit_id = commit_id;
        this.created_at = created_at;
        this.modified_at = modified_at;

        this.status = status;

        this.from_profile_id = from_profile_id;
        this.to_profile_id = to_profile_id;
    }
}
