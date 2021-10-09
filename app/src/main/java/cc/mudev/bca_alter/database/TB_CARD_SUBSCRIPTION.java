package cc.mudev.bca_alter.database;

import static androidx.room.ForeignKey.NO_ACTION;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "TB_CARD_SUBSCRIPTION",
        foreignKeys = {
            @ForeignKey(
                    entity = cc.mudev.bca_alter.database.TB_PROFILE.class,
                    parentColumns = "uuid",
                    childColumns = "card_profile_id",
                    onDelete = NO_ACTION),
            @ForeignKey(
                    entity = cc.mudev.bca_alter.database.TB_CARD.class,
                    parentColumns = "uuid",
                    childColumns = "card_id",
                    onDelete = NO_ACTION),
            @ForeignKey(
                    entity = cc.mudev.bca_alter.database.TB_PROFILE.class,
                    parentColumns = "uuid",
                    childColumns = "subscribed_profile_id",
                    onDelete = NO_ACTION),
})
public class TB_CARD_SUBSCRIPTION {
    @PrimaryKey(autoGenerate = false)
    public int uuid;

    @NonNull
    public String commit_id;
    public int created_at;
    public int modified_at;

    public int card_profile_id;
    public int card_id;


    @ColumnInfo(name = "subscribed_profile_id")
    @NonNull
    public int subscribed_profile_id;

    public TB_CARD_SUBSCRIPTION(
            int uuid,
            @NonNull String commit_id,
            int created_at,
            int modified_at,
            int card_profile_id,
            int card_id,
            int subscribed_profile_id) {
        this.uuid = uuid;
        this.commit_id = commit_id;
        this.created_at = created_at;
        this.modified_at = modified_at;
        this.card_profile_id = card_profile_id;
        this.card_id = card_id;
        this.subscribed_profile_id = subscribed_profile_id;
    }
}
