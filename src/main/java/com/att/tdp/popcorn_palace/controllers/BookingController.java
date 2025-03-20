package com.att.tdp.popcorn_palace.controllers;

import com.att.tdp.popcorn_palace.exceptions.InvalidResourceException;
import com.att.tdp.popcorn_palace.models.Booking;
import com.att.tdp.popcorn_palace.models.BookingResponse;
import com.att.tdp.popcorn_palace.services.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> bookTicket(@RequestBody Booking booking) {
        validateBooking(booking);
        BookingResponse bookingResponse = bookingService.bookTicket(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingResponse);
    }

    private void validateBooking(Booking booking) {
        if (booking == null) {
            throw new InvalidResourceException("Booking cannot be null");
        }

        if (booking.getShowtimeId() == null) {
            throw new InvalidResourceException("showtimeId", "must not be null");
        }

        if (booking.getSeatNumber() == null) {
            throw new InvalidResourceException("seatNumber", "must not be null");
        }

        if (booking.getSeatNumber() <= 0) {
            throw new InvalidResourceException("seatNumber", "must be positive");
        }

        if (booking.getUserId() == null) {
            throw new InvalidResourceException("userId", "must not be null");
        }
    }
}