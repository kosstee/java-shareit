package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    ItemDto addNewItem(Long userId, ItemDto itemDto);

    ItemDto getItemById(Long itemId, Long userId);

    ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId);

    Collection<ItemDto> getAllUserItems(Long userId);

    Collection<ItemDto> searchItems(String text);

    List<ItemDto> getItems();

    CommentResponseDto addComment(CommentRequestDto commentRequestDto, Long bookerId, Long itemId);

    void deleteItem(Long userId, long itemId);
}