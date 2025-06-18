package com.gachon.home_protector.domain.favorite_music;

import com.gachon.home_protector.domain.common.BaseEntity;
import com.gachon.home_protector.domain.music.Music;
import com.gachon.home_protector.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteMusic extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_id", nullable = false)
    private Music music;

    @Builder
    private FavoriteMusic(User user, Music music) {
        this.user = user;
        this.music = music;
    }

    public static FavoriteMusic of(User user, Music music) {
        return FavoriteMusic.builder()
                .user(user)
                .music(music)
                .build();
    }
}
