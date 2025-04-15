package com.gachon.home_protector.music;

import com.gachon.home_protector.api.BaseEntity;
import com.gachon.home_protector.music.dto.FavoriteMusicListResponse;
import com.gachon.home_protector.music.dto.recommend.MusicRecommendResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Music extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recommendSong;

    private String recommendSongUrl;

    @Builder
    private Music(Long id, String recommendSong, String recommendSongUrl) {
        this.id = id;
        this.recommendSong = recommendSong;
        this.recommendSongUrl = recommendSongUrl;
    }

    public static Music of (String recommendSong, String recommendSongUrl) {
        return Music.builder()
                .recommendSong(recommendSong)
                .recommendSongUrl(recommendSongUrl)
                .build();
    }

    public MusicRecommendResponse toRecommendResponse() {
        return new MusicRecommendResponse(id, recommendSong, recommendSongUrl);
    }

    public FavoriteMusicListResponse toFavoriteMusicListResponse() {
        return new FavoriteMusicListResponse(id, recommendSong, recommendSongUrl);
    }
}
