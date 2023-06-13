package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {

    User create(User user);

    UserDto getById(Long userId);

    Collection<UserDto> getAll();

    UserDto update(Long userId, UserDto userDto);

    UserDto updateName(Long userId, UserDto userDto);

    UserDto updateEmail(Long userId, UserDto userDto);

    boolean delete(Long userId);
}