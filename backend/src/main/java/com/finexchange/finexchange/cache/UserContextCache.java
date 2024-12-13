package com.finexchange.finexchange.cache;

import com.finexchange.finexchange.exception.AuthenticatedUserNotFoundException;
import com.finexchange.finexchange.security.CustomUserDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserContextCache {

    public CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || (authentication instanceof AnonymousAuthenticationToken)) {
            throw new AuthenticatedUserNotFoundException();
        } else {
            return (CustomUserDetails) authentication.getPrincipal();
        }
    }
}
