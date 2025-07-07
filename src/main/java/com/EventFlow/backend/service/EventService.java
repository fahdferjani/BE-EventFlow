package com.EventFlow.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.EventFlow.backend.dto.EventDTO;
import com.EventFlow.backend.dto.AgendaItemDTO;
import com.EventFlow.backend.dto.UpdateEventDTO;
import com.EventFlow.backend.model.*;
import com.EventFlow.backend.repository.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {
    
    private final EventRepository eventRepository;
    private final UserEventRepository userEventRepository;
    private final AgendaItemRepository agendaItemRepository;
    private final UserService userService;

    @Autowired
    public EventService(EventRepository eventRepository, UserEventRepository userEventRepository, 
                        AgendaItemRepository agendaItemRepository, UserService userService) {
        this.eventRepository = eventRepository;
        this.userEventRepository = userEventRepository;
        this.agendaItemRepository = agendaItemRepository;
        this.userService = userService;
    }

    // Create a new event (hierarchy 1)
    public Event createEvent(String description, int importanceLevel, String location, String type,  LocalDateTime startDate, LocalDateTime dueDate, int order) {
        User user = userService.getAuthenticatedUser();
        Event event = new Event(description, importanceLevel, location, type, startDate, dueDate, order);
        event = eventRepository.save(event);

        UserEvent userEvent = new UserEvent();
        userEvent.setUser(user);
        userEvent.setEvent(event);
        userEvent.setHierarchy(1);  // Hierarchy 1 (Main Event)
        userEventRepository.save(userEvent);

        return event;
    }

    // Add a sub-event (hierarchy 2)
    public Event addAgendaItem(Long mainEventId, String description, int importanceLevel, String location, String type, LocalDateTime startDate, LocalDateTime dueDate, int subOrder) {
    	User user = userService.getAuthenticatedUser();
        Event mainEvent = eventRepository.findById(mainEventId)
                .orElseThrow(() -> new RuntimeException("Main Event not found!"));

        Event itemAgenda = new Event(description, importanceLevel, location, type, startDate, dueDate, subOrder); 
        itemAgenda = eventRepository.save(itemAgenda);

        AgendaItem agendaItem = new AgendaItem();
        agendaItem.setMainEvent(mainEvent);
        agendaItem.setItemAgenda(itemAgenda);
        agendaItemRepository.save(agendaItem);
        
        UserEvent userEvent = new UserEvent();
        userEvent.setUser(user);
        userEvent.setEvent(itemAgenda);
        userEvent.setHierarchy(2);  // Hierarchy 2 (sub-event)
        userEventRepository.save(userEvent);

        return itemAgenda;
    }

 // Modify an event's completion status (done/not done)
    public void updateEventStatus(Long eventId, boolean isDone) {
        User user = userService.getAuthenticatedUser();
        Event eventToUpdated = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found!"));
    	UserEvent userEvent = userEventRepository.findByUserAndEvent(user, eventToUpdated)
            .orElseThrow(() -> new RuntimeException("Event not assigned to user!"));


        eventToUpdated.setDone(isDone);
        eventRepository.save(eventToUpdated);
    }

    public void updateEvent(Long eventId, UpdateEventDTO updateData) {
        User user = userService.getAuthenticatedUser();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found!"));

        // Ensure the user owns the event
        UserEvent userEvent = userEventRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new RuntimeException("Event not assigned to user!"));

        // Update fields if provided
        if (updateData.getDescription() != null) {
            event.setDescription(updateData.getDescription());
        }
        if (updateData.getImportanceLevel() != null) {
            event.setImportanceLevel(updateData.getImportanceLevel());
        }
        if (updateData.getLocation() != null) {
            event.setLocation(updateData.getLocation());
        }
        if (updateData.getType() != null) {
            event.setType(updateData.getType());
        }
        
        if (updateData.getStartDate() != null) {
            event.setStartDate(updateData.getStartDate());
        }
        if (updateData.getDueDate() != null) {
            event.setDueDate(updateData.getDueDate());
        }

        if (updateData.getIsDone() != null) {
            event.setDone(updateData.getIsDone());
        }
        
        if (updateData.getEventOrder() != null) {
            event.setEventOrder(updateData.getEventOrder());
        }        
        eventRepository.save(event);

    }

    public EventDTO getMyEvent(Long eventId) {
        User user = userService.getAuthenticatedUser();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found!"));

        // Ensure the user owns the event
        UserEvent userEvent = userEventRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new RuntimeException("Event not assigned to user!"));
        
        return new EventDTO(event, userEvent);
        
    }
    
    public List<EventDTO> getUserEvents(boolean filterUnfinished) {
        User user = userService.getAuthenticatedUser();
        List<UserEvent> userEvents = userEventRepository.findByUserAndHierarchy(user, 1);

        // Filter if needed, and then sort by eventOrder (ascending)
        return userEvents.stream()
                .map(userEvent -> new EventDTO(userEvent.getEvent(), userEvent))  // Create DTO with event and userEvent
                .filter(eventDTO -> !filterUnfinished || !eventDTO.isDone()) // If filterUnfinished is true, only include unfinished events
                .sorted(Comparator.comparingInt(EventDTO::getOrder)) // Sort by event order (ascending)
                .collect(Collectors.toList());
    }

    public List<AgendaItemDTO> getAgendaItems(Long eventId) {
        User user = userService.getAuthenticatedUser();

        // Ensure the main event belongs to the user
        Event mainEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found!"));

        userEventRepository.findByUserAndEvent(user, mainEvent)
                .orElseThrow(() -> new RuntimeException("Event not assigned to user!"));

        // Retrieve (sub-events) of the main event and map to DTO, ordered by eventOrder
        return agendaItemRepository.findByMainEventOrderByItemAgendaEventOrderAsc(mainEvent)  // Corrected method call
                .stream()
                .map(AgendaItemDTO::new) // Convert AgendaItem to AgendaItemDTO
                .collect(Collectors.toList());
    }




    
    public void deleteEvent(Long eventId) {
        User user = userService.getAuthenticatedUser();
        Event mainEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found!"));

        // Ensure the user owns the event
        userEventRepository.findByUserAndEventAndHierarchy(user, mainEvent, 1)
                .orElseThrow(() -> new RuntimeException("Event not assigned to user or it's not a main Event!"));

        // Retrieve all sub events linked to this main event
        List<AgendaItem> agendaItems = agendaItemRepository.findByMainEvent(mainEvent);

        // Extract sub events and delete them first
        List<Event> itemAgendas = agendaItems.stream()
                .map(AgendaItem::getItemAgenda)
                .collect(Collectors.toList());
        
        // Delete sub events from the userEvent table first
        List<UserEvent> subUserEvents = userEventRepository.findByEventIn(itemAgendas);
        userEventRepository.deleteAll(subUserEvents);
        
        agendaItemRepository.deleteAll(agendaItems); // Delete sub relationships
        eventRepository.deleteAll(itemAgendas); // Delete sub events

        // Finally, delete the main event
        userEventRepository.deleteAll(userEventRepository.findByUserAndEvent(user, mainEvent).stream().toList());
        eventRepository.delete(mainEvent);
    }


    public void deleteItemAgenda(Long itemAgendaId) {
        User user = userService.getAuthenticatedUser();
        
        // Find the sub event
        Event itemAgenda = eventRepository.findById(itemAgendaId)
                .orElseThrow(() -> new RuntimeException("Sub Event not found!"));

        // Ensure the user owns the event
        userEventRepository.findByUserAndEventAndHierarchy(user, itemAgenda, 2)
                .orElseThrow(() -> new RuntimeException("Event not assigned to user or it's not a main Event!"));

        // Find the AgendaItem relation to get the main Event
        AgendaItem agendaItem = agendaItemRepository.findByItemAgenda(itemAgenda)
                .orElseThrow(() -> new RuntimeException("Sub Event is not linked to a main event!"));

        Event mainEvent = agendaItem.getMainEvent();

        // ✅ Ensure the user owns the **main event** (not the sub event)
        userEventRepository.findByUserAndEventAndHierarchy(user, mainEvent, 1)
                .orElseThrow(() -> new RuntimeException("You do not own the main event, cannot delete Sub event!"));

        userEventRepository.findByUserAndEvent(user, itemAgenda).ifPresent(userEventRepository::delete);

        // ✅ Remove from event_sub table
        agendaItemRepository.delete(agendaItem);

        // ✅ Delete the sub event itself
        eventRepository.delete(itemAgenda);
    }
    
}
