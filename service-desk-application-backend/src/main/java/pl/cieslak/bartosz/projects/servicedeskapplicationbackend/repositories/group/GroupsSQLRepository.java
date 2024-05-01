package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups.SupportGroup;

import java.util.UUID;

@Repository
interface GroupsSQLRepository extends JpaRepository<SupportGroup, UUID>, GroupsRepository {}
