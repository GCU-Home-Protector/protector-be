package com.gachon.home_protector.security.userdetails;

import com.gachon.home_protector.user.dto.RestUserLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@RequiredArgsConstructor
public class RestUserDetails implements UserDetails {

    private final RestUserLoginResponse restUser;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(restUser.getRole()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return restUser.getPassword();
    }

    @Override
    public String getUsername() {
        return restUser.getUserId();
    }

    public Long getId() {
        return restUser.getId();
    }

    public void removePassword() {
        restUser.removePassword();
    }

    public RestUserLoginResponse getUser() {
        return restUser;
    }
}
