package com.EventFlow.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "event_AgendaItems")
public class AgendaItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_event_id", nullable = false)
    private Event mainEvent; // Reference to Main Event (Hierarchy 1)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_event_id", nullable = false)
    private Event itemAgenda; // Reference to Sub Event (Hierarchy 2)


}
