package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.Collection;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public ItemDto addNewItem(Long userId, ItemDto itemDto) throws DataAccessException {
        String sqlQueryToCheckIfUserExists = "SELECT * FROM USERS WHERE USER_ID = ?";
        try {
            jdbcTemplate.queryForObject(
                    sqlQueryToCheckIfUserExists, (resultSet, rowNum) -> UserMapper.toUserDto(resultSet), userId);
        } catch (DataAccessException e) {
            log.error("User with id = {} not found", userId);
            throw new UserNotFoundException("User with id = " + userId + " not found");
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("ITEMS")
                .usingGeneratedKeyColumns("ITEM_ID");

        itemDto.setId(simpleJdbcInsert.executeAndReturnKey(
                Map.of("ITEM_NAME", itemDto.getName(),
                        "ITEM_DESCRIPTION", itemDto.getDescription(),
                        "AVAILABLE", itemDto.getAvailable(),
                        "OWNER_ID", userId)).longValue());
        return itemDto;
    }

    @Override
    public Item getById(Long itemId) {
        String sqlQuery = "SELECT * FROM ITEMS WHERE ITEM_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, (resultSet, rowNum) -> ItemMapper.toItem(resultSet), itemId);
    }

    @Override
    public Item updateItem(ItemDto itemDto, Long itemId) {
        String sqlQuery = "UPDATE ITEMS SET ITEM_NAME = ?, ITEM_DESCRIPTION = ?, AVAILABLE = ? WHERE ITEM_ID = ?";
        jdbcTemplate.update(
                sqlQuery,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemId);
        return getById(itemId);
    }

    @Override
    public Collection<Item> getAllUserItems(Long userId) {
        String sqlQuery = "SELECT * FROM ITEMS WHERE OWNER_ID = ?";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> ItemMapper.toItem(resultSet), userId);
    }

    @Override
    public Collection<Item> searchItems(String text) {
        String sqlQuery = "SELECT * " +
                "FROM ITEMS " +
                "WHERE (LOWER(ITEM_NAME) LIKE ? OR LOWER(ITEM_DESCRIPTION) LIKE ?) AND AVAILABLE = TRUE";
        return jdbcTemplate.query(
                sqlQuery, (resultSet, rowNum) -> ItemMapper.toItem(resultSet), "%" + text + "%", "%" + text + "%");
    }
}