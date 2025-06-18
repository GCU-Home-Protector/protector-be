package com.gachon.home_protector.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);

    Boolean existsByUserId(String userId);

    Boolean existsByIdAndUserId(Long id, String username);
}
