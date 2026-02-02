package com.thc.capstone.repository;

import com.thc.capstone.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByUsernameAndPassword(String username, String password);

    Optional<User> findByEmail(String email);
}
