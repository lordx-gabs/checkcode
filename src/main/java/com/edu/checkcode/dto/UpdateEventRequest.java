package com.edu.checkcode.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateEventRequest(

		@Size(max = 200, message = "Event name must have at most 200 characters")
		String name,

		@Size(max = 500, message = "Description must have at most 500 characters")
		String description,

		LocalDate date,

		@Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "Start time must be in HH:mm format")
		String startTime,

		@Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$", message = "End time must be in HH:mm format")
		String endTime,

		@Valid
		LocationRequest location,

		@DecimalMin(value = "0.0", message = "Ticket price must be positive")
		BigDecimal ticketPrice
) {
}
