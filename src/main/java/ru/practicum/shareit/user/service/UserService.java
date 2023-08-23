package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto getById(Long userId);

    Collection<UserDto> getAll();

    UserDto update(Long userId, UserDto userDto);

    void delete(Long userId);
}