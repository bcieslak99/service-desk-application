package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.tasks.TaskSet;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface TaskSetSQLRepository extends JpaRepository<TaskSet, UUID>, TaskSetRepository
{
    @Query("select ts from TaskSet as ts " +
            "left join fetch ts.group as g " +
            "left join fetch g.groupMembers as gm " +
            "where gm.id = :userId " +
            "order by ts.plannedEndDate, ts.group.name, ts.title, ts.createdAt")
    List<TaskSet> getUserTasks(@Param("userId") UUID userId);

    @Query("select ts from TaskSet as ts " +
            "left join ts.comments as c " +
            "left join ts.tasks as t " +
            "left join ts.group as g " +
            "left join g.groupMembers as gm " +
            "where ts.id = :taskId " +
            "order by t.position asc, c.createdAt desc")
    Optional<TaskSet> getTask(@Param("taskId") UUID taskId);
}
