package com.edu.checkcode.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegisterEventRequest(

		@NotBlank(message = "Event name is required")
		@Size(max = 200, message = "Event name must have at most 200 characters")
		String name,

		@NotBlank(message = "Description is required")
		@Size(max = 500, message = "Description must have at most 500 characters")
		String description,

		@NotNull(message = "Event date is required")
		LocalDate date,

		@NotBlank(message = "Start time is required")
		@Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Start time must be in HH:mm format")
		String startTime,

		@NotBlank(message = "End time is required")
		@Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "End time must be in HH:mm format")
		String endTime,

		@NotNull(message = "Location is required")
		@Valid
		LocationRequest location,

		@NotNull(message = "Ticket price is required")
		@DecimalMin(value = "0.0", message = "Ticket price must be positive")
		BigDecimal ticketPrice
) {
}
