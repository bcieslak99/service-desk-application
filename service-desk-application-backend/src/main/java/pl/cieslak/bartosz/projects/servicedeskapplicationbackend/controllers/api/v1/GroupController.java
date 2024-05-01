package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.controllers.api.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.GroupsService;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/group")
public class GroupController
{
    private final GroupsService GROUP_SERVICE;
}
