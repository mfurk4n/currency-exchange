package com.finexchange.finexchange.security;

import com.finexchange.finexchange.enums.Role;
import com.finexchange.finexchange.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
public class CustomUserDetails implements UserDetails {

    public String id;
    private String username;
    private String password;
    private User user;
    private String customerId;
    private Collection<? extends GrantedAuthority> authorities;

    private CustomUserDetails(String id, String username, String password, User user, String customerId, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.user = user;
        this.customerId = customerId;
        this.authorities = authorities;
    }

    public static CustomUserDetails create(User user, String customerId) {
        List<GrantedAuthority> authoritiesList = new ArrayList<>();
        authoritiesList.add(new SimpleGrantedAuthority(Role.USER.name()));
        if (user.isAdmin()) {
            authoritiesList.add(new SimpleGrantedAuthority(Role.ADMIN.name()));
        }
        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPassword(), user, customerId, authoritiesList);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}