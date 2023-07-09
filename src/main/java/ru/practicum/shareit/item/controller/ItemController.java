package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long userId,
                       @Valid @RequestBody ItemDto itemDto) {
        ItemDto response = itemService.addNewItem(userId, itemDto);
        log.info("Created {}", response);
        return response;
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") long userId,
                               @PathVariable Long itemId) {
        ItemDto response = itemService.getItemById(itemId);
        log.info("Received {}", response);
        return response;
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @RequestBody ItemDto itemDto,
                              @PathVariable Long itemId) {
        ItemDto response = itemService.updateItem(userId, itemDto, itemId);
        log.info("Updated {}", response);
        return response;
    }

    @GetMapping
    public Collection<ItemDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        Collection<ItemDto> response = itemService.getAllUserItems(userId);
        log.info("Received {}", response);
        return response;
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestParam String text) {
        Collection<ItemDto> response = itemService.searchItems(text);
        log.info("Found {}", response);
        return response;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable Long itemId) {
        itemService.deleteItem(userId, itemId);
    }
}