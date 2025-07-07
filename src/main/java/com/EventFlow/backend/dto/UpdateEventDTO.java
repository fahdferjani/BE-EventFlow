package com.EventFlow.backend.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateEventDTO {
    private String description;
    private Integer importanceLevel;
    private String location;
    private String type;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private Boolean isDone;
    private Integer eventOrder;
}
