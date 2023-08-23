package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(long ownerId);

    @Modifying
    @Query("UPDATE Item i SET " +
            "i.name = CASE WHEN :#{#item.name} IS NOT NULL THEN :#{#item.name} ELSE i.name END, " +
            "i.description = CASE WHEN :#{#item.description} IS NOT NULL THEN :#{#item.description} ELSE i.description END, " +
            "i.isAvailable = CASE WHEN :#{#item.isAvailable} IS NOT NULL THEN :#{#item.isAvailable} ELSE i.isAvailable END " +
            "WHERE i.id = :itemId AND i.owner.id = :ownerId")
    void updateItemFields(Item item, Long ownerId, Long itemId);

    @Query("SELECT it FROM Item it " +
            "WHERE (LOWER(it.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(it.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND it.isAvailable = true ")
    List<Item> searchItemByNameOrDescription(String text);
}