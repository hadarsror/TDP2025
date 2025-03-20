package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.exceptions.InvalidResourceException;
import com.att.tdp.popcorn_palace.exceptions.ResourceAlreadyExistsException;
import com.att.tdp.popcorn_palace.exceptions.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.models.Booking;
import com.att.tdp.popcorn_palace.models.BookingResponse;
import com.att.tdp.popcorn_palace.repositories.BookingRepository;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import com.att.tdp.popcorn_palace.services.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ShowtimeRepository showtimeRepository;

    @InjectMocks
    private BookingService bookingService;

    private Booking testBooking;
    private UUID bookingId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        bookingId = UUID.randomUUID();
        userId = UUID.randomUUID();
        
        testBooking = new Booking();
        testBooking.setBookingId(bookingId);
        testBooking.setShowtimeId(1L);
        testBooking.setSeatNumber(15);
        testBooking.setUserId(userId);
    }

    @Test
    void bookTicket_WithValidData_ShouldCreateBooking() {
        // Arrange
        when(showtimeRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.existsByShowtimeIdAndSeatNumber(anyLong(), anyInt())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        // Act
        BookingResponse response = bookingService.bookTicket(testBooking);

        // Assert
        assertEquals(bookingId, response.getBookingId());
        verify(showtimeRepository).existsById(testBooking.getShowtimeId());
        verify(bookingRepository).existsByShowtimeIdAndSeatNumber(
                testBooking.getShowtimeId(), testBooking.getSeatNumber());
        verify(bookingRepository).save(testBooking);
    }

    @Test
    void bookTicket_WithNonExistentShowtime_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(showtimeRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> bookingService.bookTicket(testBooking)
        );
        
        assertTrue(exception.getMessage().contains("Showtime"));
        assertTrue(exception.getMessage().contains(testBooking.getShowtimeId().toString()));
        verify(showtimeRepository).existsById(testBooking.getShowtimeId());
        verify(bookingRepository, never()).existsByShowtimeIdAndSeatNumber(anyLong(), anyInt());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void bookTicket_WithAlreadyBookedSeat_ShouldThrowResourceAlreadyExistsException() {
        // Arrange
        when(showtimeRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.existsByShowtimeIdAndSeatNumber(anyLong(), anyInt())).thenReturn(true);

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(
            ResourceAlreadyExistsException.class,
            () -> bookingService.bookTicket(testBooking)
        );
        
        assertTrue(exception.getMessage().contains("Seat"));
        assertTrue(exception.getMessage().contains(testBooking.getSeatNumber().toString()));
        verify(showtimeRepository).existsById(testBooking.getShowtimeId());
        verify(bookingRepository).existsByShowtimeIdAndSeatNumber(
                testBooking.getShowtimeId(), testBooking.getSeatNumber());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void bookTicket_WithNullShowtimeId_ShouldThrowInvalidResourceException() {
        // Arrange
        testBooking.setShowtimeId(null);

        // Act & Assert
        InvalidResourceException exception = assertThrows(
            InvalidResourceException.class,
            () -> bookingService.bookTicket(testBooking)
        );
        
        assertTrue(exception.getMessage().contains("showtimeId"));
        verify(showtimeRepository, never()).existsById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void bookTicket_WithNullSeatNumber_ShouldThrowInvalidResourceException() {
        // Arrange
        testBooking.setSeatNumber(null);

        // Act & Assert
        InvalidResourceException exception = assertThrows(
            InvalidResourceException.class,
            () -> bookingService.bookTicket(testBooking)
        );
        
        assertTrue(exception.getMessage().contains("seatNumber"));
        verify(showtimeRepository, never()).existsById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void bookTicket_WithNegativeSeatNumber_ShouldThrowInvalidResourceException() {
        // Arrange
        testBooking.setSeatNumber(-5);

        // Act & Assert
        InvalidResourceException exception = assertThrows(
            InvalidResourceException.class,
            () -> bookingService.bookTicket(testBooking)
        );
        
        assertTrue(exception.getMessage().contains("seatNumber"));
        verify(showtimeRepository, never()).existsById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void bookTicket_WithNullUserId_ShouldThrowInvalidResourceException() {
        // Arrange
        testBooking.setUserId(null);

        // Act & Assert
        InvalidResourceException exception = assertThrows(
            InvalidResourceException.class,
            () -> bookingService.bookTicket(testBooking)
        );
        
        assertTrue(exception.getMessage().contains("userId"));
        verify(showtimeRepository, never()).existsById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}
