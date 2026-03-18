package org.todolist.project.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.todolist.project.dto.StatusRequestDTO;
import org.todolist.project.dto.StatusResponseDTO;
import org.todolist.project.service.StatusService;

import java.util.List;

@RestController
@RequestMapping("/api/statuses")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusService;

    @GetMapping
    public List<StatusResponseDTO> getAll() {
        return statusService.getAll();
    }

    @PostMapping
    public StatusResponseDTO create(@RequestBody StatusRequestDTO dto) {
        return statusService.create(dto);
    }

    @PutMapping("/{id}")
    public StatusResponseDTO update(@PathVariable Long id, @RequestBody StatusRequestDTO dto) {
        return statusService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        statusService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
