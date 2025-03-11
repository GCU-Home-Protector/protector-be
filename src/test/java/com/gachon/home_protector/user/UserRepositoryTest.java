package com.gachon.home_protector.user;

import com.gachon.home_protector.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest extends IntegrationTestSupport {

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @DisplayName("userId에 따라 유저를 찾을 수 있다.")
    @Test
    void findByUserId() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "role";

        User user = User.createRestLoginUser(userId, password, role);
        userRepository.save(user);

        // when
        Optional<User> result = userRepository.findByUserId(userId);

        // then
        assertTrue(result.isPresent());
        assertThat(result.get())
                .extracting("userId", "password", "role", "loginUserType")
                .containsExactlyInAnyOrder(userId, password, role, LoginUserType.REST_LOGIN_USER);
    }

    @DisplayName("userId에 따라 유저를 찾지 못할 수 있다.")
    @Test
    void findByUserId_NOUSER() {
        // given
        String userId = "userId";

        // when
        Optional<User> result = userRepository.findByUserId(userId);

        // then
        assertTrue(result.isEmpty());
    }

    @DisplayName("같은 userId를 가진 user가 잇는 지 찾을 수 잇다.")
    @Test
    void existsByUserId() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "role";

        User user = User.createRestLoginUser(userId, password, role);
        userRepository.save(user);

        String newUserId = "newUserId";

        // when
        Boolean result = userRepository.existsByUserId(newUserId);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("중복된 userId를 가진 user가 잇을 수 있다.")
    @Test
    void existsByUserId_DUPLICATE_USERID() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "role";

        User user = User.createRestLoginUser(userId, password, role);
        userRepository.save(user);

        String newUserId = "userId";

        // when
        Boolean result = userRepository.existsByUserId(newUserId);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("특정 id와 userId를 동시에 가진 유저를 찾을 수 있다.")
    @Test
    void existsByIdAndUserId() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "role";

        User user1 = User.createRestLoginUser(userId, password, role);
        User firstUser = userRepository.save(user1);

        // when
        Boolean result = userRepository.existsByIdAndUserId(firstUser.getId(), firstUser.getUserId());

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("특정 id와 userId를 동시에 가진 유저를 찾지 못할 수 있다.")
    @Test
    void existsByIdAndUserId_BAD_INPUT() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "role";

        String secondUserId = "userId2";
        String secondPassword = "password2";
        String secondRole = "role";

        User user1 = User.createRestLoginUser(userId, password, role);
        User firstUser = userRepository.save(user1);

        User user2 = User.createRestLoginUser(secondUserId, secondPassword, secondRole);
        User secondUser = userRepository.save(user2);

        // when
        Boolean result = userRepository.existsByIdAndUserId(firstUser.getId(), secondUser.getUserId());

        // then
        assertThat(result).isFalse();
    }
}