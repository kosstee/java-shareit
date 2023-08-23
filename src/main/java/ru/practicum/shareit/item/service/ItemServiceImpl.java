package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingClosest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.CommentNotAuthorisedException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.mapper.CommentMapper.toComment;
import static ru.practicum.shareit.item.mapper.CommentMapper.toCommentResponseDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItem;
import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        User user = checkPresenceAndReturnUserOrElseThrow(userId);
        Item createdItem = toItem(itemDto);
        createdItem.setOwner(user);
        itemRepository.save(createdItem);
        return toItemDto(createdItem);
    }

    @Override
    @Transactional
    public ItemDto getItemById(Long itemId, Long userId) {
        Item item = checkPresenceAndReturnItemOrElseThrow(itemId);
        ItemDto itemDto = toItemDto(item);
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        itemDto.setComments(toCommentResponseDto(comments));

        if (itemDto.getOwnerId().equals(userId)) {
            List<BookingClosest> nextBookingClosest =
                    bookingRepository.findNextClosestBookingByOwnerId(userId, itemId);

            List<BookingClosest> lastBookingClosest =
                    bookingRepository.findLastClosestBookingByOwnerId(userId, itemId);

            if (!nextBookingClosest.isEmpty()) {
                itemDto.setNextBooking(nextBookingClosest.get(0));
            }

            if (!lastBookingClosest.isEmpty()) {
                itemDto.setLastBooking(lastBookingClosest.get(0));
            }
        }

        return itemDto;
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        checkPresenceAndReturnUserOrElseThrow(userId);
        itemRepository.updateItemFields(toItem(itemDto), userId, itemId);
        ItemDto updatedItemDto = toItemDto(checkPresenceAndReturnItemOrElseThrow(itemId));
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        updatedItemDto.setComments(toCommentResponseDto(comments));
        return updatedItemDto;
    }

    @Override
    public Collection<ItemDto> getAllUserItems(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<Long> ids = getItemsIds(items);
        List<ItemDto> itemsDto = combineItemsWithComments(items, ids);

        for (ItemDto i : itemsDto) {
            List<BookingClosest> nextBookingClosest =
                    bookingRepository.findNextClosestBookingByOwnerId(userId, i.getId());

            List<BookingClosest> lastBookingClosest =
                    bookingRepository.findLastClosestBookingByOwnerId(userId, i.getId());

            if (!nextBookingClosest.isEmpty()) {
                i.setNextBooking(nextBookingClosest.get(0));
            }

            if (!lastBookingClosest.isEmpty()) {
                i.setLastBooking(lastBookingClosest.get(0));
            }
        }

        return itemsDto;
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> items = itemRepository.searchItemByNameOrDescription(text);
        List<Long> ids = getItemsIds(items);
        return combineItemsWithComments(items, ids);
    }

    @Override
    @Transactional
    public List<ItemDto> getItems() {
        List<Item> items = itemRepository.findAll();
        List<Long> ids = getItemsIds(items);
        return combineItemsWithComments(items, ids);
    }

    public void deleteItem(Long userId, long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(CommentRequestDto commentRequestDto, Long bookerId, Long itemId) {
        User user = checkPresenceAndReturnUserOrElseThrow(bookerId);
        Item item = checkPresenceAndReturnItemOrElseThrow(itemId);

        List<Booking> bookings = bookingRepository.findAllByBookerIdPast(bookerId);

        if (bookings.isEmpty()) {
            throw new CommentNotAuthorisedException("Booking from user " + bookerId + " for item " + itemId + " doesn't exist");
        }

        Booking booking = new Booking();
        for (Booking b : bookings) {
            if (b.getItem().getId().equals(itemId)) {
                booking = b;
            }
        }

        Comment comment = toComment(commentRequestDto);

        ZoneId zoneId = ZoneId.of("Europe/Moscow");
        ZonedDateTime moscowDateTime = ZonedDateTime.now(zoneId);
        comment.setCreated(moscowDateTime.plusMinutes(1));

        if (ZonedDateTime.of(booking.getEnd(), zoneId).isAfter(comment.getCreated())) {
            throw new CommentNotAuthorisedException("Comment field created must be after booking end");
        }
        if (comment.getText().isEmpty()) {
            throw new CommentNotAuthorisedException("Comment text should not be empty");
        }

        comment.setItem(item);
        comment.setAuthor(user);
        commentRepository.save(comment);

        return toCommentResponseDto(comment);
    }

    private User checkPresenceAndReturnUserOrElseThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + userId + " not found"));
    }

    private Item checkPresenceAndReturnItemOrElseThrow(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item with id " + itemId + " not found"));
    }

    private List<ItemDto> combineItemsWithComments(List<Item> items, List<Long> ids) {
        List<Comment> comments = commentRepository.findAllByItemIdIn(ids);
        List<ItemDto> itemsDto = new ArrayList<>();
        for (Item i : items) {
            List<CommentResponseDto> itemComments = comments.stream()
                    .filter(c -> c.getItem().getId().equals(i.getId()))
                    .map(CommentMapper::toCommentResponseDto)
                    .collect(Collectors.toList());
            ItemDto dto = toItemDto(i);
            dto.setComments(itemComments);
            itemsDto.add(dto);
        }
        return itemsDto;
    }

    private List<Long> getItemsIds(List<Item> items) {
        return items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
    }
}