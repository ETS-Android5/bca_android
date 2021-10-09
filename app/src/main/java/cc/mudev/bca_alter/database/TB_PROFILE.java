package cc.mudev.bca_alter.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "TB_PROFILE")
public class TB_PROFILE {
    @PrimaryKey(autoGenerate = false)
    public int uuid;

    @NonNull
    public String name;
    public String description;
    @NonNull
    public String data;

    public String email;
    public String phone;
    public String sns;
    public String address;

    @NonNull
    public String commit_id;
    @NonNull
    public int created_at;
    @NonNull
    public int modified_at;

    public Integer deleted_at;
    public String why_deleted;

    @ColumnInfo(name = "private")
    @NonNull
    public int is_private;

    public TB_PROFILE(
            int uuid,
            @NonNull String name,
            String description,
            @NonNull String data,

            String email,
            String phone,
            String sns,
            String address,

            @NonNull String commit_id,
            int created_at,
            int modified_at,
            Integer deleted_at,
            String why_deleted,
            int is_private) {
        this.uuid = uuid;

        this.name = name;
        this.description = description;
        this.data = data;

        this.email = email;
        this.phone = phone;
        this.sns = sns;
        this.address = address;

        this.commit_id = commit_id;
        this.created_at = created_at;
        this.modified_at = modified_at;
        this.deleted_at = deleted_at;
        this.why_deleted = why_deleted;

        this.is_private = is_private;
    }
}