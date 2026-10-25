package com.edu.checkcode.dto;

import com.edu.checkcode.model.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateUserRequest(

		@Size(max = 150, message = "Name must have at most 150 characters")
		String name,

		@Email(message = "Invalid email")
		@Size(max = 254, message = "Email must have at most 254 characters")
		String email,

		@Size(min = 8, max = 72, message = "Password must have between 8 and 72 characters")
		String password,

		LocalDate birthDate,

		Gender gender
) {
}
