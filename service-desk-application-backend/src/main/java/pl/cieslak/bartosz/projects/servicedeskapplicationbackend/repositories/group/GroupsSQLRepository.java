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
    @Query("select sg from SupportGroup as sg left join fetch sg.groupMembers as gm order by sg.groupActive desc, sg.name")
    List<SupportGroup> getAllGroups();

    Optional<SupportGroup> getSupportGroupByName(String name);

    @Query("select sg from SupportGroup as sg left join fetch sg.groupMembers as gm where sg.id = :id order by gm.surname, gm.name, gm.mail")
    Optional<SupportGroup> getSupportGroupAndMembersById(@Param("id") UUID id);

    @Query("select sg from SupportGroup as sg left join fetch sg.groupManager where sg.id = :id")
    Optional<SupportGroup> getSupportGroupAndManagerById(@Param("id") UUID groupId);
}
