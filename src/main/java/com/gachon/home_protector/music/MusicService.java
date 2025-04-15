package com.gachon.home_protector.music;

import com.gachon.home_protector.api.MusicAssert;
import com.gachon.home_protector.music.client.MusicRecommendClient;
import com.gachon.home_protector.music.dto.FavoriteMusicListResponse;
import com.gachon.home_protector.music.dto.AddFavoriteMusicServiceRequest;
import com.gachon.home_protector.music.dto.recommend.MusicRecommendResponse;
import com.gachon.home_protector.music.dto.recommend.MusicRecommendResponseFromAI;
import com.gachon.home_protector.music.dto.recommend.MusicRecommendServiceRequest;
import com.gachon.home_protector.security.userdetails.RestUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MusicService {

    private final MusicRepository musicRepository;
    private final MusicRecommendClient musicRecommendClient;

    @Transactional
    public MusicRecommendResponse recommendMusic(MusicRecommendServiceRequest request) {
        MusicRecommendResponseFromAI aiResponse = musicRecommendClient.requestRecommendation(request);
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

    public String addOrDeleteFavoriteMusic(RestUserDetails userDetails, AddFavoriteMusicServiceRequest serviceRequest) {

        return null;
    }
}
