package com.finexchange.finexchange.mapper;

import com.finexchange.finexchange.dto.UserDto;
import com.finexchange.finexchange.model.User;

public final class UserDtoMapper {

    private UserDtoMapper() {
    }

    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .isAdmin(user.isAdmin())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
