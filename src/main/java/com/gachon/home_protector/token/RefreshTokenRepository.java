package com.gachon.home_protector.token;

import com.gachon.home_protector.token.token.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
     // Boolean existsById()
     // Boolean deleteById()
}
