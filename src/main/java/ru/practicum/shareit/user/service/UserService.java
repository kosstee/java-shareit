package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    User create(User user);

    UserDto getById(Long userId);

    Collection<UserDto> getAll();

    UserDto update(Long userId, UserDto userDto);

    boolean delete(Long userId);
}
