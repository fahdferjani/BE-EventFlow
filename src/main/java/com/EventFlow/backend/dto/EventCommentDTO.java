package com.EventFlow.backend.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventCommentDTO {
	private long id;
    private String userEmail;
    private LocalDateTime createdAt;
    private String text;
}
