package com.edu.checkcode.service;

import com.edu.checkcode.dto.LoginRequest;
import com.edu.checkcode.dto.LoginResponse;
import com.edu.checkcode.exception.InvalidCredentialsException;
import com.edu.checkcode.model.User;
import com.edu.checkcode.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public LoginResponse login(LoginRequest request) {
		String email = request.email().trim().toLowerCase(Locale.ROOT);

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw new InvalidCredentialsException("Invalid email or password");
		}

		String token = jwtService.generateToken(user);

		return new LoginResponse(
				token,
				user.getId(),
				user.getName(),
				user.getEmail(),
				user.getRole()
		);
	}
}
