package com.gachon.home_protector.music;

import com.gachon.home_protector.IntegrationTestSupport;
import com.gachon.home_protector.favorite_music.FavoriteMusic;
import com.gachon.home_protector.favorite_music.FavoriteMusicRepository;
import com.gachon.home_protector.user.User;
import com.gachon.home_protector.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class MusicRepositoryTest extends IntegrationTestSupport {

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

    @DisplayName("특정 유저의 좋아요 음악 리스트를 가져올 수 있다.")
    @Test
    void findFavoriteMusicByUserId() {
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

    @DisplayName("특정 유저가 음악에 좋아요를 누르지 않았을 수 있다.")
    @Test
    void findFavoriteMusicByUserId_FAVORITE_NOT_FOUND() {
        // given
        User user1 = User.createRestLoginUser("userId", "password", "role");
        User user2 = User.createRestLoginUser("userId2", "password2", "role");
        List<User> users = userRepository.saveAll(List.of(user1, user2));

        Music music1 = Music.of("title1", "url1");
        Music music2 = Music.of("title2", "url2");
        List<Music> musics = musicRepository.saveAll(List.of(music1, music2));

        FavoriteMusic f1 = FavoriteMusic.of(users.get(0), musics.get(0));
        FavoriteMusic f2 = FavoriteMusic.of(users.get(0), musics.get(1));

        favoriteMusicRepository.saveAll(List.of(f1, f2));

        // when
        List<Music> result = musicRepository.findFavoriteMusicByUserId(users.get(1).getId());

        // then
        assertThat(result).isEmpty();
    }
}