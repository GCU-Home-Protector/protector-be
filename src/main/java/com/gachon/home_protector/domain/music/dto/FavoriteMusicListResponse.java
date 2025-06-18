package com.gachon.home_protector.domain.music.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FavoriteMusicListResponse {

    private Long songId;
    private String songName;
    private String songUrl;

    public FavoriteMusicListResponse(Long songId, String songName, String songUrl) {
        this.songId = songId;
        this.songName = songName;
        this.songUrl = songUrl;
    }
}
