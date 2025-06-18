package com.gachon.home_protector.security.userdetails;

import com.gachon.home_protector.IntegrationTestSupport;
import com.gachon.home_protector.domain.security.userdetails.RestUserDetails;
import com.gachon.home_protector.domain.security.userdetails.RestUserDetailsService;
import com.gachon.home_protector.domain.user.User;
import com.gachon.home_protector.domain.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RestUserDetailsServiceTest extends IntegrationTestSupport {

    @Autowired
    RestUserDetailsService restUserDetailsService;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @DisplayName("사용자 정보를 기반으로 userDetail을 만들 수 있다.")
    @Test
    void loadUserByUsername() {
        // given
        String userId = "userId";
        String password = "password";
        String role = "ROLE";

        User user = User.createRestLoginUser(userId, password, role);
        User savedUser = userRepository.save(user);

        // when
        RestUserDetails result = (RestUserDetails) restUserDetailsService.loadUserByUsername(userId);

        // then
        assertThat(result)
                .extracting("id", "username", "password", "authorities")
                .containsExactly(savedUser.getId(), userId, password, List.of(new SimpleGrantedAuthority(role)));
    }

    @DisplayName("사용자를 찾을 수 없다면 UsernameNotFoundException을 throw한다.")
    @Test
    void loadUserByUsername_EMPTY_USER() {
        // given
        String userId = "userId";

        // when // then
        assertThatThrownBy(() -> restUserDetailsService.loadUserByUsername(userId))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("존재하지 않는 유저입니다!");
    }
}
