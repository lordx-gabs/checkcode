package com.edu.checkcode.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LocationRequest(
		@NotNull(message = "Address is required")
		@Valid
		AddressRequest address,

		@NotBlank(message = "City is required")
		@Size(max = 100, message = "City must have at most 100 characters")
		String city,

		@NotBlank(message = "State is required")
		@Size(max = 100, message = "State must have at most 100 characters")
		String state
) {
}
