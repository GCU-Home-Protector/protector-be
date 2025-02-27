package com.gachon.home_protector.security.token;

import com.gachon.home_protector.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenRepositoryTest extends IntegrationTestSupport {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAll();
    }

    @DisplayName("userId에 따라 refresh token을 찾을 수 있다.")
    @Test
    void existsById() {
        // given
        Long userId = 1L;
        String uuid = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.createRefreshToken(userId, uuid);
        refreshTokenRepository.save(refreshToken);

        // when
        boolean result = refreshTokenRepository.existsById(userId);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("refresh token이 없을 수도 잇다")
    @Test
    void existsById_EMPTY() {
        // given
        Long userId = 1L;

        // when
        boolean result = refreshTokenRepository.existsById(userId);

        // then
        assertThat(result).isFalse();
    }


}