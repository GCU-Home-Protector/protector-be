package com.gachon.home_protector.api;

import com.gachon.home_protector.music.dto.recommend.MusicRecommendResponseFromAI;
import com.gachon.home_protector.music.exception.ai.MusicNotRecommendException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

public class MusicAssert extends Assert {

    public static void validateMusicRecommendation(MusicRecommendResponseFromAI response, String message) {
        if (response == null || StringUtils.isEmpty(response.getRecommendSong()) || StringUtils.isEmpty(response.getRecommendSongUrl())) {
            throw new MusicNotRecommendException(message);
        }
    }


}
