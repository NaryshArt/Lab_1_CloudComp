package org.todolist.project.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.todolist.project.BaseIntegrationTest;
import org.todolist.project.model.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StatusControllerTest extends BaseIntegrationTest {

    @Test
    void getAll_returnsEmptyList_whenNoStatuses() throws Exception {
        mockMvc.perform(get("/api/statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getAll_returnsAllStatuses() throws Exception {
        statusRepository.save(new Status("Todo"));
        statusRepository.save(new Status("Done"));

        mockMvc.perform(get("/api/statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void create_persistsStatusAndReturnsIt() throws Exception {
        mockMvc.perform(post("/api/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"In Progress\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("In Progress"));

        assertThat(statusRepository.existsByName("In Progress")).isTrue();
    }

    @Test
    void create_duplicateName_returns500() throws Exception {
        statusRepository.save(new Status("Unique"));

        mockMvc.perform(post("/api/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Unique\"}"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void update_changesStatusName() throws Exception {
        Status status = statusRepository.save(new Status("Old Name"));

        mockMvc.perform(put("/api/statuses/" + status.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"New Name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));

        Status updated = statusRepository.findById(status.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("New Name");
    }

    @Test
    void update_nonExistentId_returns500() throws Exception {
        mockMvc.perform(put("/api/statuses/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Any\"}"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    void delete_removesStatus() throws Exception {
        Status status = statusRepository.save(new Status("To Delete"));

        mockMvc.perform(delete("/api/statuses/" + status.getId()))
                .andExpect(status().isNoContent());

        assertThat(statusRepository.existsById(status.getId())).isFalse();
    }

    @Test
    void delete_nonExistentId_returns500() throws Exception {
        mockMvc.perform(delete("/api/statuses/999"))
                .andExpect(status().is5xxServerError());
    }
}