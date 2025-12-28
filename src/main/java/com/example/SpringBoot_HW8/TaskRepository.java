package com.example.SpringBoot_HW8;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TaskRepository {

    private final List<Task> tasks = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public List<Task> findAll() {
        return new ArrayList<>(tasks);
    }

    public Task findById(Long id) {
        return tasks.stream()
                .filter(task -> task.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(idCounter.getAndIncrement());
        }
        tasks.add(task);
        return task;
    }

    public Task update(Long id, Task updatedTask) {
        Task existingTask = findById(id);
        if (existingTask != null) {
            existingTask.setTitle(updatedTask.getTitle());
            existingTask.setDescription(updatedTask.getDescription());
            existingTask.setCompleted(updatedTask.getCompleted());
            return existingTask;
        }
        return null;
    }

    public boolean deleteById(Long id) {
        return tasks.removeIf(task -> task.getId().equals(id));
    }

    public void deleteAll() {
        tasks.clear();
    }

    public boolean existsById(Long id) {
        return tasks.stream()
                .anyMatch(task -> task.getId().equals(id));
    }
}