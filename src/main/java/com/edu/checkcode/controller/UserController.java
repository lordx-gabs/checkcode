package com.edu.checkcode.controller;

import com.edu.checkcode.dto.RegisterUserRequest;
import com.edu.checkcode.dto.UserResponse;
import com.edu.checkcode.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

	private final UserService userService;

	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Register user", description = "Create a new user account")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "User created successfully"),
			@ApiResponse(responseCode = "409", description = "Email already registered"),
			@ApiResponse(responseCode = "400", description = "Invalid request data")
	})
	public UserResponse register(@Valid @RequestBody RegisterUserRequest request) {
		return userService.register(request);
	}
}
