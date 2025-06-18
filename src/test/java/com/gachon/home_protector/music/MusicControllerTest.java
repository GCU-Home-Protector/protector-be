package com.gachon.home_protector.music;

import com.gachon.home_protector.ControllerTestSupport;
import com.gachon.home_protector.domain.music.dto.AddFavoriteMusicRequest;
import com.gachon.home_protector.domain.music.dto.recommend.MusicRecommendRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;


import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MusicControllerTest extends ControllerTestSupport {

    @WithMockUser(roles = "USER")
    @DisplayName("음악을 추천할 수 있다.")
    @Test
    void recommendMusic() throws Exception {
        // given
        String encodedFaceImage = "image";
        MusicRecommendRequest request = new MusicRecommendRequest(encodedFaceImage);

        // when // then
        mockMvc.perform(post("/music")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @WithMockUser(roles = "USER")
    @DisplayName("음악을 추천할 때 이미지는 필수이다.")
    @Test
    void recommendMusic_BLANK_IMAGE() throws Exception {
        // given
        MusicRecommendRequest request = new MusicRecommendRequest("");

        // when // then
        mockMvc.perform(post("/music")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("아기 얼굴은 필수입니다!"));
    }


    @WithMockUser(roles = "USER")
    @DisplayName("음악 좋아요 리스트를 가져올 수 있다.")
    @Test
    void getFavoriteMusicList() throws Exception {
        // given


        // when // then
        mockMvc.perform(get("/music/likes")
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @WithMockUser(roles = "USER")
    @DisplayName("음악 좋아요를 누르거나 취소할 수 있다.")
    @Test
    void addOrDeleteFavoriteMusic() throws Exception {
        // given
        Long songId = 1L;
        AddFavoriteMusicRequest request = new AddFavoriteMusicRequest(songId);

        mockMvc.perform(post("/music/likes")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @WithMockUser(roles = "USER")
    @DisplayName("음악 좋아요를 누르거나 취소할 때 PK의 값은 NULL이면 안 된다.")
    @Test
    void addOrDeleteFavoriteMusic_NULL() throws Exception {
        // given
        Long songId = null;
        AddFavoriteMusicRequest request = new AddFavoriteMusicRequest(songId);

        mockMvc.perform(post("/music/likes")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("노래의 ID는 필수입니다!"));
    }

    @WithMockUser(roles = "USER")
    @DisplayName("음악 좋아요를 누르거나 취소할 때 PK의 값은 1 이상이여야 한다")
    @Test
    void addOrDeleteFavoriteMusic_POSITIVE() throws Exception {
        // given
        Long songId = 0L;
        AddFavoriteMusicRequest request = new AddFavoriteMusicRequest(songId);

        mockMvc.perform(post("/music/likes")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("노래의 ID는 1 이상이어야 합니다!"));
    }

}
