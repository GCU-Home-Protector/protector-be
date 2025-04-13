package com.gachon.home_protector.music;

import com.gachon.home_protector.api.SuccessResponse;
import com.gachon.home_protector.music.dto.recommend.RecommendMusicRequest;
import com.gachon.home_protector.music.dto.recommend.RecommendMusicResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/music")
public class MusicController {

    private final MusicService musicService;

    @PostMapping
    public SuccessResponse<RecommendMusicResponse> recommendMusic(@Valid @RequestBody RecommendMusicRequest request) {
        return SuccessResponse.success(musicService.recommendMusic(request.toServiceRequest()));
    }
}
