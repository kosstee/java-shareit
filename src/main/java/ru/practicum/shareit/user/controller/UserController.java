package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    User create(@Valid @NotNull @RequestBody User user) {
        User response = userService.create(user);
        log.info("Created {}", response);
        return response;
    }

    @GetMapping("/{id}")
    UserDto getUserById(@PathVariable("id") Long userId) {
        UserDto response = userService.getById(userId);
        log.info("Received {}", response);
        return response;
    }

    @GetMapping
    Collection<UserDto> getAll() {
        Collection<UserDto> response = userService.getAll();
        log.info("Received {}", response);
        return response;
    }

    @PatchMapping("/{id}")
    UserDto update(@PathVariable("id") Long userId, @RequestBody UserDto userDto) {
        UserDto response = userService.update(userId, userDto);
        log.info("Updated {}", response);
        return response;
    }

    @DeleteMapping("/{id}")
    boolean delete(@PathVariable("id") Long userId) {
        boolean response = userService.delete(userId);
        log.info("Deleting user with id = {}: {}", userId, response);
        return response;
    }
}