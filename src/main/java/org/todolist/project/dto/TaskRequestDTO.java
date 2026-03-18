package org.todolist.project.dto;

import lombok.Data;
import org.todolist.project.model.Priority;
import java.time.LocalDate;

@Data
public class TaskRequestDTO {
    private String title;
    private String description;
    private Long statusId;
    private Priority priority;
    private LocalDate dueDate;
}
