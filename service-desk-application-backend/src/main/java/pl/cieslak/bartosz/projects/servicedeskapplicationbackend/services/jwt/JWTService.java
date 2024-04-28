package pl.cieslak.bartosz.projects.servicedeskapplicationbackend.services.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.dto.auth.JWTToken;
import pl.cieslak.bartosz.projects.servicedeskapplicationbackend.components.entities.user.User;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class JWTService
{
    @Value("${jwt.secret}")
    private String secret;

    @Value("${minutes.until.token.expires}")
    private long minutesUntilTokenExpires;

    @Value("${minutes.until.refresh.token.expires}")
    private long minutesUntilRefreshTokenExpires;

    private Key getSignKey()
    {
        byte[] keyBytes= Decoders.BASE64.decode(this.secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public JWTToken createToken(User user)
    {
        JWTToken token = new JWTToken();
        token.setExpiration(new Date(System.currentTimeMillis() + 1000L * 60L * minutesUntilTokenExpires).getTime());

        String jwt = Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(token.getExpiration()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();

        token.setToken(jwt);

        return token;
    }

    public JWTToken createRefreshToken(User user)
    {
        JWTToken token = new JWTToken();
        token.setExpiration(new Date(System.currentTimeMillis() + 1000L * 60L * minutesUntilRefreshTokenExpires).getTime());

        String jwt = Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(token.getExpiration()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();

        token.setToken(jwt);

        return token;
    }

    public boolean validateToken(String token)
    {
        if(token == null || token.trim().isEmpty()) return false;

        try
        {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration().after(new Date());
        }
        catch(Exception exception)
        {
            return false;
        }
    }

    public UUID extractUserIdFromToken(String token)
    {
        try
        {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return UUID.fromString(claims.getSubject());
        }
        catch(Exception exception)
        {
            return null;
        }
    }
}
