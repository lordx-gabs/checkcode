package com.edu.checkcode.service;

import com.edu.checkcode.dto.RegisterUserRequest;
import com.edu.checkcode.dto.UserResponse;
import com.edu.checkcode.exception.EmailAlreadyRegisteredException;
import com.edu.checkcode.model.User;
import com.edu.checkcode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(RegisterUserRequest request) {
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setBirthDate(request.birthDate());
        user.setGender(request.gender());
        user.setRole(request.role());

        return UserResponse.from(userRepository.save(user));
    }
}
