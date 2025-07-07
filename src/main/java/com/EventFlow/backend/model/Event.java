package com.EventFlow.backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private int importanceLevel;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    @Column(nullable = false)
    private boolean isDone = false;

    @Column(nullable = false)
    private int eventOrder;

    public Event(String description, int importanceLevel, String location, String type, LocalDateTime startDate, LocalDateTime dueDate, int eventOrder) {
        this.description = description;
        this.importanceLevel = importanceLevel;
        this.location = location;
        this.type = type;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.isDone = false;
        this.eventOrder = eventOrder;
    }
}
