package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Builder
@Data
public class UserDto {

    private Long id;
    private String name;
    @Email
    @NotNull
    private String email;
}