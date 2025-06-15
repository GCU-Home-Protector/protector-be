package com.gachon.home_protector.music.dto.recommend;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MusicRecommendServiceRequest {

    private String encodedFaceImage;

    public MusicRecommendServiceRequest(String encodedFaceImage) {
        this.encodedFaceImage = encodedFaceImage;
    }
}
