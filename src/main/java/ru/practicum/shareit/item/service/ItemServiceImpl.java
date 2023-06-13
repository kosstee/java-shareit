package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotEnoughRightsToEditException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        return repository.addNewItem(userId, itemDto);
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(repository.getById(itemId));
    }

    @Override
    public ItemDto updateItem(
            Long userId, Map<String, Object> updates, Long itemId) throws NotEnoughRightsToEditException {
        Item item = repository.getById(itemId);
        boolean isUserOwnerItem = Objects.equals(item.getOwnerId(), userId);

        if (!isUserOwnerItem) {
            log.error("Not enough rights to edit item with id = {}", itemId);
            throw new NotEnoughRightsToEditException(
                    "Not enough rights to edit item with id = " + itemId);
        }

        ItemDto itemDto = ItemMapper.toItemDto(item);

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            switch (entry.getKey()) {
                case "name":
                    itemDto.setName(entry.getValue().toString());
                    break;
                case "description":
                    itemDto.setDescription(entry.getValue().toString());
                    break;
                case "available":
                    itemDto.setAvailable(Boolean.valueOf(entry.getValue().toString()));
                    break;
            }
        }
        return ItemMapper.toItemDto(repository.updateItem(itemDto, itemId));
    }

    @Override
    public Collection<ItemDto> getAllUserItems(Long userId) {
        return repository.getAllUserItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        return repository.searchItems(text.toLowerCase()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public void deleteItem(Long userId, long itemId) {
    }
}