package com.gachon.home_protector.music;

import com.gachon.home_protector.MockTestSupport;
import com.gachon.home_protector.music.dto.recommend.MusicRecommendResponse;
import com.gachon.home_protector.music.dto.recommend.MusicRecommendResponseFromAI;
import com.gachon.home_protector.music.dto.recommend.MusicRecommendServiceRequest;
import com.gachon.home_protector.music.exception.ai.MusicNotRecommendException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;

class MusicServiceAboutRestClientTest extends MockTestSupport {


    @DisplayName("AI에게서 음악을 추천받을 수 있다.")
    @Test
    void recommendMusic() {
        // given
        String recommendSong = "recommendSong";
        String recommendSongUrl = "recommendSongUrl";

        MusicRecommendServiceRequest request = new MusicRecommendServiceRequest("encodedImage");
        MusicRecommendResponseFromAI response = new MusicRecommendResponseFromAI(recommendSong, recommendSongUrl);

        given(musicRecommendClient.requestRecommendation(any(MusicRecommendServiceRequest.class))).willReturn(response);
        given(musicRepository.save(any(Music.class))).willReturn(createMusicEntityForTest(recommendSong, recommendSongUrl));

        // when
        MusicRecommendResponse result = musicService.recommendMusic(request);

        // then
        verify(musicRepository).save(any(Music.class));
        assertThat(result).extracting("songId", "recommendSong", "recommendSongUrl")
                .containsExactly(1L, recommendSong, recommendSongUrl);
    }

    @DisplayName("AI에게서 받은 결과 중 음악 제목만 받을 수 있다.")
    @Test
    void recommendMusic_EMPTY_URL() {
        // given
        String recommendSong = "recommendSong";
        String recommendSongUrl = null;

        MusicRecommendServiceRequest request = new MusicRecommendServiceRequest("encodedImage");
        MusicRecommendResponseFromAI response = new MusicRecommendResponseFromAI(recommendSong, recommendSongUrl);

        given(musicRecommendClient.requestRecommendation(any(MusicRecommendServiceRequest.class))).willReturn(response);

        // when // then
        assertThatThrownBy(() -> musicService.recommendMusic(request))
                .isInstanceOf(MusicNotRecommendException.class)
                .hasMessage("추천 결과가 없습니다!");
    }

    @DisplayName("AI에게서 받은 결과 중 음악 URL만 받을 수 있다.")
    @Test
    void recommendMusic_EMPTY_TITLE() {
        // given
        String recommendSong = null;
        String recommendSongUrl = "recommendSongUrl";

        MusicRecommendServiceRequest request = new MusicRecommendServiceRequest("encodedImage");
        MusicRecommendResponseFromAI response = new MusicRecommendResponseFromAI(recommendSong, recommendSongUrl);

        given(musicRecommendClient.requestRecommendation(any(MusicRecommendServiceRequest.class))).willReturn(response);

        // when // then
        assertThatThrownBy(() -> musicService.recommendMusic(request))
                .isInstanceOf(MusicNotRecommendException.class)
                .hasMessage("추천 결과가 없습니다!");
    }

    @DisplayName("AI에게서 받은 추천 음악이 없을 수 있다.")
    @Test
    void recommendMusic_EMPTY_RECOMMEND() {
        // given
        MusicRecommendServiceRequest request = new MusicRecommendServiceRequest("encodedImage");

        given(musicRecommendClient.requestRecommendation(any(MusicRecommendServiceRequest.class))).willReturn(null);

        // when // then
        assertThatThrownBy(() -> musicService.recommendMusic(request))
                .isInstanceOf(MusicNotRecommendException.class)
                .hasMessage("추천 결과가 없습니다!");
    }

    public static Music createMusicEntityForTest(String recommendSong, String recommendSongUrl) {
        return Music.builder()
                .id(1L)
                .recommendSong(recommendSong)
                .recommendSongUrl(recommendSongUrl)
                .build();
    }
}