package cc.mudev.bca_android.database;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ProfileRelationDao {
    @Query("SELECT * FROM TB_PROFILE_RELATION")
    List<TB_PROFILE_RELATION> getAll();

    @Query("SELECT * FROM TB_PROFILE_RELATION WHERE from_profile_id IN (:profile_id)")
    List<TB_PROFILE_RELATION> getAllFollowingProfileRelations(int profile_id);

    @Query("SELECT DISTINCT to_profile_id FROM TB_PROFILE_RELATION WHERE from_profile_id IN (:profile_id)")
    List<Integer> getAllFollowingProfileId(int profile_id);
}