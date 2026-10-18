package com.edu.checkcode.dto;

import com.edu.checkcode.model.Gender;
import com.edu.checkcode.model.User;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record UserResponse(
		UUID id,
		String name,
		String email,
		LocalDate birthDate,
		Gender gender,
		Instant createdAt
) {

	public static UserResponse from(User user) {
		return new UserResponse(
				user.getId(),
				user.getName(),
				user.getEmail(),
				user.getBirthDate(),
				user.getGender(),
				user.getCreatedAt()
		);
	}
}
