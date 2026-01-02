package com.example.SpringBoot_HW8;

import com.example.SpringBoot_HW8.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;


    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void shouldCreateTask() throws Exception {
        Task task = new Task(null, "Купить молоко", "2 литра", false);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Купить молоко"))
                .andExpect(jsonPath("$.description").value("2 литра"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void shouldGetAllTasks() throws Exception {
        // Создаём две задачи
        taskRepository.save(new Task(null, "Задача 1", "Описание 1", false));
        taskRepository.save(new Task(null, "Задача 2", "Описание 2", true));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Задача 1"))
                .andExpect(jsonPath("$[1].title").value("Задача 2"));
    }

    @Test
    void shouldGetTaskById() throws Exception {
        Task savedTask = taskRepository.save(
                new Task(null, "Тестовая задача", "Описание", false));

        mockMvc.perform(get("/tasks/" + savedTask.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Тестовая задача"));
    }

    @Test
    void shouldReturn404WhenTaskNotFound() throws Exception {
        mockMvc.perform(get("/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateTask() throws Exception {
        Task savedTask = taskRepository.save(
                new Task(null, "Старое название", "Старое описание", false));

        Task updatedTask = new Task(null, "Новое название", "Новое описание", true);

        mockMvc.perform(put("/tasks/" + savedTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Новое название"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void shouldDeleteTask() throws Exception {
        Task savedTask = taskRepository.save(
                new Task(null, "Задача для удаления", "Описание", false));

        mockMvc.perform(delete("/tasks/" + savedTask.getId()))
                .andExpect(status().isNoContent());

        // Проверяем, что задача действительно удалена
        mockMvc.perform(get("/tasks/" + savedTask.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldValidateEmptyTitle() throws Exception {
        Task task = new Task(null, "", "Описание", false);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldValidateNullCompleted() throws Exception {
        Task task = new Task(null, "Название", "Описание", null);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest());
    }
}