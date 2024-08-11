package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tasks.TaskComment;

import java.util.UUID;

@Repository
interface TaskCommentSQLRepository extends JpaRepository<TaskComment, UUID>, TaskCommentRepository {}
