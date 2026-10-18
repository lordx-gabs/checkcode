package com.edu.checkcode.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.edu.checkcode.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {

	@Value("${jwt.secret:default-secret-key-change-in-production}")
	private String secret;

	@Value("${jwt.expiration-hours:24}")
	private long expirationHours;

	public String generateToken(User user) {
		Algorithm algorithm = Algorithm.HMAC256(secret);
		Instant now = Instant.now();
		Instant expiration = now.plus(expirationHours, ChronoUnit.HOURS);

		return JWT.create()
				.withSubject(user.getId().toString())
				.withClaim("email", user.getEmail())
				.withClaim("role", user.getRole().name())
				.withIssuedAt(Date.from(now))
				.withExpiresAt(Date.from(expiration))
				.sign(algorithm);
	}

	public String validateTokenAndGetUserId(String token) {
		Algorithm algorithm = Algorithm.HMAC256(secret);
		try {
			return JWT.require(algorithm)
					.build()
					.verify(token)
					.getSubject();
		} catch (JWTVerificationException e) {
			throw new RuntimeException("Invalid or expired token", e);
		}
	}
}
