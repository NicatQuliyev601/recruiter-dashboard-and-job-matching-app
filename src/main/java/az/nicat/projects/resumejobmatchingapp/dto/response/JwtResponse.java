package az.nicat.projects.resumejobmatchingapp.dto.response;


import az.nicat.projects.resumejobmatchingapp.entity.Authority;

import java.util.List;

public class JwtResponse {
    private Long id;
    String jwt;
    private List<Authority> authorities;

    public JwtResponse(Long id, String jwt, List<Authority> authorities) {
        this.id = id;
        this.jwt = jwt;
        this.authorities = authorities;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }
}