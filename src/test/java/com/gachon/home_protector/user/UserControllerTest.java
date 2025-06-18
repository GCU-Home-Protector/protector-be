package com.gachon.home_protector.user;

import com.gachon.home_protector.ControllerTestSupport;
import com.gachon.home_protector.domain.user.dto.identification.UpdateIdentificationRequest;
import com.gachon.home_protector.domain.user.dto.join.UserJoinRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends ControllerTestSupport {

    @DisplayName("회원가입할 수 있다.")
    @Test
    void join() throws Exception {
        // given
        String userId = "userId";
        String password = "password";
        UserJoinRequest request = UserJoinRequest.of(userId, password);

        // when // then
        mockMvc.perform(post("/user/join")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @DisplayName("회원가입 시 ID를 입력해야 한다.")
    @Test
    void join_EMPTY_USERID() throws Exception {
        // given
        String userId = "";
        String password = "password";
        UserJoinRequest request = UserJoinRequest.of(userId, password);

        // when // then
        mockMvc.perform(post("/user/join")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("ID는 필수입니다!"));
    }

    @DisplayName("회원가입 시 비밀번호를 입력해야 한다.")
    @Test
    void join_EMPTY_PASSWORD() throws Exception {
        // given
        String userId = "userId";
        String password = "";
        UserJoinRequest request = UserJoinRequest.of(userId, password);

        // when // then
        mockMvc.perform(post("/user/join")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("비밀번호는 필수입니다!"));
    }

    @WithMockUser(roles = "USER")
    @DisplayName("회원 정보를 변경할 수 있다.")
    @Test
    void updateIdentification() throws Exception {
        // given
        String identificationToken = "test-identification-token";
        String userId = "userId";
        String password = "password";
        UpdateIdentificationRequest request = UpdateIdentificationRequest.of(userId, password);

        // when // then
        mockMvc.perform(patch("/user/update-identification")
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON)
                .header("Protector-Identification", identificationToken)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @WithMockUser(roles = "USER")
    @DisplayName("이전에 비밀번호 인증을 받지 않았으면 유저 신원 정보를 변경할 수 없다.")
    @Test
    void updateIdentification_without_HEADER() throws Exception {
        // given
        String userId = "userId";
        String password = "password";
        UpdateIdentificationRequest request = UpdateIdentificationRequest.of(userId, password);

        // when // then
        mockMvc.perform(patch("/user/update-identification")
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON)
        )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.status").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").value("header의 값이 존재하지 않습니다!"));
    }

    @WithMockUser(roles = "USER")
    @DisplayName("비밀번호 인증을 받았지만, header의 값이 없을 수 있다.")
    @Test
    void updateIdentification_without_HEADER_VALUE() throws Exception {
        // given
        String userId = "userId";
        String password = "password";
        UpdateIdentificationRequest request = UpdateIdentificationRequest.of(userId, password);

        // when // then
        mockMvc.perform(patch("/user/update-identification")
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON)
                        .header("Protector-Identification", "")
        )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.status").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("이전 페이지에서 비밀번호를 다시 입력해주세요!"));
    }

    @WithMockUser(roles = "USER")
    @DisplayName("신원 변경 시 userId는 필수이다.")
    @Test
    void updateIdentification_without_USERID() throws Exception {
        // given
        String userId = "";
        String password = "password";
        String identificationToken = "test-identification-token";
        UpdateIdentificationRequest request = UpdateIdentificationRequest.of(userId, password);

        // when // then
        mockMvc.perform(patch("/user/update-identification")
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON)
                        .header("Protector-Identification", identificationToken)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("새로운 사용자 ID는 필수입니다!"));
    }

    @WithMockUser(roles = "USER")
    @DisplayName("신원 변경 시 비밀번호는 필수이다.")
    @Test
    void updateIdentification_without_PASSWORD() throws Exception {
        // given
        String userId = "userId";
        String password = "";
        String identificationToken = "test-identification-token";
        UpdateIdentificationRequest request = UpdateIdentificationRequest.of(userId, password);

        // when // then
        mockMvc.perform(patch("/user/update-identification")
                .content(objectMapper.writeValueAsString(request))
                .contentType(APPLICATION_JSON)
                        .header("Protector-Identification", identificationToken)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("새로운 사용자 비밀번호는 필수입니다!"));
    }

}
