package az.nicat.projects.resumejobmatchingapp.security.service;

import az.nicat.projects.resumejobmatchingapp.security.jwt.JwtCredentials;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityService {

    private final ModelMapper mapper;

    public SecurityService(ModelMapper mapper) {
        this.mapper = mapper;
    }


    public JwtCredentials getCurrentJwtCredentials() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.of(securityContext.getAuthentication())
                .map(authentication -> mapper.map(authentication.getPrincipal(), JwtCredentials.class))
                .orElseThrow(RuntimeException::new);
    }
}