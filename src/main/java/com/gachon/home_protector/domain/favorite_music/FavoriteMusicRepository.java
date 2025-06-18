package com.gachon.home_protector.domain.favorite_music;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FavoriteMusicRepository extends JpaRepository<FavoriteMusic, Long> {

    @Query("SELECT f FROM FavoriteMusic f WHERE f.user.id = :userId AND f.music.id = :songId")
    Optional<FavoriteMusic> findByUserAndMusicId(Long userId, Long songId);
}
