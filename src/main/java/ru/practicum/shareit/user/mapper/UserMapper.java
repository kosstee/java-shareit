package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {

    public static UserDto toUserDto(ResultSet resultSet) throws SQLException {
        return UserDto.builder()
                .id(resultSet.getLong("USER_ID"))
                .name(resultSet.getString("USER_NAME"))
                .email(resultSet.getString("EMAIL"))
                .build();
    }
}