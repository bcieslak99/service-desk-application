package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.groups.GroupType;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.configuration.SecurityConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class UserEntityDetails implements UserDetails
{
    private UUID userId;
    private String mail;
    private String password;
    private boolean isEnabled;
    private List<GrantedAuthority> authorities;


    public UserEntityDetails(User user)
    {
        this.userId = user.getId();
        this.mail = user.getMail();
        this.password = user.getPassword();
        this.isEnabled = user.isActive();

        this.authorities = new ArrayList<>();

        if(user.isActive())
        {
            this.authorities.add(new SimpleGrantedAuthority(SecurityConfiguration.ACTIVE_USER_ROLE_NAME));

            if(user.isAccessAsEmployeeIsPermitted())
                this.authorities.add(new SimpleGrantedAuthority(SecurityConfiguration.EMPLOYEE_ROLE_NAME));

            if(user.isAdministrator())
                this.authorities.add(new SimpleGrantedAuthority(SecurityConfiguration.ADMINISTRATOR_ROLE_NAME));

            if(user.getUserGroups().stream().anyMatch(group -> group.isGroupActive() && group.getGroupType().equals(GroupType.FIRST_LINE)))
                this.authorities.add(new SimpleGrantedAuthority(SecurityConfiguration.FIRST_LINE_ANALYST_ROLE_NAME));

            if(user.getUserGroups().stream().anyMatch(group -> group.isGroupActive() && group.getGroupType().equals(GroupType.SECOND_LINE)))
                this.authorities.add(new SimpleGrantedAuthority(SecurityConfiguration.SECOND_LINE_ANALYST_ROLE_NAME));
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return this.authorities;
    }

    @Override
    public String getPassword()
    {
        return this.password;
    }

    @Override
    public String getUsername()
    {
        return this.userId.toString();
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return this.isEnabled;
    }

    public boolean userIsAnalyst()
    {
        return this.authorities.stream()
                .anyMatch(authority -> authority.getAuthority().equals(SecurityConfiguration.FIRST_LINE_ANALYST_ROLE_NAME) ||
                        authority.getAuthority().equals(SecurityConfiguration.SECOND_LINE_ANALYST_ROLE_NAME));
    }
}
