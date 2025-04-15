package com.gachon.home_protector.music.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddFavoriteMusicRequest {

    @NotNull(message = "노래의 ID는 필수입니다!")
    @Min(value = 1, message = "노래의 ID는 1 이상이어야 합니다!")
    private Long songId;

    public AddFavoriteMusicRequest(Long songId) {
        this.songId = songId;
    }

    public AddFavoriteMusicServiceRequest toServiceRequest() {
        return new AddFavoriteMusicServiceRequest(songId);
    }
}
