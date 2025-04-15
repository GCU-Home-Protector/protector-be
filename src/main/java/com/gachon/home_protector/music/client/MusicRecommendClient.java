package com.gachon.home_protector.music.client;

import com.gachon.home_protector.music.dto.recommend.MusicRecommendResponse;
import com.gachon.home_protector.music.dto.recommend.MusicRecommendServiceRequest;
import com.gachon.home_protector.music.exception.ai.BadRequestFromAIException;
import com.gachon.home_protector.music.exception.ai.InternalServerErrorFromAIException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class MusicRecommendClient {

    @Value("${ai.domain}")
    private String aiDomain;

    @Value("${ai.port}")
    private String aiPort;

    @Value("${ai.recommend.path}")
    private String aiPath;

    private final RestClient restClient;

    public MusicRecommendResponse requestRecommendation(MusicRecommendServiceRequest request) {

        return restClient.post()
                .uri(String.format("http://%s:%s/%s", aiDomain, aiPort, aiPath))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()

                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                    log.error("AI 모듈에서 4xx 에러를 반환했습니다! : {} - {}", res.getStatusCode(), res.getStatusText());
                    throw new BadRequestFromAIException("잘못된 요청입니다!");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                    log.error("AI 모듈에서 5xx 에러를 반환했습니다! : {} - {}", res.getStatusCode(), res.getStatusText());
                    throw new InternalServerErrorFromAIException("AI 서버 내부에서 에러 발생했습니다!");
                })

                .body(MusicRecommendResponse.class);
    }
}
