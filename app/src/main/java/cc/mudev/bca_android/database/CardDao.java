package cc.mudev.bca_android.database;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CardDao {
    @Query("SELECT * FROM TB_CARD")
    List<TB_CARD> getAll();

    @Query("SELECT * FROM TB_CARD WHERE uuid IN (:card_id)")
    TB_CARD loadByCardID(int card_id);

    @Query("SELECT * FROM TB_CARD WHERE profile_id IN (:profile_id)")
    List<TB_CARD> loadByProfileID(int profile_id);
}