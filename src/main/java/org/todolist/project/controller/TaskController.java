package org.todolist.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.todolist.project.dto.TaskRequestDTO;
import org.todolist.project.dto.TaskResponseDTO;
import org.todolist.project.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<TaskResponseDTO> getAll() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public TaskResponseDTO getById(@PathVariable Long id) {
        return taskService.getById(id);
    }

    @PostMapping
    public TaskResponseDTO create(@RequestBody TaskRequestDTO dto) {
        return taskService.create(dto);
    }

    @PutMapping("/{id}")
    public TaskResponseDTO update(@PathVariable Long id, @RequestBody TaskRequestDTO dto) {
        return taskService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}