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

class SubtaskControllerTest extends BaseIntegrationTest {

    private Long taskId;

    @BeforeEach
    void setUp() {
        Long statusId = statusRepository.save(new Status("Todo")).getId();
        taskId = taskRepository.save(new Task("Parent Task", null, statusId, Priority.MEDIUM)).getId();
    }



    @Test
    void getByTaskId_returnsEmptyList_whenNoSubtasks() throws Exception {
        mockMvc.perform(get("/api/subtasks/task/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getByTaskId_returnsSubtasksOrderedByPosition() throws Exception {
        subtaskRepository.save(new Subtask(taskId, "Third", 3));
        subtaskRepository.save(new Subtask(taskId, "First", 1));
        subtaskRepository.save(new Subtask(taskId, "Second", 2));

        mockMvc.perform(get("/api/subtasks/task/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].title").value("First"))
                .andExpect(jsonPath("$[1].title").value("Second"))
                .andExpect(jsonPath("$[2].title").value("Third"));
    }



    @Test
    void create_persistsSubtaskAndReturnsIt() throws Exception {
        String body = """
                {
                  "taskId": %d,
                  "title": "New Subtask",
                  "position": 1
                }
                """.formatted(taskId);

        mockMvc.perform(post("/api/subtasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("New Subtask"))
                .andExpect(jsonPath("$.taskId").value(taskId))
                .andExpect(jsonPath("$.isCompleted").value(false))
                .andExpect(jsonPath("$.position").value(1));

        assertThat(subtaskRepository.countByTaskId(taskId)).isEqualTo(1L);
    }

    @Test
    void create_nonExistentTaskId_returns500() throws Exception {
        String body = """
                {
                  "taskId": 9999,
                  "title": "Orphan",
                  "position": 1
                }
                """;

        mockMvc.perform(post("/api/subtasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is5xxServerError());
    }



    @Test
    void toggle_switchesCompletionFromFalseToTrue() throws Exception {
        Subtask subtask = subtaskRepository.save(new Subtask(taskId, "Toggle me", 1));
        assertThat(subtask.getIsCompleted()).isFalse();

        mockMvc.perform(patch("/api/subtasks/" + subtask.getId() + "/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isCompleted").value(true));
    }

    @Test
    void toggle_switchesCompletionFromTrueToFalse() throws Exception {
        Subtask subtask = new Subtask(taskId, "Already done", 1);
        subtask.setIsCompleted(true);
        subtask = subtaskRepository.save(subtask);

        mockMvc.perform(patch("/api/subtasks/" + subtask.getId() + "/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isCompleted").value(false));
    }

    @Test
    void toggle_nonExistentId_returns500() throws Exception {
        mockMvc.perform(patch("/api/subtasks/999/toggle"))
                .andExpect(status().is5xxServerError());
    }



    @Test
    void delete_removesSubtask() throws Exception {
        Subtask subtask = subtaskRepository.save(new Subtask(taskId, "To Delete", 1));

        mockMvc.perform(delete("/api/subtasks/" + subtask.getId()))
                .andExpect(status().isNoContent());

        assertThat(subtaskRepository.existsById(subtask.getId())).isFalse();
    }

    @Test
    void delete_nonExistentId_returns500() throws Exception {
        mockMvc.perform(delete("/api/subtasks/999"))
                .andExpect(status().is5xxServerError());
    }
}