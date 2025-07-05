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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MusicService {

    private final MusicRepository musicRepository;
    private final UserRepository userRepository;
    private final FavoriteMusicRepository favoriteMusicRepository;
    private final MusicRecommendClient musicRecommendClient;

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
        List<Music> favoriteMusicList = musicRepository.findFavoriteMusicByUserId(userId);
        MusicAssert.validateFavoriteMusicListEmpty(favoriteMusicList, "좋아요를 누른 음악이 없습니다!");

        return favoriteMusicList.stream()
                .map(Music::toFavoriteMusicListResponse)
                .toList();
    }

    @Transactional
    public String addOrDeleteFavoriteMusic(RestUserDetails userDetails, AddFavoriteMusicServiceRequest serviceRequest) {
        Long userId = userDetails.getId();
        Long songId = serviceRequest.getSongId();

        return favoriteMusicRepository.findByUserAndMusicId(userId, songId)
                .map(this::removeFavoriteMusic)
                .orElseGet(() -> addFavoriteMusic(userId, songId));
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
