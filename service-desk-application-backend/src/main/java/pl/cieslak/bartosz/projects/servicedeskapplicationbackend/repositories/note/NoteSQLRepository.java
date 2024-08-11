package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.note;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.Note;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteSQLRepository extends JpaRepository<Note, UUID>, NoteRepository
{
    @Query("select n from Note as n left join fetch n.owner as o where o.id = :userId and n.deleted = false order by n.lastUpdateAt desc")
    List<Note> getNotesByUserId(@Param("userId") UUID userId);

    @Query("select n from Note as n left join fetch n.owner as o where n.id = :noteId and n.deleted = false")
    Optional<Note> getNoteById(@Param("noteId") UUID noteId);
}
