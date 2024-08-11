package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tasks.Task;

import java.util.Optional;
import java.util.UUID;

@Repository
interface TaskSQLRepository extends JpaRepository<Task, UUID>, TaskRepository
{
    @Query("select t from Task as t " +
            "left join fetch t.container as c " +
            "left join fetch c.group g " +
            "left join fetch g.groupMembers as gm " +
            "where t.id = :taskId")
    Optional<Task> getTaskById(@Param("taskId") UUID taskId);
}
