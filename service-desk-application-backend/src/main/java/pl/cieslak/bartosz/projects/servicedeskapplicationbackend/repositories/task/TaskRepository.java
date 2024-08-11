package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.task;

import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tasks.Task;

import java.util.Optional;
import java.util.UUID;

public interface TaskRepository
{
    Task saveAndFlush(Task task);
    Optional<Task> getTaskById(UUID taskId);
}
