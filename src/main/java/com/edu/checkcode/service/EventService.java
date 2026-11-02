package com.edu.checkcode.service;

import com.edu.checkcode.dto.*;
import com.edu.checkcode.exception.UnauthorizedException;
import com.edu.checkcode.model.Address;
import com.edu.checkcode.model.Event;
import com.edu.checkcode.model.Location;
import com.edu.checkcode.model.Role;
import com.edu.checkcode.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

	private final EventRepository eventRepository;

	@Transactional
	public EventResponse registerEvent(RegisterEventRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null) {
			throw new UnauthorizedException("Authentication not found");
		}

		String role = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null);

		if (role == null) {
			throw new UnauthorizedException("Role not found");
		}

		if (!Role.MANAGER.name().equals(role)) {
			throw new UnauthorizedException("Only MANAGER can register events");
		}

		if (request.date().isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("Event date cannot be in the past");
		}

		LocalDate today = LocalDate.now();
		long monthsUntilEvent = ChronoUnit.MONTHS.between(today, request.date());

		if (monthsUntilEvent < 3) {
			throw new IllegalArgumentException("Event date must be at least 3 months in the future");
		}

		validateTimeRange(request.startTime(), request.endTime());

		Event event = new Event();
		event.setName(request.name().trim());
		event.setDescription(request.description().trim());
		event.setDate(request.date());
		event.setStartTime(request.startTime());
		event.setEndTime(request.endTime());
		event.setLocation(mapLocation(request.location()));
		event.setTicketPrice(request.ticketPrice());

		return EventResponse.from(eventRepository.save(event));
	}

	@Transactional
	public EventResponse updateEvent(UUID eventId, UpdateEventRequest request) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null) {
			throw new UnauthorizedException("Authentication not found");
		}

		String role = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(null);

		if (role == null) {
			throw new UnauthorizedException("Role not found");
		}

		if (!Role.MANAGER.name().equals(role)) {
			throw new UnauthorizedException("Only MANAGER can update events");
		}

		Event event = eventRepository.findById(eventId)
				.orElseThrow(() -> new RuntimeException("Event not found"));

		LocalDate eventDate = event.getDate();
		LocalDate today = LocalDate.now();
		long weeksUntilEvent = ChronoUnit.WEEKS.between(today, eventDate);

		if (weeksUntilEvent < 2) {
			throw new UnauthorizedException("Event can only be edited at least 2 weeks before the event date");
		}

		if (request.name() != null) {
			event.setName(request.name().trim());
		}

		if (request.description() != null) {
			event.setDescription(request.description().trim());
		}

		if (request.date() != null) {
			if (request.date().isBefore(LocalDate.now())) {
				throw new IllegalArgumentException("Event date cannot be in the past");
			}
			event.setDate(request.date());
		}

		if (request.startTime() != null || request.endTime() != null) {
			String startTime = request.startTime() != null ? request.startTime() : event.getStartTime();
			String endTime = request.endTime() != null ? request.endTime() : event.getEndTime();
			validateTimeRange(startTime, endTime);
		}

		if (request.startTime() != null) {
			event.setStartTime(request.startTime());
		}

		if (request.endTime() != null) {
			event.setEndTime(request.endTime());
		}

		if (request.location() != null) {
			event.setLocation(mapLocation(request.location()));
		}

		if (request.ticketPrice() != null) {
			event.setTicketPrice(request.ticketPrice());
		}

		return EventResponse.from(eventRepository.save(event));
	}

	private Location mapLocation(LocationRequest request) {
		return Location.builder()
				.address(mapAddress(request.address()))
				.city(request.city().trim())
				.state(request.state().trim())
				.build();
	}

	private Address mapAddress(AddressRequest request) {
		return Address.builder()
				.complement(request.complement())
				.number(request.number())
				.zipCode(request.zipCode())
				.street(request.street())
				.build();
	}

	private void validateTimeRange(String startTime, String endTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		LocalTime start = LocalTime.parse(startTime, formatter);
		LocalTime end = LocalTime.parse(endTime, formatter);

		if (start.isAfter(end) || start.equals(end)) {
			throw new IllegalArgumentException("Start time must be before end time");
		}
	}
}
