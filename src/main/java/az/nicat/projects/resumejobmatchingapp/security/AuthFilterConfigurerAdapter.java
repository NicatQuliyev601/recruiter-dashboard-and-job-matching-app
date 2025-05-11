package az.nicat.projects.resumejobmatchingapp.security;

import az.nicat.projects.resumejobmatchingapp.security.filter.AuthRequestFilter;
import az.nicat.projects.resumejobmatchingapp.security.service.TokenAuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class AuthFilterConfigurerAdapter extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenAuthService tokenAuthService;

    private static final Logger logger = LogManager.getLogger(AuthFilterConfigurerAdapter.class);


    public AuthFilterConfigurerAdapter(TokenAuthService tokenAuthService) {
        this.tokenAuthService = tokenAuthService;
    }

    @Override
    public void configure(HttpSecurity http) {
        logger.trace("Added auth request filter");
        http.addFilterBefore(new AuthRequestFilter(tokenAuthService), UsernamePasswordAuthenticationFilter.class);
    }
}