package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

    ItemDto addNewItem(Long userId, ItemDto itemDto);

    ItemDto getItemById(Long itemId);

    ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId);

    Collection<ItemDto> getAllUserItems(Long userId);

    Collection<ItemDto> searchItems(String text);

    void deleteItem(Long userId, long itemId);
}