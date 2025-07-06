package com.gachon.home_protector.domain.music;

import com.gachon.home_protector.domain.common.MusicAssert;
import com.gachon.home_protector.domain.favorite_music.FavoriteMusic;
import com.gachon.home_protector.domain.favorite_music.FavoriteMusicRepository;
import com.gachon.home_protector.domain.music.client.MusicRecommendClient;
import com.gachon.home_protector.domain.music.dto.FavoriteMusicListResponse;
import com.gachon.home_protector.domain.music.dto.AddFavoriteMusicServiceRequest;
import com.gachon.home_protector.domain.music.dto.recommend.MusicRecommendResponse;
import com.gachon.home_protector.domain.music.dto.recommend.MusicRecommendResponseFromAI;
import com.gachon.home_protector.domain.music.dto.recommend.MusicRecommendServiceRequest;
import com.gachon.home_protector.domain.music.exception.MusicNotFoundException;
import com.gachon.home_protector.domain.security.userdetails.RestUserDetails;
import com.gachon.home_protector.domain.user.User;
import com.gachon.home_protector.domain.user.UserRepository;
import com.gachon.home_protector.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MusicService {

    private static final String FAVORITE_MUSIC_CACHE_KEY_PREFIX = "favoriteMusic:";
    private static final int CACHE_BASE_TTL_SECONDS = 600;
    private static final int CACHE_JITTER_MAX_SECONDS = 60;

    private final MusicRepository musicRepository;
    private final UserRepository userRepository;
    private final FavoriteMusicRepository favoriteMusicRepository;
    private final MusicRecommendClient musicRecommendClient;
    private final RedisTemplate redisTemplate;

    @Transactional
    public MusicRecommendResponse recommendMusic(MusicRecommendServiceRequest request) {
        MusicRecommendResponseFromAI aiResponse = musicRecommendClient.requestRecommendation(request);
        MusicAssert.validateMusicRecommendation(aiResponse, "추천 결과가 없습니다!"); // IllegalArgumentException throw
        Music savedMusic = musicRepository.save(aiResponse.toMusic());
        return savedMusic.toRecommendResponse();
    }

    @Transactional
    public MusicRecommendResponse updateMusic(MusicRecommendResponseFromAI aiResponse) {
        MusicAssert.validateMusicRecommendation(aiResponse, "추천 결과가 없습니다!"); // IllegalArgumentException throw
        Music savedMusic = musicRepository.save(aiResponse.toMusic());
        return savedMusic.toRecommendResponse();
    }

    public List<FavoriteMusicListResponse> getFavoriteMusicList(RestUserDetails userDetails) {
        Long userId = userDetails.getId();
        String cacheKey = FAVORITE_MUSIC_CACHE_KEY_PREFIX + userId;

        // 1. 캐시에서 조회
        List<FavoriteMusicListResponse> cachedList = (List<FavoriteMusicListResponse>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedList != null) {
            return cachedList;
        }

        // 2. 캐시에 없으면 DB에서 조회
        List<Music> favoriteMusicList = musicRepository.findFavoriteMusicByUserId(userId);
        MusicAssert.validateFavoriteMusicListEmpty(favoriteMusicList, "좋아요를 누른 음악이 없습니다!");

        List<FavoriteMusicListResponse> responseList = favoriteMusicList.stream()
                .map(Music::toFavoriteMusicListResponse)
                .toList();

        // 3. TTL + Jitter 설정 (예: 10분 + 0~1분)
        int jitterSeconds = ThreadLocalRandom.current().nextInt(0, CACHE_JITTER_MAX_SECONDS);
        redisTemplate.opsForValue().set(cacheKey, responseList, CACHE_BASE_TTL_SECONDS + jitterSeconds, TimeUnit.SECONDS);

        return responseList;
    }

    @Transactional
    public String addFavoriteMusic(RestUserDetails userDetails, AddFavoriteMusicServiceRequest serviceRequest) {
        Long userId = userDetails.getId();
        Long songId = serviceRequest.getSongId();

        // 1. DB에 저장
        Music music = musicRepository.findById(songId)
                .orElseThrow(() -> new MusicNotFoundException("존재하지 않는 음악입니다!"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다!"));

//        boolean alreadyLiked = favoriteMusicRepository.existsByUserAndMusic(user, music);
//        if (alreadyLiked) {
//            return "이미 좋아요했습니다!";
//        }

        FavoriteMusic favoriteMusic = FavoriteMusic.of(user, music);
        favoriteMusicRepository.save(favoriteMusic);

        String cacheKey = FAVORITE_MUSIC_CACHE_KEY_PREFIX + userId;
        List<FavoriteMusicListResponse> cachedList = (List<FavoriteMusicListResponse>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedList != null) {
            FavoriteMusicListResponse newFavorite = music.toFavoriteMusicListResponse();
            List<FavoriteMusicListResponse> updatedList = new ArrayList<>(cachedList);
            updatedList.add(newFavorite);
            redisTemplate.opsForValue().set(cacheKey, updatedList, CACHE_BASE_TTL_SECONDS + ThreadLocalRandom.current().nextInt(CACHE_JITTER_MAX_SECONDS), TimeUnit.SECONDS);
        }

        return "좋아요를 눌렀습니다!";
    }

    private String addFavoriteMusic(Long userId, Long songId) {
        Music music = musicRepository.findById(songId)
                .orElseThrow(() -> new MusicNotFoundException("존재하지 않는 음악입니다!"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저입니다!"));

        FavoriteMusic favoriteMusic = FavoriteMusic.of(user, music);
        favoriteMusicRepository.save(favoriteMusic);
        return "좋아요를 눌렀습니다!";
    }

    private String removeFavoriteMusic(FavoriteMusic favoriteMusic) {
        favoriteMusicRepository.delete(favoriteMusic);
        return "좋아요를 취소했습니다!";
    }
}
