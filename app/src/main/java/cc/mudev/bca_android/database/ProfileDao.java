package cc.mudev.bca_android.database;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ProfileDao {
    @Query("SELECT * FROM TB_PROFILE")
    List<TB_PROFILE> getAll();

    @Query("SELECT * FROM TB_PROFILE WHERE uuid IN (:uuid)")
    List<TB_PROFILE> loadAllByUUID(int[] uuid);

    @Query("SELECT * FROM TB_PROFILE WHERE name LIKE '%' || :name || '%' LIMIT 1")
    TB_PROFILE findByName(String name);

    @Query("SELECT * FROM TB_PROFILE WHERE uuid IN (:profile_id)")
    List<TB_PROFILE> loadByProfileID(List<Integer> profile_id); //리스트 파라미터면 리스트로 반환

    @Query("SELECT * FROM TB_PROFILE WHERE uuid IN (:profile_id)")
    TB_PROFILE loadByProfileID(Integer profile_id); //단일 파라미터면 단일로 반환
}