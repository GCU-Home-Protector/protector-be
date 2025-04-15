package com.gachon.home_protector.music.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddFavoriteMusicServiceRequest {
    private Long songId;

    public AddFavoriteMusicServiceRequest(Long songId) {
        this.songId = songId;
    }
}
