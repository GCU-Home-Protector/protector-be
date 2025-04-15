package com.gachon.home_protector.music;

import com.gachon.home_protector.api.SuccessResponse;
import com.gachon.home_protector.music.dto.FavoriteMusicListResponse;
import com.gachon.home_protector.music.dto.AddFavoriteMusicRequest;
import com.gachon.home_protector.music.dto.recommend.MusicRecommendRequest;
import com.gachon.home_protector.music.dto.recommend.MusicRecommendResponse;
import com.gachon.home_protector.security.userdetails.RestUserDetails;
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

    @PostMapping
    public SuccessResponse<MusicRecommendResponse> recommendMusic(@Valid @RequestBody MusicRecommendRequest request) {
        return SuccessResponse.success(musicService.recommendMusic(request.toServiceRequest()));
    }

    @PostMapping("/likes")
    public SuccessResponse<String> addOrDeleteFavoriteMusic(@AuthenticationPrincipal RestUserDetails userDetails,
                                                            @Valid @RequestBody AddFavoriteMusicRequest request) {
        return SuccessResponse.success(musicService.addOrDeleteFavoriteMusic(userDetails, request.toServiceRequest()));
    }

    @GetMapping("/likes")
    public SuccessResponse<List<FavoriteMusicListResponse>> getFavoriteMusicList(@AuthenticationPrincipal RestUserDetails userDetails) {
        return SuccessResponse.success(musicService.getFavoriteMusicList(userDetails));
    }

}
