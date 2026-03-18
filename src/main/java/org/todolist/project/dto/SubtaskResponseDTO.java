package org.todolist.project.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SubtaskResponseDTO {
    private Long id;
    private Long taskId;
    private String title;
    private Boolean isCompleted;
    private Integer position;
    private LocalDateTime createdAt;
}