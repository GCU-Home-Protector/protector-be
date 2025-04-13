package com.gachon.home_protector.music;

import com.gachon.home_protector.api.BaseEntity;
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

    private String recommenedSong;

    private String recommenedSongUrl;

    @Builder
    private Music(String recommenedSong, String recommenedSongUrl) {
        this.recommenedSong = recommenedSong;
        this.recommenedSongUrl = recommenedSongUrl;
    }

    public static Music of (String recommenedSong, String recommenedSongUrl) {
        return Music.builder()
                .recommenedSong(recommenedSong)
                .recommenedSongUrl(recommenedSongUrl)
                .build();
    }
}
