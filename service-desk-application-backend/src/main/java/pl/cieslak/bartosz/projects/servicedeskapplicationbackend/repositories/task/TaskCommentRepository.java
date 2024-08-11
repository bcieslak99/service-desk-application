package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.task;

import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tasks.TaskComment;

public interface TaskCommentRepository
{
    TaskComment saveAndFlush(TaskComment taskComment);
}
