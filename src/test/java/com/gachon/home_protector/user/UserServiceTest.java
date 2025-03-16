package com.gachon.home_protector.user;

import com.gachon.home_protector.IntegrationTestSupport;
import com.gachon.home_protector.user.dto.join.UserJoinServiceRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @DisplayName("회원가입할 수 있다.")
    @Test
    void joinRestUser() {
        // given
        String userId = "userId";
        String password = "password";
        UserJoinServiceRequest loginRequest = UserJoinServiceRequest.of(userId, password);

        // when
        RestUserJoinResponse restUserJoinResponse = userService.joinRestUser(loginRequest);

        // then
        assertThat(restUserJoinResponse)
                .extracting("userId", "role", "loginUserType")
                .containsExactlyInAnyOrder(userId, "ROLE_USER", LoginUserType.REST_LOGIN_USER);
    }

    @DisplayName("중복된 id를 사용하는 유저가 있다면 회원가입할 수 없다.")
    @Test
    void joinRestUser_DUPLICATE_USERID() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "ROLE_USER";
        userRepository.save(User.createRestLoginUser(userId, password, role));

        String duplicateUserId = "userId";
        String newUserpassword = "password";
        UserJoinServiceRequest loginRequest = UserJoinServiceRequest.of(duplicateUserId, newUserpassword);

        // when // then
        assertThatThrownBy(() -> userService.joinRestUser(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 중복된 ID가 사용중입니다! 다른 ID를 사용해주세요!");
    }
}