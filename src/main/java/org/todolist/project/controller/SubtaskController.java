package org.todolist.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.todolist.project.dto.SubtaskRequestDTO;
import org.todolist.project.dto.SubtaskResponseDTO;
import org.todolist.project.service.SubtaskService;

import java.util.List;

@RestController
@RequestMapping("/api/subtasks")
@RequiredArgsConstructor
public class SubtaskController {

    private final SubtaskService subtaskService;

    @GetMapping("/task/{taskId}")
    public List<SubtaskResponseDTO> getByTaskId(@PathVariable Long taskId) {
        return subtaskService.getByTaskId(taskId);
    }

    @PostMapping
    public SubtaskResponseDTO create(@RequestBody SubtaskRequestDTO dto) {
        return subtaskService.create(dto);
    }

    @PatchMapping("/{id}/toggle")
    public SubtaskResponseDTO toggle(@PathVariable Long id) {
        return subtaskService.toggleComplete(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subtaskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}