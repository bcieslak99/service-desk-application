package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.repositories.user.UserRepository;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.user.UserEntityDetailsService;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter
{
    private final JWTService JWT_SERVICE;
    private final UserEntityDetailsService USER_ENTITY_DETAILS_SERVICE;
    private final UserRepository USER_REPOSITORY;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userMail = null;

        final String TOKEN_PREFIX = "Bearer ";
        if(authHeader != null && authHeader.startsWith(TOKEN_PREFIX))
        {
            token = authHeader.substring(TOKEN_PREFIX.length());

            UUID userId = JWT_SERVICE.extractUserIdFromToken(token);

            if(userId != null)
            {
                Optional<User> userInDatabase = this.USER_REPOSITORY.findById(userId);

                if(userInDatabase.isPresent())
                    userMail = userInDatabase.get().getMail();
            }
        }

        if(userMail != null && SecurityContextHolder.getContext().getAuthentication() == null)
        {
            UserDetails userDetails = USER_ENTITY_DETAILS_SERVICE.loadUserByUsername(userMail);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
