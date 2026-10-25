package com.edu.checkcode.controller;

import com.edu.checkcode.dto.EventResponse;
import com.edu.checkcode.dto.RegisterEventRequest;
import com.edu.checkcode.dto.UpdateEventRequest;
import com.edu.checkcode.service.EventService;
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
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(name = "Events", description = "Event management endpoints")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EventController {

	private final EventService eventService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Register event", description = "Create a new event (MANAGER only, at least 3 months in the future)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Event created successfully"),
			@ApiResponse(responseCode = "403", description = "Only MANAGER can register events"),
			@ApiResponse(responseCode = "400", description = "Invalid request data or event date is less than 3 months in the future")
	})
	public EventResponse registerEvent(@Valid @RequestBody RegisterEventRequest request) {
		return eventService.registerEvent(request);
	}

	@PutMapping("/{id}")
	@ResponseStatus(HttpStatus.OK)
	@Operation(summary = "Update event", description = "Update event data (MANAGER only, at least 2 weeks before event)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Event updated successfully"),
			@ApiResponse(responseCode = "403", description = "Only MANAGER can update events or event is less than 2 weeks away"),
			@ApiResponse(responseCode = "404", description = "Event not found"),
			@ApiResponse(responseCode = "400", description = "Invalid request data")
	})
	public EventResponse updateEvent(@PathVariable UUID id, @Valid @RequestBody UpdateEventRequest request) {
		return eventService.updateEvent(id, request);
	}
}
