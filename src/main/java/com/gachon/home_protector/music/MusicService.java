package com.gachon.home_protector.music;

import com.gachon.home_protector.api.MusicAssert;
import com.gachon.home_protector.music.client.MusicRecommendClient;
import com.gachon.home_protector.music.dto.recommend.MusicRecommendResponse;
import com.gachon.home_protector.music.dto.recommend.MusicRecommendServiceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MusicService {

    private final MusicRepository musicRepository;
    private final MusicRecommendClient musicRecommendClient;

    @Transactional
    public MusicRecommendResponse recommendMusic(MusicRecommendServiceRequest request) {
        MusicRecommendResponse response = musicRecommendClient.requestRecommendation(request);
        MusicAssert.validateMusicRecommendation(response, "추천 결과가 없습니다!"); // IllegalArgumentException throw
        musicRepository.save(response.toMusic());
        return response;
    }
}
