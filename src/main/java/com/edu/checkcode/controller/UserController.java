package com.edu.checkcode.controller;

import com.edu.checkcode.dto.LoginResponse;
import com.edu.checkcode.dto.RegisterEmployeeRequest;
import com.edu.checkcode.dto.RegisterUserRequest;
import com.edu.checkcode.dto.UpdateUserRequest;
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

import java.util.UUID;

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

	@PostMapping("/register-employee")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Register employee", description = "Register a new employee (MANAGER only)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Employee registered successfully"),
			@ApiResponse(responseCode = "403", description = "Only MANAGER role can register employees"),
			@ApiResponse(responseCode = "409", description = "Email already registered"),
			@ApiResponse(responseCode = "400", description = "Invalid request data")
	})
	public LoginResponse registerEmployee(@Valid @RequestBody RegisterEmployeeRequest request) {
		return userService.registerEmployee(request);
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Update user", description = "Update user data (own data or MANAGER)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "User updated successfully"),
			@ApiResponse(responseCode = "403", description = "You can only update your own data or be a MANAGER"),
			@ApiResponse(responseCode = "404", description = "User not found"),
			@ApiResponse(responseCode = "409", description = "Email already registered"),
			@ApiResponse(responseCode = "400", description = "Invalid request data")
	})
	public UserResponse updateUser(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
		return userService.updateUser(id, request);
	}
}
