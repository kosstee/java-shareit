package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Item addNewItem(Long userId, Item item) throws DataAccessException {
        String sqlQueryToCheckIfUserExists = "SELECT COUNT(*) FROM USERS WHERE USER_ID = ?";
        Integer result = jdbcTemplate.queryForObject(
                sqlQueryToCheckIfUserExists, Integer.class, userId);
        if (result == null || result == 0) {
            log.error("User with id = {} not found", userId);
            throw new UserNotFoundException("User with id = " + userId + " not found");
        }

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("ITEMS")
                .usingGeneratedKeyColumns("ITEM_ID");

        item.setId(simpleJdbcInsert.executeAndReturnKey(
                Map.of("ITEM_NAME", item.getName(),
                        "ITEM_DESCRIPTION", item.getDescription(),
                        "AVAILABLE", item.getAvailable(),
                        "OWNER_ID", userId)).longValue());
        return item;
    }

    @Override
    public Item getById(Long itemId) {
        String sqlQuery = "SELECT * FROM ITEMS WHERE ITEM_ID = ?";
        return jdbcTemplate.queryForObject(sqlQuery, (resultSet, rowNum) -> rowMapper(resultSet), itemId);
    }

    @Override
    public Item updateItem(Item item, Long itemId) {
        Item itemToUpdate = getById(itemId);
        String sqlQuery = "UPDATE ITEMS SET ITEM_NAME = ?, ITEM_DESCRIPTION = ?, AVAILABLE = ? WHERE ITEM_ID = ?";
        jdbcTemplate.update(
                sqlQuery,
                item.getName() == null ? itemToUpdate.getName() : item.getName(),
                item.getDescription() == null ? itemToUpdate.getDescription() : item.getDescription(),
                item.getAvailable() == null ? itemToUpdate.getAvailable() : item.getAvailable(),
                itemId);
        return getById(itemId);
    }

    @Override
    public Collection<Item> getAllUserItems(Long userId) {
        String sqlQuery = "SELECT * FROM ITEMS WHERE OWNER_ID = ?";
        return jdbcTemplate.query(sqlQuery, (resultSet, rowNum) -> rowMapper(resultSet), userId);
    }

    @Override
    public Collection<Item> searchItems(String text) {
        String sqlQuery = "SELECT * " +
                "FROM ITEMS " +
                "WHERE (LOWER(ITEM_NAME) LIKE ? OR LOWER(ITEM_DESCRIPTION) LIKE ?) AND AVAILABLE = TRUE";
        return jdbcTemplate.query(
                sqlQuery, (resultSet, rowNum) -> rowMapper(resultSet), "%" + text + "%", "%" + text + "%");
    }

    private Item rowMapper(ResultSet resultSet) throws SQLException {
        return Item.builder()
                .id(resultSet.getLong("ITEM_ID"))
                .name(resultSet.getString("ITEM_NAME"))
                .description(resultSet.getString("ITEM_DESCRIPTION"))
                .available(resultSet.getBoolean("AVAILABLE"))
                .ownerId(resultSet.getLong("OWNER_ID"))
                .build();
    }
}