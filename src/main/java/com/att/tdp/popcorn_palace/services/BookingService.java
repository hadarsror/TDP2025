package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.exceptions.InvalidResourceException;
import com.att.tdp.popcorn_palace.exceptions.ResourceAlreadyExistsException;
import com.att.tdp.popcorn_palace.exceptions.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.models.Booking;
import com.att.tdp.popcorn_palace.models.BookingResponse;
import com.att.tdp.popcorn_palace.repositories.BookingRepository;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;

    public BookingService(BookingRepository bookingRepository, ShowtimeRepository showtimeRepository) {
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
    }

    public BookingResponse bookTicket(Booking booking) {
        // Validate input
        validateBooking(booking);

        // validate showtime exists
        if (!showtimeRepository.existsById(booking.getShowtimeId())) {
            throw new ResourceNotFoundException("Showtime", booking.getShowtimeId());
        }

        // validate seat is available
        if (bookingRepository.existsByShowtimeIdAndSeatNumber(booking.getShowtimeId(), booking.getSeatNumber())) {
            throw new ResourceAlreadyExistsException(
                    String.format("Seat %d for showtime %d is already booked",
                            booking.getSeatNumber(), booking.getShowtimeId())
            );
        }

        Booking newBooking = bookingRepository.save(booking);
        return new BookingResponse(newBooking.getBookingId());
    }

    /**
     * Validates the booking data.
     *
     * @param booking the booking to validate
     * @throws InvalidResourceException if booking data is invalid
     */
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
            throw new InvalidResourceException("seatNumber", "must be a positive number");
        }

        if (booking.getUserId() == null) {
            throw new InvalidResourceException("userId", "must not be null");
        }
    }
}