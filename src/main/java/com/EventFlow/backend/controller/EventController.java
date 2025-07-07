package com.EventFlow.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.EventFlow.backend.dto.EventDTO;
import com.EventFlow.backend.dto.AgendaItemDTO;
import com.EventFlow.backend.dto.UpdateEventDTO;
import com.EventFlow.backend.model.Event;
import com.EventFlow.backend.service.EventService;

import java.util.List;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/events")
@Tag(name = "Events", description = "Manage user events and sub-events")
public class EventController {

    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(summary = "Create a new event", description = "Creates a new main event for the authenticated user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Event created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/")
    public ResponseEntity<?> createEvent(
            @RequestParam String description,
            @RequestParam int importanceLevel,
            @RequestParam String location,
            @RequestParam String type,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime dueDate,
            @RequestParam int order) {
        Event event = eventService.createEvent(description, importanceLevel, location, type, startDate, dueDate, order);
        return ResponseEntity.ok(event);
    }

    @Operation(summary = "Add a sub-event to a main event", description = "Adds a new sub-event under a main event")
    @PostMapping("/{mainEventId}/sub-events")
    public ResponseEntity<?> addAgendaItem(
            @PathVariable Long mainEventId,
            @RequestParam String description,
            @RequestParam int importanceLevel,
            @RequestParam String location,
            @RequestParam String type,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime dueDate,
            @RequestParam int subOrder) {
        Event itemAgenda = eventService.addAgendaItem(mainEventId, description, importanceLevel, location, type, startDate, dueDate, subOrder);
        return ResponseEntity.ok(itemAgenda);
    }

    @Operation(summary = "Update an event's done status", description = "Mark a event as done or not done")
    @PatchMapping("/{eventId}/status")
    public ResponseEntity<?> updateEventStatus(@PathVariable Long eventId, @RequestParam boolean isDone) {
        eventService.updateEventStatus(eventId, isDone);
        return ResponseEntity.ok("Event status updated successfully!");
    }

    @Operation(summary = "Update an event", description = "Updates the fields of an event including description,importanceLevel, etc.")
    @PatchMapping("/{eventId}")
    public ResponseEntity<?> updateEvent(@PathVariable Long eventId, @RequestBody UpdateEventDTO updateData) {
        eventService.updateEvent(eventId, updateData);
        return ResponseEntity.ok("Event updated successfully!");
    }

    @Operation(summary = "Get current user's events", description = "Returns all main events created by the authenticated user, sorted by event order, and filtered by unfinished status.")
    @GetMapping("/")
    public ResponseEntity<List<EventDTO>> getUserEvents(
            @RequestParam(value = "filter", required = false) boolean filterUnfinished) { // Optional filter query param
        return ResponseEntity.ok(eventService.getUserEvents(filterUnfinished));
    }

    @Operation(summary = "Get a specific event", description = "Returns details of a specific event owned by the user")
    @GetMapping("/{eventId}")
    public ResponseEntity<EventDTO> getMyEvent(@PathVariable Long eventId) {
        EventDTO eventDTO = eventService.getMyEvent(eventId);
        return ResponseEntity.ok(eventDTO);
    }

    @Operation(summary = "Get sub-events of an event", description = "Returns the sub-events associated with a main event")
    @GetMapping("/{eventId}/sub-events")
    public ResponseEntity<List<AgendaItemDTO>> getAgendaItems(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventService.getAgendaItems(eventId));
    }



    @Operation(summary = "Delete an event", description = "Deletes a main event and its associated sub-events")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.ok("Event deleted successfully!");
    }

    @Operation(summary = "Delete a sub-event", description = "Deletes a sub-event from a main event")
    @DeleteMapping("/sub-events/{itemAgendaId}")
    public ResponseEntity<?> deleteItemAgenda(@PathVariable Long itemAgendaId) {
        eventService.deleteItemAgenda(itemAgendaId);
        return ResponseEntity.ok("Sub-event event deleted successfully!");
    }
}
