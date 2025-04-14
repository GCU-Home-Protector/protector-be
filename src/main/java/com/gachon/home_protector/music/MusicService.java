package com.gachon.home_protector.music;

import com.gachon.home_protector.music.client.MusicRecommendClient;
import com.gachon.home_protector.music.dto.recommend.MusicRecommendResponse;
import com.gachon.home_protector.music.dto.recommend.MusicRecommendServiceRequest;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MusicService {


    private final MusicRecommendClient musicRecommendClient;

    private final MusicRepository musicRepository;


    @Transactional
    public MusicRecommendResponse recommendMusic(MusicRecommendServiceRequest request) {
        MusicRecommendResponse response = musicRecommendClient.requestRecommendation(request);
        Assert.notNull(response, "추천 결과가 없습니다!"); // IllegalArgumentException throw
        musicRepository.save(response.toMusic());
        return response;
    }
}
