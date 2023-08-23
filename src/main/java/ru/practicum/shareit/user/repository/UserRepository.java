package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Modifying
    @Query("UPDATE User u SET " +
            "u.name = CASE WHEN :#{#user.name} IS NOT NULL THEN :#{#user.name} ELSE u.name END, " +
            "u.email = CASE WHEN :#{#user.email} IS NOT NULL THEN :#{#user.email} ELSE u.email END " +
            "WHERE u.id = :userId")
    void updateUserFields(User user, Long userId);
}