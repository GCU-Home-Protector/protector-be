package com.gachon.home_protector.music.dto.recommend;

import com.gachon.home_protector.music.Music;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MusicRecommendResponseFromAI {

    private String recommendSong;

    private String recommendSongUrl;

    public MusicRecommendResponseFromAI(String recommendSong, String recommendSongUrl) {
        this.recommendSong = recommendSong;
        this.recommendSongUrl = recommendSongUrl;
    }

    public Music toMusic() {
        return Music.of(recommendSong, recommendSongUrl);
    }
}
