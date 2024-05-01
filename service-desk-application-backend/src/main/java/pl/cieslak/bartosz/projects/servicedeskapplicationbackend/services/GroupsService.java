package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.group.GroupsRepository;

@Service
@RequiredArgsConstructor
public class GroupsService
{
    private final GroupsRepository GROUP_REPOSITORY;
}
