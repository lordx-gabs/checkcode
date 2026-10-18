package com.edu.checkcode.repository;

import com.edu.checkcode.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

	boolean existsByEmail(String email);

	java.util.Optional<User> findByEmail(String email);
}
