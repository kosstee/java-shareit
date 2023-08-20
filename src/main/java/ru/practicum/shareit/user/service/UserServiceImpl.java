package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
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
    @Transactional
    public UserDto create(UserDto userDto) {
        User createdUser = repository.save(toUser(userDto));
        return toUserDto(createdUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(Long userId) {
        User user = repository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with id = " + userId + " not found"));
        return toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<UserDto> getAll() {
        return repository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        repository.updateUserFields(toUser(userDto), userId);
        return toUserDto(repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id = " + userId + " not found")));
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        repository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User with id = " + userId + " not found"));
        repository.deleteById(userId);
    }
}