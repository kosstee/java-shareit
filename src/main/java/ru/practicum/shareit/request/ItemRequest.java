package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Builder
@Data
public class ItemRequest {

    private Long id;
    private User requestor;
    private LocalDateTime created;
}