package az.nicat.projects.resumejobmatchingapp.security.jwt;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtCredentials {

    private List<String> authority;
    private String sub;
    private Long iat;
    private Long exp;
    private String email;
    private String username;

    public JwtCredentials() {
    }

    public JwtCredentials(List<String> authority, String sub, Long iat, Long exp, String email, String username) {
        this.authority = authority;
        this.sub = sub;
        this.iat = iat;
        this.exp = exp;
        this.email = email;
        this.username = username;
    }

    public List<String> getAuthority() {
        return authority;
    }

    public void setAuthority(List<String> authority) {
        this.authority = authority;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public Long getIat() {
        return iat;
    }

    public void setIat(Long iat) {
        this.iat = iat;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}