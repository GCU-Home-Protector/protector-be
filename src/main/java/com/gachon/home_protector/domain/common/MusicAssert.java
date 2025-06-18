package com.gachon.home_protector.domain.common;

import com.gachon.home_protector.domain.music.Music;
import com.gachon.home_protector.domain.music.dto.recommend.MusicRecommendResponseFromAI;
import com.gachon.home_protector.domain.music.exception.FavoriteMusicNotFoundException;
import com.gachon.home_protector.domain.music.exception.ai.MusicNotRecommendException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.List;

public class MusicAssert extends Assert {

    public static void validateMusicRecommendation(MusicRecommendResponseFromAI response, String message) {
        if (response == null || StringUtils.isEmpty(response.getRecommendSong()) || StringUtils.isEmpty(response.getRecommendSongUrl())) {
            throw new MusicNotRecommendException(message);
        }
    }

    public static void validateFavoriteMusicListEmpty(List<Music> favoriteMusicList, String message) {
        if (favoriteMusicList == null || favoriteMusicList.isEmpty()) {
            throw new FavoriteMusicNotFoundException(message);
        }
    }
}
