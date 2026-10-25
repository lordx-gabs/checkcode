package com.edu.checkcode.dto;

import com.edu.checkcode.model.Event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EventResponse(
		UUID id,
		String name,
		String description,
		LocalDate date,
		String startTime,
		String endTime,
		LocationResponse location,
		BigDecimal ticketPrice
) {
	public static EventResponse from(Event event) {
		return new EventResponse(
				event.getId(),
				event.getName(),
				event.getDescription(),
				event.getDate(),
				event.getStartTime(),
				event.getEndTime(),
				LocationResponse.from(event.getLocation()),
				event.getTicketPrice()
		);
	}
}
