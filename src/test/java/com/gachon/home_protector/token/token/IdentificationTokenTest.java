package com.gachon.home_protector.token.token;

import com.gachon.home_protector.IntegrationTestSupport;
import com.gachon.home_protector.token.repository.IdentificationTokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class IdentificationTokenTest extends IntegrationTestSupport {

    @Autowired
    IdentificationTokenRepository identificationTokenRepository;

    @AfterEach
    void tearDown() {
        identificationTokenRepository.deleteAll();
    }

    @DisplayName("uuid 값에 따라 identification token을 찾을 수 있다.")
    @Test
    void existsById() {
        // given
        String uuid = UUID.randomUUID().toString();
        IdentificationToken token = IdentificationToken.createIdentificationToken(uuid);
        identificationTokenRepository.save(token);

        // when
        boolean result = identificationTokenRepository.existsById(uuid);

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("특정 uuid 값을 가진 identification token이 없을 수 있다.")
    @Test
    void existsById_TOKEN_NOT_FOUND() {
        // given
        String uuid = UUID.randomUUID().toString();

        // when
        boolean result = identificationTokenRepository.existsById(uuid);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("특정 uuid를 통해 token을 제거할 수 있다.")
    @Test
    void deleteById() {
        // given
        String uuid = UUID.randomUUID().toString();
        IdentificationToken token = IdentificationToken.createIdentificationToken(uuid);
        identificationTokenRepository.save(token);

        // when
        identificationTokenRepository.deleteById(uuid);
        boolean result = identificationTokenRepository.existsById(uuid);

        // then
        assertThat(result).isFalse();
    }
}