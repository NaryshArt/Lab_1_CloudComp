package org.todolist.project.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.todolist.project.dto.StatusRequestDTO;
import org.todolist.project.dto.StatusResponseDTO;
import org.todolist.project.model.Status;
import org.todolist.project.repository.StatusRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;

    public List<StatusResponseDTO> getAll() {
        return statusRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public StatusResponseDTO create(StatusRequestDTO dto) {
        if (statusRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Статус уже существует: " + dto.getName());
        }
        Status status = new Status(dto.getName());
        return toResponse(statusRepository.save(status));
    }

    public StatusResponseDTO update(Long id, StatusRequestDTO dto) {
        Status status = statusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Статус не найден: " + id));
        status.setName(dto.getName());
        return toResponse(statusRepository.save(status));
    }

    public void delete(Long id) {
        if (!statusRepository.existsById(id)) {
            throw new RuntimeException("Статус не найден: " + id);
        }
        statusRepository.deleteById(id);
    }

    private StatusResponseDTO toResponse(Status s) {
        StatusResponseDTO dto = new StatusResponseDTO();
        dto.setId(s.getId());
        dto.setName(s.getName());
        return dto;
    }
}