package com.finexchange.finexchange.service;

import com.finexchange.finexchange.exception.UserNotFoundException;
import com.finexchange.finexchange.model.User;
import com.finexchange.finexchange.repository.UserRepository;
import com.finexchange.finexchange.security.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetails loadUserById(String userId, String customerId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return CustomUserDetails.create(user, customerId);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        return CustomUserDetails.create(user, "");
    }
}