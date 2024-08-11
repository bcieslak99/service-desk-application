package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.task;

import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tasks.TaskSet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskSetRepository
{
    TaskSet saveAndFlush(TaskSet taskSet);
    List<TaskSet> getUserTasks( UUID userId);
    Optional<TaskSet> getTask(UUID taskId);
}
