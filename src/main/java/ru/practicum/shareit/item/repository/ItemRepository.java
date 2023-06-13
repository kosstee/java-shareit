package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {

    ItemDto addNewItem(Long userId, ItemDto itemDto);

    Item getById(Long itemId);

    Item updateItem(ItemDto itemDto, Long itemId);

    Collection<Item> getAllUserItems(Long userId);

    Collection<Item> searchItems(String text);
}