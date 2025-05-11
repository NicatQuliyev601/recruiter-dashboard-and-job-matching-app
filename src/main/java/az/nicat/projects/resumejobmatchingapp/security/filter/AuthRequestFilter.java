package az.nicat.projects.resumejobmatchingapp.security.filter;

import az.nicat.projects.resumejobmatchingapp.security.service.TokenAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

public class AuthRequestFilter extends OncePerRequestFilter {

    private final TokenAuthService tokenAuthService;

    private static final Logger logger = LogManager.getLogger(AuthRequestFilter.class);


    public AuthRequestFilter(TokenAuthService tokenAuthService) {
        this.tokenAuthService = tokenAuthService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Optional<Authentication> authentication = tokenAuthService.getAuthentication(request);
        authentication.ifPresent(auth -> {
            SecurityContextHolder.getContext().setAuthentication(auth);
            logger.info("AUTHENTICATION IS {}", auth);
        });

        logger.info("Auth request filter is working!!");
        filterChain.doFilter(request, response);
    }
}