package com.EventFlow.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import com.EventFlow.backend.model.Event;
import com.EventFlow.backend.model.AgendaItem;

@Getter
@Setter
public class AgendaItemDTO {
    private Long itemAgendaId;
    private String description;
    private int importanceLevel;
    private String location;
    private String type;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private int hierarchy;
    private int subOrder; // ✅ Order of the sub
    private boolean isDone;

    public AgendaItemDTO(AgendaItem agendaitem) {
        Event itemAgenda = agendaitem.getItemAgenda();
        this.itemAgendaId = itemAgenda.getId();
        this.description = itemAgenda.getDescription();
        this.importanceLevel = itemAgenda.getImportanceLevel();
        this.location = itemAgenda.getLocation();
        this.type = itemAgenda.getType();
        this.startDate = itemAgenda.getStartDate();
        this.dueDate = itemAgenda.getDueDate();
        this.subOrder = itemAgenda.getEventOrder();
        this.isDone =  itemAgenda.isDone();
    }
}
