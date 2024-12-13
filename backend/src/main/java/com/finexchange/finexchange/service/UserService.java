package com.finexchange.finexchange.service;

import com.finexchange.finexchange.cache.UserContextCache;
import com.finexchange.finexchange.dto.UserDto;
import com.finexchange.finexchange.dto.request.UserPasswordUpdateRequest;
import com.finexchange.finexchange.dto.request.UserRegisterRequest;
import com.finexchange.finexchange.exception.*;
import com.finexchange.finexchange.mapper.UserDtoMapper;
import com.finexchange.finexchange.model.User;
import com.finexchange.finexchange.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserContextCache userContextCache;

    public User getUserEntityById(String userId) {
        return userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    }

    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User createUser(UserRegisterRequest newUser) {
        boolean userEmailControl = userRepository.existsByEmail(newUser.getEmail());

        if (userEmailControl) throw new UserAlreadyExistsException();

        User user = User.builder().email(newUser.getEmail()).name(newUser.getName()).isAdmin(false).password(passwordEncoder.encode("finexchange2024")).createdAt(LocalDateTime.now()).build();

        return userRepository.save(user);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public User createAdmin(String userId) {
        User user = getUserEntityById(userId);
        if (user.isAdmin()) {
            throw new UserAlreadyAdminException();
        }
        user.setAdmin(true);
        return userRepository.save(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserDtoMapper::mapToUserDto).collect(Collectors.toList());
    }

    public String updateUserPassword(String userId, UserPasswordUpdateRequest updateRequest) {
        if (updateRequest == null || updateRequest.getNewPassword() == null || updateRequest.getCurrentPassword() == null) {
            throw new InvalidPasswordRequestException();
        }

        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException();
        }

        User user = userOptional.get();

        if (!passwordEncoder.matches(updateRequest.getCurrentPassword(), user.getPassword())) {
            throw new InvalidCurrentPasswordException();
        }

        String newHashedPassword = passwordEncoder.encode(updateRequest.getNewPassword());
        user.setPassword(newHashedPassword);
        userRepository.save(user);

        return "Şifre başarıyla güncellendi.";
    }

    public UserDto getUserDtoById() {
        return UserDtoMapper.mapToUserDto(getUserEntityById(userContextCache.getCurrentUser().getId()));
    }

    @PostConstruct
    @Transactional(propagation = Propagation.REQUIRED)
    public void init() {
        if (userRepository.count() == 0) {
            User user = User.builder().email("admin@finexchange.com").name("Admin Finexchange").isAdmin(true).password(passwordEncoder.encode("finexchange2024")).createdAt(LocalDateTime.now()).build();
            userRepository.save(user);
        }
    }
}