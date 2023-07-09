package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {

    User create(User user);

    User getById(Long userId);

    Collection<User> getAll();

    User update(Long userId, User user);

    User updateName(Long userId, User user);

    User updateEmail(Long userId, User user);

    boolean delete(Long userId);
}