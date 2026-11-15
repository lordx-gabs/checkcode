package com.edu.checkcode.dto;

import com.edu.checkcode.model.Event;
import com.edu.checkcode.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public record EventResponse(
		UUID id,
		String name,
		String description,
		LocalDate date,
		String startTime,
		String endTime,
		LocationResponse location,
		BigDecimal ticketPrice,
		List<UserResponse> employees
) {
	public static EventResponse from(Event event, List<User> employees) {
		List<UserResponse> employeeResponses = List.of();
		if(employees != null) {
			 employeeResponses = employees.stream()
					.map(UserResponse::from)
					.toList();
		}

		return new EventResponse(
				event.getId(),
				event.getName(),
				event.getDescription(),
				event.getDate(),
				event.getStartTime(),
				event.getEndTime(),
				LocationResponse.from(event.getLocation()),
				event.getTicketPrice(),
				employeeResponses
		);
	}
}
