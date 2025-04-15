package com.gachon.home_protector.music.dto.recommend;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MusicRecommendResponse {

    private Long songId;
    private String recommendSong;
    private String recommendSongUrl;

    public MusicRecommendResponse(Long songId, String recommendSong, String recommendSongUrl) {
        this.songId = songId;
        this.recommendSong = recommendSong;
        this.recommendSongUrl = recommendSongUrl;
    }
}
