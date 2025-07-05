package com.gachon.home_protector.domain.music.facade;

import com.gachon.home_protector.domain.music.MusicService;
import com.gachon.home_protector.domain.music.client.MusicRecommendClient;
import com.gachon.home_protector.domain.music.dto.recommend.MusicRecommendResponse;
import com.gachon.home_protector.domain.music.dto.recommend.MusicRecommendResponseFromAI;
import com.gachon.home_protector.domain.music.dto.recommend.MusicRecommendServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MusicFacade {

    private final MusicService musicService;
    private final MusicRecommendClient musicRecommendClient;

    public MusicRecommendResponse recommendMusic (MusicRecommendServiceRequest request) {
        MusicRecommendResponseFromAI aiResponse = musicRecommendClient.requestRecommendation(request);
        return musicService.updateMusic(aiResponse);
    }
}
