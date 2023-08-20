package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookingClosest {

    private Long id;

    private Long bookerId;

    public BookingClosest(Long id, Long bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }
}