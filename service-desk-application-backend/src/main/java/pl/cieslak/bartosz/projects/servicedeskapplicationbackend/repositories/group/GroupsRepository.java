package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.group;

import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups.SupportGroup;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupsRepository
{
    SupportGroup saveAndFlush(SupportGroup group);
    List<SupportGroup> getAllGroups();
    Optional<SupportGroup> getSupportGroupByName(String name);
    Optional<SupportGroup> findById(UUID groupId);
    Optional<SupportGroup> getSupportGroupAndMembersById(UUID id);
    Optional<SupportGroup> getSupportGroupAndManagerById(UUID groupId);
    List<User> getMembersFromGroup(UUID groupId);
}
