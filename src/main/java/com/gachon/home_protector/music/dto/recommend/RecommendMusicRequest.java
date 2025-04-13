package com.gachon.home_protector.music.dto.recommend;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

@Getter
@RequiredArgsConstructor
public class RecommendMusicRequest {

    @NotBlank(message = "아기 얼굴은 필수입니다!")
    private String encodedFaceImage;

    public RecommendMusicRequest(String encodedFaceImage) {
        this.encodedFaceImage = encodedFaceImage;
    }

    public RecommendMusicServiceRequest toServiceRequest() {
        return new RecommendMusicServiceRequest(encodedFaceImage);
    }
}
