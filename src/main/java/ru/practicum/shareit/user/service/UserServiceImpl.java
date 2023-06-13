package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public User create(User user) {
        return repository.create(user);
    }

    @Override
    public UserDto getById(Long userId) {
        return repository.getById(userId);
    }

    @Override
    public Collection<UserDto> getAll() {
        return repository.getAll();
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        boolean isOnlyNameUpdated = userDto.getName() != null && userDto.getEmail() == null;
        boolean isOnlyEmailUpdated = userDto.getEmail() != null && userDto.getName() == null;

        if (isOnlyNameUpdated) {
            return repository.updateName(userId, userDto);
        }

        if (isOnlyEmailUpdated) {
            return repository.updateEmail(userId, userDto);
        }

        return repository.update(userId, userDto);
    }

    @Override
    public boolean delete(Long userId) {
        return repository.delete(userId);
    }
}