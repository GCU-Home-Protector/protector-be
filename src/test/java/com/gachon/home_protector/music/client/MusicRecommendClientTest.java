package com.gachon.home_protector.music.client;
//
//import com.gachon.home_protector.config.RestClientConfig;
//import com.gachon.home_protector.music.dto.recommend.MusicRecommendResponse;
//import com.gachon.home_protector.music.dto.recommend.MusicRecommendServiceRequest;
//import com.gachon.home_protector.music.exception.ai.BadRequestFromAIException;
//import com.gachon.home_protector.music.exception.ai.InternalServerErrorFromAIException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.client.MockRestServiceServer;
//import org.springframework.web.client.RestClient;
//import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
//import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
//import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
//import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
//
//@ActiveProfiles("test")
//@RestClientTest(MusicRecommendClient.class)
////@Import(RestClientConfig.class)
//class MusicRecommendClientTest {
//
//    @Value("${ai.domain}")
//    private String aiDomain;
//
//    @Value("${ai.port}")
//    private String aiPort;
//
//    @Value("${ai.recommend.path}")
//    private String aiPath;
//
//    @Autowired
//    private RestClient restClient;
//
//    @Autowired
//    private MusicRecommendClient musicRecommendClient;
//
//    @Autowired
//    private MockRestServiceServer mockServer;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
////    @BeforeEach
////    void setUp() {
////        mockServer = MockRestServiceServer.bindTo(restClient).build();
////    }
//
//    @DisplayName("AI 서버와 통신할 수 있다.")
//    @Test
//    void requestRecommendation() throws JsonProcessingException {
//        // given
//        String url = String.format("http://%s:%s/%s", aiDomain, aiPort, aiPath);
//
//        String encodedImage = "encodedImage";
//        String recommendSong = "recommendSong";
//        String recommendSongUrl = "recommendSongUrl";
//
//        MusicRecommendServiceRequest request = new MusicRecommendServiceRequest(encodedImage);
//        MusicRecommendResponse response = new MusicRecommendResponse(recommendSong, recommendSongUrl);
//
//        mockServer.expect(requestTo(url))
//                .andExpect(method(HttpMethod.POST))
//                .andRespond(withSuccess(objectMapper.writeValueAsString(response), MediaType.APPLICATION_JSON));
//
//        // when
//        MusicRecommendResponse result = musicRecommendClient.requestRecommendation(request);
//
//        // then
//        assertThat(result).extracting("recommendSong", "recommendSongUrl")
//                .containsExactly(recommendSong, recommendSongUrl);
//    }
//
//    @DisplayName("AI 서버에 잘못된 요청을 날릴 수도 있다.")
//    @Test
//    void requestRecommendation_4xx() {
//        // given
//        String url = String.format("http://%s:%s/%s", aiDomain, aiPort, aiPath);
//
//        String encodedImage = "encodedImage";
//
//        MusicRecommendServiceRequest request = new MusicRecommendServiceRequest(encodedImage);
//
//        mockServer.expect(requestTo(url))
//                .andExpect(method(HttpMethod.POST))
//                .andRespond(withStatus(HttpStatus.BAD_REQUEST));
//
//        // when // then
//        assertThatThrownBy(() -> musicRecommendClient.requestRecommendation(request))
//                .isInstanceOf(BadRequestFromAIException.class)
//                .hasMessage("잘못된 요청입니다!");
//    }
//
//    @DisplayName("AI 서버에서 에러가 발생할 수 있다.")
//    @Test
//    void requestRecommendation_5xx() {
//        // given
//        String url = String.format("http://%s:%s/%s", aiDomain, aiPort, aiPath);
//
//        String encodedImage = "encodedImage";
//
//        MusicRecommendServiceRequest request = new MusicRecommendServiceRequest(encodedImage);
//
//        mockServer.expect(requestTo(url))
//                .andExpect(method(HttpMethod.POST))
//                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
//
//        // when // then
//        assertThatThrownBy(() -> musicRecommendClient.requestRecommendation(request))
//                .isInstanceOf(InternalServerErrorFromAIException.class)
//                .hasMessage("AI 서버 내부에서 에러 발생했습니다!");
//    }
//}