package com.gachon.home_protector.music.dto.recommend;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecommendMusicServiceRequest {

    private String encodedImage;

    public RecommendMusicServiceRequest(String encodedImage) {
        this.encodedImage = encodedImage;
    }
}
