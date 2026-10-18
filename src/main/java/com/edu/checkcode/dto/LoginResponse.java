package com.edu.checkcode.dto;

import com.edu.checkcode.model.Role;

import java.util.UUID;

public record LoginResponse(
		String token,
		UUID id,
		String name,
		String email,
		Role role
) {
}
