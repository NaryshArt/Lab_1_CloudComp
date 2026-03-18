package org.todolist.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.todolist.project.dto.SubtaskRequestDTO;
import org.todolist.project.dto.SubtaskResponseDTO;
import org.todolist.project.model.Subtask;
import org.todolist.project.repository.SubtaskRepository;
import org.todolist.project.repository.TaskRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubtaskService {

    private final SubtaskRepository subtaskRepository;
    private final TaskRepository taskRepository;

    public List<SubtaskResponseDTO> getByTaskId(Long taskId) {
        return subtaskRepository.findByTaskIdOrderByPositionAsc(taskId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public SubtaskResponseDTO create(SubtaskRequestDTO dto) {
        if (!taskRepository.existsById(dto.getTaskId())) {
            throw new RuntimeException("Задача не найдена: " + dto.getTaskId());
        }
        Subtask subtask = new Subtask(dto.getTaskId(), dto.getTitle(), dto.getPosition());
        return toResponse(subtaskRepository.save(subtask));
    }

    public SubtaskResponseDTO toggleComplete(Long id) {
        Subtask subtask = subtaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Подзадача не найдена: " + id));
        subtask.setIsCompleted(!subtask.getIsCompleted());
        return toResponse(subtaskRepository.save(subtask));
    }

    public void delete(Long id) {
        if (!subtaskRepository.existsById(id)) {
            throw new RuntimeException("Подзадача не найдена: " + id);
        }
        subtaskRepository.deleteById(id);
    }

    private SubtaskResponseDTO toResponse(Subtask s) {
        SubtaskResponseDTO dto = new SubtaskResponseDTO();
        dto.setId(s.getId());
        dto.setTaskId(s.getTaskId());
        dto.setTitle(s.getTitle());
        dto.setIsCompleted(s.getIsCompleted());
        dto.setPosition(s.getPosition());
        dto.setCreatedAt(s.getCreatedAt());
        return dto;
    }
}