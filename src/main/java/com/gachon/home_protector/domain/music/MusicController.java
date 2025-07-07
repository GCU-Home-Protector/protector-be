package com.gachon.home_protector.domain.music;

import com.gachon.home_protector.domain.common.SuccessResponse;
import com.gachon.home_protector.domain.music.dto.FavoriteMusicListResponse;
import com.gachon.home_protector.domain.music.dto.AddFavoriteMusicRequest;
import com.gachon.home_protector.domain.music.dto.recommend.MusicRecommendRequest;
import com.gachon.home_protector.domain.music.dto.recommend.MusicRecommendResponse;
import com.gachon.home_protector.domain.music.facade.MusicFacade;
import com.gachon.home_protector.domain.security.userdetails.RestUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/music")
public class MusicController {

    private final MusicService musicService;
    private final MusicFacade musicFacade;

    @PostMapping
    public SuccessResponse<MusicRecommendResponse> recommendMusic(@Valid @RequestBody MusicRecommendRequest request) {
        return SuccessResponse.success(musicFacade.recommendMusic(request.toServiceRequest()));
    }

    @PostMapping("/likes")
    public SuccessResponse<String> addFavoriteMusic(@AuthenticationPrincipal RestUserDetails userDetails,
                                                    @Valid @RequestBody AddFavoriteMusicRequest request) {
        return SuccessResponse.success(musicService.addFavoriteMusic(userDetails, request.toServiceRequest()));
    }

    @GetMapping("/likes")
    public SuccessResponse<List<FavoriteMusicListResponse>> getFavoriteMusicList(@AuthenticationPrincipal RestUserDetails userDetails) {
        return SuccessResponse.success(musicService.getFavoriteMusicList(userDetails));
    }

}
