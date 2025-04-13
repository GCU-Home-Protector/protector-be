package com.gachon.home_protector.music.dto.recommend;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MusicRecommendRequest {

    @NotBlank(message = "아기 얼굴은 필수입니다!")
    private String encodedFaceImage;

    public MusicRecommendRequest(String encodedFaceImage) {
        this.encodedFaceImage = encodedFaceImage;
    }

    public MusicRecommendServiceRequest toServiceRequest() {
        return new MusicRecommendServiceRequest(encodedFaceImage);
    }
}
