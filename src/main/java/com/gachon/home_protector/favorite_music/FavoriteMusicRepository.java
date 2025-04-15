package com.gachon.home_protector.favorite_music;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FavoriteMusicRepository extends JpaRepository<FavoriteMusic, Long> {

    @Query("SELECT EXISTS (SELECT 1 FROM FavoriteMusic f WHERE f.user.id = :userId AND f.music.id = :songId)")
    boolean existsByUserAndMusic(Long userId, Long songId);
}
