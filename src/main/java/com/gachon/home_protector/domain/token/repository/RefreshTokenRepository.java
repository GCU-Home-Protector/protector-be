package com.gachon.home_protector.domain.token.repository;

import com.gachon.home_protector.domain.token.token.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
     // Boolean existsById()
     // Boolean deleteById()
}
