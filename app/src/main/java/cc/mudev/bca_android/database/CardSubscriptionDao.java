package cc.mudev.bca_android.database;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CardSubscriptionDao {
    @Query("SELECT * FROM TB_CARD_SUBSCRIPTION")
    List<TB_CARD_SUBSCRIPTION> getAll();

    @Query("SELECT * FROM TB_CARD_SUBSCRIPTION WHERE subscribed_profile_id IN (:profile_id)")
    List<TB_CARD_SUBSCRIPTION> loadAllByProfileID(int profile_id);
}