package org.todolist.project.dto;

import lombok.Data;
import org.todolist.project.model.Priority;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Long statusId;
    private Priority priority;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}