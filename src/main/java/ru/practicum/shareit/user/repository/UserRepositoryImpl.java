package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) throws DuplicateEmailException {
        if (checkEmail(user.getEmail()) != null) {
            log.error("User with email = {} already exists", user.getEmail());
            throw new DuplicateEmailException(
                    "User with email = " + user.getEmail() + " already exists");
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID");
        user.setId(simpleJdbcInsert.executeAndReturnKey(
                Map.of("USER_NAME", user.getName(), "EMAIL", user.getEmail())).longValue());
        return user;
    }

    private UserDto checkEmail(String email) {
        String sqlQuery = "SELECT * FROM USERS WHERE EMAIL = ?";
        try {
            return jdbcTemplate.queryForObject(sqlQuery, (resultSet, rowNum) -> UserMapper.toUserDto(resultSet), email);
        } catch (DataAccessException e) {
            log.info("Email = {} is not busy", email);
            return null;
        }
    }

    @Override
    public UserDto getById(Long userId) {
        String sqlQuery = "SELECT * FROM USERS WHERE USER_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, (resultSet, rowNum) -> UserMapper.toUserDto(resultSet), userId);
    }

    @Override
    public Collection<UserDto> getAll() {
        String sqlQuery = "SELECT * FROM USERS";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> UserMapper.toUserDto(resultSet));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) throws DuplicateEmailException {
        if (checkEmail(userDto.getEmail()) != null) {
            throw new DuplicateEmailException(
                    "User with email = " + userDto.getEmail() + " already exists");
        }

        String sqlQuery = "UPDATE USERS SET USER_NAME = ?, EMAIL = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, userDto.getName(), userDto.getEmail(), userId);
        return getById(userId);
    }

    @Override
    public UserDto updateName(Long userId, UserDto userDto) {
        String sqlQuery = "UPDATE USERS SET USER_NAME = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, userDto.getName(), userId);
        return getById(userId);
    }

    @Override
    public UserDto updateEmail(Long userId, UserDto userDto) {
        String sqlQuery = "UPDATE USERS SET EMAIL = ? WHERE USER_ID = ?";
        jdbcTemplate.update(sqlQuery, userDto.getEmail(), userId);
        return getById(userId);
    }

    @Override
    public boolean delete(Long userId) {
        String sqlQuery = "DELETE FROM USERS WHERE USER_ID = ?";
        return jdbcTemplate.update(sqlQuery, userId) > 0;
    }
}