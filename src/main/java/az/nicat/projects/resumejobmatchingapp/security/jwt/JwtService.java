package az.nicat.projects.resumejobmatchingapp.security.jwt;

import az.nicat.projects.resumejobmatchingapp.entity.Authority;
import az.nicat.projects.resumejobmatchingapp.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtService {

    @Value("${spring.security.secret}")
    private String secret;

    @Value("${spring.security.token.expirationTime}")
    private Long expTimeInMinutes;
    private Key key;


    @PostConstruct
    public void init() {
        byte[] keyBytes;
        keyBytes = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String issueToken(User user) {
        Map<String, Object> header = new HashMap<>();
        header.put("type", "JWT");

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(Duration.ofMinutes(expTimeInMinutes))))
                .setHeader(header)
                .signWith(key, SignatureAlgorithm.HS512)
                .claim("username", user.getUsername())
                .claim("authority", user.getAuthorities().stream().map(Authority::getAuthority).collect(Collectors.toList()))
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
