package org.todolist.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.todolist.project.dto.TaskRequestDTO;
import org.todolist.project.dto.TaskResponseDTO;
import org.todolist.project.model.Task;
import org.todolist.project.repository.StatusRepository;
import org.todolist.project.repository.SubtaskRepository;
import org.todolist.project.repository.TaskRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final StatusRepository statusRepository;
    private final SubtaskRepository subtaskRepository;

    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public TaskResponseDTO getById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задача не найдена: " + id));
        return toResponse(task);
    }

    public TaskResponseDTO create(TaskRequestDTO dto) {
        if (!statusRepository.existsById(dto.getStatusId())) {
            throw new RuntimeException("Статус не найден: " + dto.getStatusId());
        }
        Task task = new Task(dto.getTitle(), dto.getDescription(), dto.getStatusId(), dto.getPriority());
        task.setDueDate(dto.getDueDate());
        return toResponse(taskRepository.save(task));
    }

    public TaskResponseDTO update(Long id, TaskRequestDTO dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задача не найдена: " + id));
        if (!statusRepository.existsById(dto.getStatusId())) {
            throw new RuntimeException("Статус не найден: " + dto.getStatusId());
        }
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatusId(dto.getStatusId());
        task.setPriority(dto.getPriority());
        task.setDueDate(dto.getDueDate());
        return toResponse(taskRepository.save(task));
    }

    @Transactional
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Задача не найдена: " + id);
        }
        subtaskRepository.deleteByTaskId(id); // удаляем подзадачи каскадом
        taskRepository.deleteById(id);
    }

    private TaskResponseDTO toResponse(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatusId(task.getStatusId());
        dto.setPriority(task.getPriority());
        dto.setDueDate(task.getDueDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        return dto;
    }
}