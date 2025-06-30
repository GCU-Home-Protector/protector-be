package com.gachon.home_protector.favorite_music;

import com.gachon.home_protector.IntegrationTestSupport;
import com.gachon.home_protector.domain.favorite_music.FavoriteMusic;
import com.gachon.home_protector.domain.favorite_music.FavoriteMusicRepository;
import com.gachon.home_protector.domain.music.Music;
import com.gachon.home_protector.domain.music.MusicRepository;
import com.gachon.home_protector.domain.user.User;
import com.gachon.home_protector.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


class FavoriteMusicRepositoryTest extends IntegrationTestSupport {

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

    @DisplayName("특정 유저가 특정 음악에 대해 좋아요를 눌렀는지 확인할 수 있다.")
    @Test
    void addOrDeleteFavoriteMusic() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "role";

        User user = User.createRestLoginUser(userId, password, role);
        User user2 = User.createRestLoginUser("userId2", "password2", "role");
        List<User> users = userRepository.saveAll(List.of(user, user2));

        Music music1 = Music.of("title1", "url1");
        Music music2 = Music.of("title2", "url2");
        List<Music> musics = musicRepository.saveAll(List.of(music1, music2));

        FavoriteMusic f1 = FavoriteMusic.of(users.get(0), musics.get(0));
        FavoriteMusic f2 = FavoriteMusic.of(users.get(0), musics.get(1));
        FavoriteMusic f3 = FavoriteMusic.of(users.get(1), musics.get(0));
        favoriteMusicRepository.saveAll(List.of(f1, f2, f3));

        // when
        Optional<FavoriteMusic> result = favoriteMusicRepository.findByUserAndMusicId(users.get(0).getId(), musics.get(0).getId());

        // then
        assertThat(result).isPresent();
    }

    @DisplayName("특정 유저가 특정 음악에 대해 좋아요를 안 눌렀는지 확인할 수 있다.")
    @Test
    void addOrDeleteFavoriteMusic_NOT_FAVORITE() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "role";

        User user = User.createRestLoginUser(userId, password, role);
        User user2 = User.createRestLoginUser("userId2", "password2", "role");
        List<User> users = userRepository.saveAll(List.of(user, user2));

        Music music1 = Music.of("title1", "url1");
        Music music2 = Music.of("title2", "url2");
        List<Music> musics = musicRepository.saveAll(List.of(music1, music2));

        FavoriteMusic f1 = FavoriteMusic.of(users.get(0), musics.get(0));
        FavoriteMusic f2 = FavoriteMusic.of(users.get(0), musics.get(1));
        FavoriteMusic f3 = FavoriteMusic.of(users.get(1), musics.get(0));
        favoriteMusicRepository.saveAll(List.of(f1, f2, f3));

        // when
        Optional<FavoriteMusic> result = favoriteMusicRepository.findByUserAndMusicId(users.get(1).getId(), musics.get(1).getId());

        // then
        assertThat(result).isEmpty();
    }
}
