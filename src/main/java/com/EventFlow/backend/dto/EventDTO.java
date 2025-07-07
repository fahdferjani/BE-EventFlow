package com.EventFlow.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import com.EventFlow.backend.model.Event;
import com.EventFlow.backend.model.UserEvent;

@Getter
@Setter
public class EventDTO {
    private Long id;
    private String description;
    private int importanceLevel;
    private String location;
    private String type;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private int hierarchy;
    private boolean isDone; // Include isDone from UserEvent
    private int order;

    public EventDTO(Event event, UserEvent userEvent) {
        this.id = event.getId();
        this.description = event.getDescription();
        this.importanceLevel = event.getImportanceLevel();
        this.location = event.getLocation();
        this.type = event.getType();
        this.startDate = event.getStartDate();
        this.dueDate = event.getDueDate();
        this.isDone = event.isDone();
        this.order = event.getEventOrder();
        this.hierarchy = (userEvent != null) ? userEvent.getHierarchy() : 0;
    }
}
