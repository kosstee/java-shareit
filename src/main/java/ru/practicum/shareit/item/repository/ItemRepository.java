package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository {

    Item addNewItem(Long userId, Item item);

    Item getById(Long itemId);

    Item updateItem(Item item, Long itemId);

    Collection<Item> getAllUserItems(Long userId);

    Collection<Item> searchItems(String text);
}