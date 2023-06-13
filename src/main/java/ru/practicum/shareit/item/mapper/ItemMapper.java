package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemMapper {
    public static Item toItem(ResultSet resultSet) throws SQLException {
        return Item.builder()
                .id(resultSet.getLong("ITEM_ID"))
                .name(resultSet.getString("ITEM_NAME"))
                .description(resultSet.getString("ITEM_DESCRIPTION"))
                .available(resultSet.getBoolean("AVAILABLE"))
                .ownerId(resultSet.getLong("OWNER_ID"))
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }
}