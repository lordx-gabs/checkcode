package com.edu.checkcode.dto;

import com.edu.checkcode.model.Gender;
import com.edu.checkcode.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterUserRequest(

		@NotBlank(message = "Name is required")
		@Size(max = 150, message = "Name must have at most 150 characters")
		String name,

		@NotBlank(message = "Email is required")
		@Email(message = "Invalid email")
		@Size(max = 254, message = "Email must have at most 254 characters")
		String email,

		// BCrypt only considers the first 72 bytes of the password
		@NotBlank(message = "Password is required")
		@Size(min = 8, max = 72, message = "Password must have between 8 and 72 characters")
		String password,

		@NotNull(message = "Birth date is required")
		@Past(message = "Birth date must be in the past")
		LocalDate birthDate,

		@NotNull(message = "Gender is required")
		Gender gender,

		@NotNull(message = "Role is required")
		Role role
) {
}
