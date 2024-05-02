package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups.SupportGroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
interface GroupsSQLRepository extends JpaRepository<SupportGroup, UUID>, GroupsRepository
{
    @Query("select sg from SupportGroup as sg left join fetch sg.groupMembers as gm where sg.groupActive = true order by sg.name")
    List<SupportGroup> getActiveGroups();

    Optional<SupportGroup> getSupportGroupByName(String name);

    @Query("select sg from SupportGroup as sg left join fetch sg.groupMembers as gm where sg.id = :id")
    Optional<SupportGroup> getSupportGroupAndMembersById(@Param("id") UUID id);
}
