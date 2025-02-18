package com.gachon.home_protector.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gachon.home_protector.ControllerTestSupport;
import com.gachon.home_protector.user.RestLoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class RestLoginFilterTest extends ControllerTestSupport {

    private ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("로그인할 수 있다.")
    @Test
    void login() throws Exception {
//        // given
//        RestLoginRequest request = RestLoginRequest.of("userId", "password");
//
//        // when // then
//        mockMvc.perform(post("/login")
//                .content(objectMapper.writeValueAsString(request))
//                .contentType(APPLICATION_JSON)
//        )
//                .andDo(print())
//                .andExpect(jsonPath("$.code").value("200"))
//                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("로그인 시 ID는 필수이다!")
    @NullAndEmptySource
    @ParameterizedTest(name = "ID가 {0}일 때 에러가 발생한다.")
    void login_EMPTYID(String emptyId) {
        // given
        RestLoginRequest request = RestLoginRequest.of(emptyId, "password");

        // when // then
        assertThatThrownBy(() -> {
            mockMvc.perform(post("/login")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON)
            );
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자의 ID/PW는 필수입니다!");
    }

    @DisplayName("로그인 시 비밀번호는 필수이다!")
    @NullAndEmptySource
    @ParameterizedTest(name = "비밀번호가 {0}일 때 에러가 발생한다.")
    void login_EMPTYPASSWORD(String emptyPassword) {
        // given
        RestLoginRequest request = RestLoginRequest.of("username", emptyPassword);

        // when // then
        assertThatThrownBy(() -> {
            mockMvc.perform(post("/login")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(APPLICATION_JSON)
            );
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자의 ID/PW는 필수입니다!");
    }

    @DisplayName("로그인 시 content가 필요하다.")
    @Test
    void login_EMPTYBODY() {
        // given

        // when // then
        assertThatThrownBy(() -> {
            mockMvc.perform(post("/login")
                    .content(objectMapper.writeValueAsString(null))
                    .contentType(APPLICATION_JSON)
            );
        })
                .isInstanceOf(NullPointerException.class);
    }

    @DisplayName("로그인 시 지정된 경로로 요청을 보내야 한다.")
    @Test
    void login_INVALIDPATH() throws Exception {
        // given
        RestLoginRequest request = RestLoginRequest.of("username", "password");

        // when // then
        mockMvc.perform(post("/invalid/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @DisplayName("로그인 시 지정된 method 통해 요청을 보내야 한다.")
    @Test
    void login_INVALIDMETHOD() throws Exception {
        // given
        RestLoginRequest request = RestLoginRequest.of("username", "password");

        // when // then
        mockMvc.perform(get("/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

