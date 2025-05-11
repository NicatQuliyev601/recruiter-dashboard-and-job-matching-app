package az.nicat.projects.resumejobmatchingapp.security.service;

import az.nicat.projects.resumejobmatchingapp.security.jwt.JwtCredentials;
import az.nicat.projects.resumejobmatchingapp.security.jwt.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TokenAuthService {

    private final static String BEARER = "Bearer ";
    private final static String AUTHORITY_CLAIM = "authority";
    private final JwtService jwtService;
    private final ModelMapper modelMapper;

    private static final Logger logger = LogManager.getLogger(TokenAuthService.class);


    public TokenAuthService(JwtService jwtService, ModelMapper modelMapper) {
        this.jwtService = jwtService;
        this.modelMapper = modelMapper;
    }

    public Optional<Authentication> getAuthentication(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(this::isBearerAuth)
                .flatMap(this::getAuthenticationBearer);
    }

    private boolean isBearerAuth(String header) {
        return header.toLowerCase().startsWith(BEARER.toLowerCase());
    }


    private Optional<Authentication> getAuthenticationBearer(String header) {
        String token = header.substring(BEARER.length()).trim();
        logger.trace("Token before parsing: {}", token);
        Claims claims = jwtService.parseToken(token);
        logger.trace("The claims parsed {}", claims);
        if (claims.getExpiration().before(new Date())) {
            return Optional.empty();
        }
        return Optional.of(getAuthenticationBearer(claims));
    }


    private Authentication getAuthenticationBearer(Claims claims) {
        List<?> roles = claims.get(AUTHORITY_CLAIM, List.class);
        List<GrantedAuthority> authorityList = roles
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .collect(Collectors.toList());
        JwtCredentials credentials = modelMapper.map(claims, JwtCredentials.class);

        User user = new User(credentials.getSub(), "", authorityList);

        return new UsernamePasswordAuthenticationToken(credentials, user, authorityList);
    }
}
