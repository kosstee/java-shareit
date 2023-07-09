package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.mapper.UserMapper.toUser;
import static ru.practicum.shareit.user.mapper.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public UserDto create(UserDto userDto) {
        User createdUser = repository.create(toUser(userDto));
        return toUserDto(createdUser);
    }

    @Override
    public UserDto getById(Long userId) {
        User user = repository.getById(userId);
        return toUserDto(user);
    }

    @Override
    public Collection<UserDto> getAll() {
        return repository.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        boolean isOnlyNameUpdated = userDto.getName() != null && userDto.getEmail() == null;
        boolean isOnlyEmailUpdated = userDto.getEmail() != null && userDto.getName() == null;

        if (isOnlyNameUpdated) {
            return toUserDto(repository.updateName(userId, toUser(userDto)));
        }

        if (isOnlyEmailUpdated) {
            return toUserDto(repository.updateEmail(userId, toUser(userDto)));
        }

        return toUserDto(repository.update(userId, toUser(userDto)));
    }

    @Override
    public boolean delete(Long userId) {
        return repository.delete(userId);
    }
}