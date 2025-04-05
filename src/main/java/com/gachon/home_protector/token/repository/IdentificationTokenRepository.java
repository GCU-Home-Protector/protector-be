package com.gachon.home_protector.token.repository;

import com.gachon.home_protector.token.token.IdentificationToken;
import org.springframework.data.repository.CrudRepository;

public interface IdentificationTokenRepository extends CrudRepository<IdentificationToken, String> {
}
