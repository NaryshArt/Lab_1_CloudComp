package org.todolist.project.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.todolist.project.BaseIntegrationTest;
import org.todolist.project.model.Priority;
import org.todolist.project.model.Status;
import org.todolist.project.model.Subtask;
import org.todolist.project.model.Task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskControllerTest extends BaseIntegrationTest {

    private Long validStatusId;

    @BeforeEach
    void setUp() {
        validStatusId = statusRepository.save(new Status("Todo")).getId();
    }



    @Test
    void getAll_returnsEmptyList_whenNoTasks() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAll_returnsTasksOrderedByCreatedAtDesc() throws Exception {
        taskRepository.save(new Task("First", null, validStatusId, Priority.LOW));
        taskRepository.save(new Task("Second", null, validStatusId, Priority.HIGH));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Second"))
                .andExpect(jsonPath("$[1].title").value("First"));
    }



    @Test
    void getById_returnsTask() throws Exception {
        Task task = taskRepository.save(new Task("My Task", "desc", validStatusId, Priority.MEDIUM));

        mockMvc.perform(get("/api/tasks/" + task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("My Task"))
                .andExpect(jsonPath("$.description").value("desc"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"));
    }

    @Test
    void getById_nonExistentId_returns500() throws Exception {
        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().is5xxServerError());
    }



    @Test
    void create_persistsTaskAndReturnsIt() throws Exception {
        String body = """
                {
                  "title": "New Task",
                  "description": "Some description",
                  "statusId": %d,
                  "priority": "HIGH"
                }
                """.formatted(validStatusId);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("New Task"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.statusId").value(validStatusId))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        assertThat(taskRepository.findAll()).hasSize(1);
    }

    @Test
    void create_invalidStatusId_returns500() throws Exception {
        String body = """
                {
                  "title": "Task",
                  "statusId": 9999,
                  "priority": "LOW"
                }
                """;

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is5xxServerError());
    }



    @Test
    void update_changesTaskFields() throws Exception {
        Task task = taskRepository.save(new Task("Old Title", "Old Desc", validStatusId, Priority.LOW));
        Long newStatusId = statusRepository.save(new Status("Done")).getId();

        String body = """
                {
                  "title": "New Title",
                  "description": "New Desc",
                  "statusId": %d,
                  "priority": "HIGH"
                }
                """.formatted(newStatusId);

        mockMvc.perform(put("/api/tasks/" + task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.description").value("New Desc"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.statusId").value(newStatusId));
    }

    @Test
    void update_nonExistentId_returns500() throws Exception {
        String body = """
                {
                  "title": "Any",
                  "statusId": %d,
                  "priority": "LOW"
                }
                """.formatted(validStatusId);

        mockMvc.perform(put("/api/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is5xxServerError());
    }



    @Test
    void delete_removesTaskAndItsSubtasks() throws Exception {
        Task task = taskRepository.save(new Task("To Delete", null, validStatusId, Priority.LOW));
        subtaskRepository.save(new Subtask(task.getId(), "Sub 1", 1));
        subtaskRepository.save(new Subtask(task.getId(), "Sub 2", 2));

        mockMvc.perform(delete("/api/tasks/" + task.getId()))
                .andExpect(status().isNoContent());

        assertThat(taskRepository.existsById(task.getId())).isFalse();
        assertThat(subtaskRepository.findByTaskIdOrderByPositionAsc(task.getId())).isEmpty();
    }

    @Test
    void delete_nonExistentId_returns500() throws Exception {
        mockMvc.perform(delete("/api/tasks/999"))
                .andExpect(status().is5xxServerError());
    }
}