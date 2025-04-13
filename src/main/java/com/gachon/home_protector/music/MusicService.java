package com.gachon.home_protector.music;

import com.gachon.home_protector.music.dto.recommend.RecommendMusicResponse;
import com.gachon.home_protector.music.dto.recommend.RecommendMusicServiceRequest;
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

    @Value("${ai.domain}")
    private String aiDomain;

    @Value("${ai.port}")
    private String aiPort;

    @Value("${ai.recommend.path}")
    private String aiPath;

    private final MusicRepository musicRepository;


    @Transactional
    public RecommendMusicResponse recommendMusic(RecommendMusicServiceRequest request) {

        RecommendMusicResponse response = recommendMusicFromAi(request);

        musicRepository.save(response.toMusic());
        return response;
    }

    private RecommendMusicResponse recommendMusicFromAi(RecommendMusicServiceRequest request) {
        RestClient restClient = RestClient.create();

        return restClient.post()
                .uri(String.format("%s:%s/%s", aiDomain, aiPort, aiPath))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    throw new IllegalArgumentException("잘못된 요청입니다!");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    throw new IllegalArgumentException("파이썬 내부에서 에러 발생했습니다!");
                })
                .body(RecommendMusicResponse.class);
    }
}
