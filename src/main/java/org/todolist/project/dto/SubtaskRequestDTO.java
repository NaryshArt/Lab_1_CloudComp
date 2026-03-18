package org.todolist.project.dto;

import lombok.Data;

@Data
public class SubtaskRequestDTO {
    private Long taskId;
    private String title;
    private Integer position;
}