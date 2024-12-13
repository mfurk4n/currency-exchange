package com.finexchange.finexchange.service;

import com.finexchange.finexchange.dto.CustomerDto;
import com.finexchange.finexchange.dto.request.RefreshTokenRequest;
import com.finexchange.finexchange.dto.request.UserLoginRequest;
import com.finexchange.finexchange.dto.request.UserRegisterRequest;
import com.finexchange.finexchange.dto.response.AuthResponse;
import com.finexchange.finexchange.exception.InvalidLoginException;
import com.finexchange.finexchange.model.RefreshToken;
import com.finexchange.finexchange.model.User;
import com.finexchange.finexchange.security.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final RefreshTokenService refreshTokenService;
    private final CustomerService customerService;

    public AuthResponse loginUser(UserLoginRequest userLoginRequest) {
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userLoginRequest.getEmail(), userLoginRequest.getPassword());
            Authentication auth = authenticationManager.authenticate(authToken);
            User user = userService.getUserEntityByEmail(userLoginRequest.getEmail());
            SecurityContextHolder.getContext().setAuthentication(auth);
            String jwtToken = jwtTokenUtils.generateJwtToken(auth);
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage("Logged in successfully");
            authResponse.setJwtToken(jwtToken);
            authResponse.setRefreshToken(refreshTokenService.createRefreshToken(user));
            authResponse.setUserId(user.getId());
            authResponse.setAdmin(user.isAdmin());
            List<CustomerDto> customerDtoList = customerService.getAllCustomersDtoByUserId(Optional.of(user.getId()));
            authResponse.setCustomers(customerDtoList);
            return authResponse;
        } catch (BadCredentialsException e) {
            throw new InvalidLoginException();
        }

    }

    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        AuthResponse authResponse = new AuthResponse();
        RefreshToken token = refreshTokenService.getByUser(refreshTokenRequest.getUserId());
        if (token.getToken().equals(refreshTokenRequest.getRefreshToken()) &&
                !refreshTokenService.isRefreshExpired(token)) {
            User user = token.getUser();
            String jwtToken = jwtTokenUtils.generateJwtTokenByUserId(user.getId());
            authResponse.setMessage("Token refreshed");
            authResponse.setJwtToken(jwtToken);
            authResponse.setUserId(user.getId());
        } else {
            authResponse.setMessage("Invalid Refresh Token");
        }

        return authResponse;
    }

}
