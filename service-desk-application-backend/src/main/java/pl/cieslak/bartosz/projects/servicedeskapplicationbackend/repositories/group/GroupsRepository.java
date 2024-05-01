package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.group;

import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups.SupportGroup;

public interface GroupsRepository
{
    SupportGroup saveAndFlush(SupportGroup group);
}
