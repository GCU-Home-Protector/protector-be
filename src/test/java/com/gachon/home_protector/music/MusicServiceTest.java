package com.gachon.home_protector.music;

import com.gachon.home_protector.IntegrationTestSupport;
import com.gachon.home_protector.domain.favorite_music.FavoriteMusic;
import com.gachon.home_protector.domain.favorite_music.FavoriteMusicRepository;
import com.gachon.home_protector.domain.music.Music;
import com.gachon.home_protector.domain.music.MusicRepository;
import com.gachon.home_protector.domain.music.MusicService;
import com.gachon.home_protector.domain.music.dto.AddFavoriteMusicServiceRequest;
import com.gachon.home_protector.domain.music.dto.FavoriteMusicListResponse;
import com.gachon.home_protector.domain.music.exception.FavoriteMusicNotFoundException;
import com.gachon.home_protector.domain.security.userdetails.RestUserDetails;
import com.gachon.home_protector.domain.user.User;
import com.gachon.home_protector.domain.user.UserRepository;
import com.gachon.home_protector.domain.user.dto.login.RestUserLoginResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MusicServiceTest extends IntegrationTestSupport {

    @Autowired
    MusicRepository musicRepository;

    @Autowired
    FavoriteMusicRepository favoriteMusicRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MusicService musicService;


    @AfterEach
    void tearDown() {
        favoriteMusicRepository.deleteAllInBatch();
        musicRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("특정 사용자의 좋아요 음악 리스트를 가져올 수 있다.")
    @Test
    void getFavoriteMusicList() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "role";

        User user = User.createRestLoginUser(userId, password, role);
        User user2 = User.createRestLoginUser("userId2", "password2", "role");
        List<User> users = userRepository.saveAll(List.of(user, user2));

        Long id = user.getId();
        RestUserLoginResponse response = new RestUserLoginResponse(id, userId, password, role);
        response.removePassword();
        RestUserDetails restUserDetails = new RestUserDetails(response);

        Music music1 = Music.of("title1", "url1");
        Music music2 = Music.of("title2", "url2");
        List<Music> musics = musicRepository.saveAll(List.of(music1, music2));

        FavoriteMusic f1 = FavoriteMusic.of(users.get(0), musics.get(0));
        FavoriteMusic f2 = FavoriteMusic.of(users.get(0), musics.get(1));
        FavoriteMusic f3 = FavoriteMusic.of(users.get(1), musics.get(0));
        favoriteMusicRepository.saveAll(List.of(f1, f2, f3));

        // when
        List<FavoriteMusicListResponse> result = musicService.getFavoriteMusicList(restUserDetails);

        // then
        assertThat(result).hasSize(2)
                .extracting("songId", "songName", "songUrl")
                .containsExactlyInAnyOrder(
                        tuple(musics.get(0).getId(), "title1", "url1"),
                        tuple(musics.get(1).getId(), "title2", "url2")
                );
    }

    @DisplayName("특정 사용자의 좋아요 음악 리스트가 없을 수 있다.")
    @Test
    void getFavoriteMusicList_EMPTY_FAVORITE_MUSIC() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "role";

        User user = User.createRestLoginUser(userId, password, role);
        User user2 = User.createRestLoginUser("userId2", "password2", "role");
        List<User> users = userRepository.saveAll(List.of(user, user2));

        Long id = user.getId();
        RestUserLoginResponse response = new RestUserLoginResponse(id, userId, password, role);
        response.removePassword();
        RestUserDetails restUserDetails = new RestUserDetails(response);

        Music music1 = Music.of("title1", "url1");
        Music music2 = Music.of("title2", "url2");
        List<Music> musics = musicRepository.saveAll(List.of(music1, music2));


        FavoriteMusic f3 = FavoriteMusic.of(users.get(1), musics.get(0));
        favoriteMusicRepository.saveAll(List.of(f3));

        // when // then
        assertThatThrownBy(() -> musicService.getFavoriteMusicList(restUserDetails))
                .isInstanceOf(FavoriteMusicNotFoundException.class)
                .hasMessage("좋아요를 누른 음악이 없습니다!");

    }

    @DisplayName("특정 음악에 대해 좋아요를 누를 수 있다.")
    @Test
    void addFavoriteMusic_LIKE_NORMAL() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "role";

        User user = User.createRestLoginUser(userId, password, role);
        User user2 = User.createRestLoginUser("userId2", "password2", "role");
        userRepository.saveAll(List.of(user, user2));

        Long id = user.getId();
        RestUserLoginResponse response = new RestUserLoginResponse(id, userId, password, role);
        response.removePassword();
        RestUserDetails restUserDetails = new RestUserDetails(response);

        Music music1 = Music.of("title1", "url1");
        Music music2 = Music.of("title2", "url2");
        List<Music> musics = musicRepository.saveAll(List.of(music1, music2));

        AddFavoriteMusicServiceRequest request = new AddFavoriteMusicServiceRequest(musics.get(0).getId());

        // when
        String result = musicService.addFavoriteMusic(restUserDetails, request);

        // then
        assertThat(favoriteMusicRepository.findAll()).hasSize(1);
        assertThat(result).isEqualTo("좋아요를 눌렀습니다!");
    }

    @Disabled
    @DisplayName("특정 음악에 대해 좋아요를 취소할 수 있다.")
    @Test
    void addFavoriteMusic_LIKE_CANCEL_NORMAL() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "role";

        User user = User.createRestLoginUser(userId, password, role);
        User user2 = User.createRestLoginUser("userId2", "password2", "role");
        List<User> users = userRepository.saveAll(List.of(user, user2));

        Long id = user.getId();
        RestUserLoginResponse response = new RestUserLoginResponse(id, userId, password, role);
        response.removePassword();
        RestUserDetails restUserDetails = new RestUserDetails(response);

        Music music1 = Music.of("title1", "url1");
        Music music2 = Music.of("title2", "url2");
        List<Music> musics = musicRepository.saveAll(List.of(music1, music2));

        FavoriteMusic f1 = FavoriteMusic.of(users.get(0), musics.get(0));
        favoriteMusicRepository.saveAll(List.of(f1));

        AddFavoriteMusicServiceRequest request = new AddFavoriteMusicServiceRequest(musics.get(0).getId());

        // when
        String result = musicService.addFavoriteMusic(restUserDetails, request);

        // then
        assertThat(favoriteMusicRepository.findAll()).isEmpty();
        assertThat(result).isEqualTo("좋아요를 취소했습니다!");
    }

}
