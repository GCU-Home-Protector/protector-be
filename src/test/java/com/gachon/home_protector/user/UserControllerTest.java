package com.gachon.home_protector.user;

import com.gachon.home_protector.ControllerTestSupport;
import com.gachon.home_protector.user.dto.UserJoinRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.http.MediaType.APPLICATION_JSON;
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
                .andExpect(jsonPath("$.message").value( "OK"));
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
                .andExpect(jsonPath("$.message").value( "ID는 필수입니다!"));
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
                .andExpect(jsonPath("$.message").value( "비밀번호는 필수입니다!"));
    }
}