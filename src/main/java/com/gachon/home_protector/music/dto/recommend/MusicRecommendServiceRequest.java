package com.gachon.home_protector.music.dto.recommend;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MusicRecommendServiceRequest {

    private String encodedImage;

    public MusicRecommendServiceRequest(String encodedImage) {
        this.encodedImage = encodedImage;
    }
}
