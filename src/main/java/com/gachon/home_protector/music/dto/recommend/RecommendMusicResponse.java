package com.gachon.home_protector.music.dto.recommend;

import com.gachon.home_protector.music.Music;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecommendMusicResponse {

    private String recommenedSong;

    private String recommenedSongUrl;

    public RecommendMusicResponse(String recommenedSong, String recommenedSongUrl) {
        this.recommenedSong = recommenedSong;
        this.recommenedSongUrl = recommenedSongUrl;
    }

    public Music toMusic() {
        return Music.of(recommenedSong, recommenedSongUrl);
    }
}
