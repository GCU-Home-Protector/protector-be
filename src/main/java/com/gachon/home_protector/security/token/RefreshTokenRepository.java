package com.gachon.home_protector.security.token;

import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    boolean existsByUserId(Long userId);
}
