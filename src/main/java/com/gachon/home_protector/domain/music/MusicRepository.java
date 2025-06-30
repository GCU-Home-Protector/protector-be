package com.gachon.home_protector.domain.music;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MusicRepository extends JpaRepository<Music, Long> {

    @Query("SELECT fm.music FROM FavoriteMusic fm WHERE fm.user.id = :userId")
    List<Music> findFavoriteMusicByUserId(@Param("userId") Long userId);
}
