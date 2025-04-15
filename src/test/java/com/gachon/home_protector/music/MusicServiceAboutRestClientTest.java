package com.gachon.home_protector.music;

import com.gachon.home_protector.MockTestSupport;
import com.gachon.home_protector.favorite_music.FavoriteMusic;
import com.gachon.home_protector.favorite_music.FavoriteMusicRepository;
import com.gachon.home_protector.music.dto.recommend.MusicRecommendResponse;
import com.gachon.home_protector.music.dto.recommend.MusicRecommendResponseFromAI;
import com.gachon.home_protector.music.dto.recommend.MusicRecommendServiceRequest;
import com.gachon.home_protector.music.exception.ai.MusicNotRecommendException;
import com.gachon.home_protector.user.User;
import com.gachon.home_protector.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;

class MusicServiceAboutRestClientTest extends MockTestSupport {

    @Autowired
    MusicRepository musicRepository;

    @Autowired
    FavoriteMusicRepository favoriteMusicRepository;

    @Autowired
    UserRepository userRepository;


    @AfterEach
    void tearDown() {
        favoriteMusicRepository.deleteAllInBatch();
        musicRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }
    
    
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
    
    @DisplayName("특정 사용자의 좋아요 음악 리스트를 가져올 수 있다.")
    @Test
    void getFavoriteMusicList() {
        // given
        User user1 = User.createRestLoginUser("userId", "password", "role");
        User user2 = User.createRestLoginUser("userId2", "password2", "role");
        List<User> users = userRepository.saveAll(List.of(user1, user2));

        Music music1 = Music.of("title1", "url1");
        Music music2 = Music.of("title2", "url2");
        List<Music> musics = musicRepository.saveAll(List.of(music1, music2));

        FavoriteMusic f1 = FavoriteMusic.of(users.get(0), musics.get(0));
        FavoriteMusic f2 = FavoriteMusic.of(users.get(0), musics.get(1));
        FavoriteMusic f3 = FavoriteMusic.of(users.get(1), musics.get(0));
        favoriteMusicRepository.saveAll(List.of(f1, f2, f3));

        // when
        List<Music> result = musicRepository.findFavoriteMusicByUserId(users.get(0).getId());

        // then
        assertThat(result).hasSize(2)
                .extracting("recommendSong", "recommendSongUrl")
                .containsExactlyInAnyOrder(
                        tuple("title1", "url1"),
                        tuple("title2", "url2")
                );
    }
}