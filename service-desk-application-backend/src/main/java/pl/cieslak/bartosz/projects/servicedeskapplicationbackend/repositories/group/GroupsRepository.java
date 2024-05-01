package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.group;

import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups.SupportGroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupsRepository
{
    SupportGroup saveAndFlush(SupportGroup group);
    List<SupportGroup> getActiveGroups();
    Optional<SupportGroup> getSupportGroupByName(String name);
    Optional<SupportGroup> findById(UUID groupId);
    Optional<SupportGroup> getSupportGroupAndMembersById(UUID id);
}
