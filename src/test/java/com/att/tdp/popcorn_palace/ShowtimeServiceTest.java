package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.exceptions.BusinessLogicException;
import com.att.tdp.popcorn_palace.exceptions.InvalidResourceException;
import com.att.tdp.popcorn_palace.exceptions.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.models.Showtime;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import com.att.tdp.popcorn_palace.services.ShowtimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShowtimeServiceTest {

    @Mock
    private ShowtimeRepository showtimeRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private ShowtimeService showtimeService;

    private Showtime testShowtime;
    private Instant futureTime;

    @BeforeEach
    void setUp() {
        futureTime = Instant.now().plus(30, ChronoUnit.DAYS); // 30 days in the future
        
        testShowtime = new Showtime();
        testShowtime.setId(1L);
        testShowtime.setMovieId(1L);
        testShowtime.setTheater("Theater 1");
        testShowtime.setPrice(12.99);
        testShowtime.setStartTime(futureTime);
        testShowtime.setEndTime(futureTime.plus(2, ChronoUnit.HOURS)); // 2 hours later
    }

    @Test
    void addShowtime_WithValidData_ShouldSaveAndReturnShowtime() {
        // Arrange
        when(movieRepository.existsById(anyLong())).thenReturn(true);
        when(showtimeRepository.findOverlappingShowtimes(
                anyString(), any(Instant.class), any(Instant.class), isNull()))
                .thenReturn(new ArrayList<>());
        when(showtimeRepository.save(any(Showtime.class))).thenReturn(testShowtime);

        // Act
        Showtime result = showtimeService.addShowtime(testShowtime);

        // Assert
        assertEquals(testShowtime, result);
        verify(movieRepository).existsById(testShowtime.getMovieId());
        verify(showtimeRepository).findOverlappingShowtimes(
                eq(testShowtime.getTheater()), 
                eq(testShowtime.getStartTime()), 
                eq(testShowtime.getEndTime()), 
                isNull());
        verify(showtimeRepository).save(testShowtime);
    }

    @Test
    void addShowtime_WithNonExistentMovie_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(movieRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> showtimeService.addShowtime(testShowtime)
        );
        
        assertTrue(exception.getMessage().contains("Movie"));
        assertTrue(exception.getMessage().contains(testShowtime.getMovieId().toString()));
        verify(movieRepository).existsById(testShowtime.getMovieId());
        verify(showtimeRepository, never()).findOverlappingShowtimes(
                anyString(), any(Instant.class), any(Instant.class), any());
        verify(showtimeRepository, never()).save(any(Showtime.class));
    }

    @Test
    void addShowtime_WithInvalidTimeRange_ShouldThrowInvalidResourceException() {
        // Arrange
        Showtime invalidShowtime = new Showtime();
        invalidShowtime.setId(1L);
        invalidShowtime.setMovieId(1L);
        invalidShowtime.setTheater("Theater 1");
        invalidShowtime.setPrice(12.99);
        invalidShowtime.setStartTime(futureTime.plus(3, ChronoUnit.HOURS)); // Start after end
        invalidShowtime.setEndTime(futureTime);
        
        when(movieRepository.existsById(anyLong())).thenReturn(true);

        // Act & Assert
        InvalidResourceException exception = assertThrows(
            InvalidResourceException.class,
            () -> showtimeService.addShowtime(invalidShowtime)
        );
        
        assertTrue(exception.getMessage().contains("startTime"));
        verify(movieRepository).existsById(invalidShowtime.getMovieId());
        verify(showtimeRepository, never()).save(any(Showtime.class));
    }

    @Test
    void addShowtime_WithPastStartTime_ShouldThrowInvalidResourceException() {
        // Arrange
        Showtime pastShowtime = new Showtime();
        pastShowtime.setId(1L);
        pastShowtime.setMovieId(1L);
        pastShowtime.setTheater("Theater 1");
        pastShowtime.setPrice(12.99);
        pastShowtime.setStartTime(Instant.now().minus(1, ChronoUnit.HOURS)); // 1 hour in the past
        pastShowtime.setEndTime(Instant.now().plus(1, ChronoUnit.HOURS));
        
        when(movieRepository.existsById(anyLong())).thenReturn(true);

        // Act & Assert
        InvalidResourceException exception = assertThrows(
            InvalidResourceException.class,
            () -> showtimeService.addShowtime(pastShowtime)
        );
        
        assertTrue(exception.getMessage().contains("startTime"));
        verify(movieRepository).existsById(pastShowtime.getMovieId());
        verify(showtimeRepository, never()).save(any(Showtime.class));
    }

    @Test
    void addShowtime_WithOverlappingTimes_ShouldThrowBusinessLogicException() {
        // Arrange
        List<Showtime> overlappingShowtimes = new ArrayList<>();
        overlappingShowtimes.add(new Showtime());
        
        when(movieRepository.existsById(anyLong())).thenReturn(true);
        when(showtimeRepository.findOverlappingShowtimes(
                anyString(), any(Instant.class), any(Instant.class), isNull()))
                .thenReturn(overlappingShowtimes);

        // Act & Assert
        BusinessLogicException exception = assertThrows(
            BusinessLogicException.class,
            () -> showtimeService.addShowtime(testShowtime)
        );
        
        assertTrue(exception.getMessage().contains("overlaps"));
        verify(movieRepository).existsById(testShowtime.getMovieId());
        verify(showtimeRepository).findOverlappingShowtimes(
                eq(testShowtime.getTheater()), 
                eq(testShowtime.getStartTime()), 
                eq(testShowtime.getEndTime()), 
                isNull());
        verify(showtimeRepository, never()).save(any(Showtime.class));
    }

    @Test
    void getShowtime_WithExistingId_ShouldReturnShowtime() {
        // Arrange
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(testShowtime));

        // Act
        Showtime result = showtimeService.getShowtime(1L);

        // Assert
        assertEquals(testShowtime, result);
        verify(showtimeRepository).findById(1L);
    }

    @Test
    void getShowtime_WithNonExistentId_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> showtimeService.getShowtime(1L)
        );
        
        assertTrue(exception.getMessage().contains("Showtime"));
        assertTrue(exception.getMessage().contains("1"));
        verify(showtimeRepository).findById(1L);
    }

    @Test
    void updateShowtime_WithValidData_ShouldUpdateShowtime() {
        // Arrange
        Showtime existingShowtime = new Showtime();
        existingShowtime.setId(1L);
        existingShowtime.setMovieId(1L);
        existingShowtime.setTheater("Theater 1");
        existingShowtime.setPrice(10.99);
        existingShowtime.setStartTime(futureTime);
        existingShowtime.setEndTime(futureTime.plus(2, ChronoUnit.HOURS));
        
        Showtime updatedShowtime = new Showtime();
        updatedShowtime.setMovieId(1L);
        updatedShowtime.setTheater("Theater 2"); // Changed theater
        updatedShowtime.setPrice(12.99); // Changed price
        updatedShowtime.setStartTime(futureTime.plus(1, ChronoUnit.HOURS)); // Changed time
        updatedShowtime.setEndTime(futureTime.plus(3, ChronoUnit.HOURS));
        
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(existingShowtime));
        when(movieRepository.existsById(anyLong())).thenReturn(true);
        when(showtimeRepository.findOverlappingShowtimes(
                anyString(), any(Instant.class), any(Instant.class), eq(1L)))
                .thenReturn(new ArrayList<>());

        // Act
        showtimeService.updateShowtime(updatedShowtime, 1L);

        // Assert
        assertEquals("Theater 2", existingShowtime.getTheater());
        assertEquals(12.99, existingShowtime.getPrice());
        assertEquals(updatedShowtime.getStartTime(), existingShowtime.getStartTime());
        assertEquals(updatedShowtime.getEndTime(), existingShowtime.getEndTime());
        
        verify(showtimeRepository).findById(1L);
        verify(movieRepository).existsById(updatedShowtime.getMovieId());
        verify(showtimeRepository).findOverlappingShowtimes(
                eq(updatedShowtime.getTheater()), 
                eq(updatedShowtime.getStartTime()), 
                eq(updatedShowtime.getEndTime()), 
                eq(1L));
        verify(showtimeRepository).save(existingShowtime);
    }

    @Test
    void updateShowtime_WithNonExistentId_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> showtimeService.updateShowtime(testShowtime, 1L)
        );
        
        assertTrue(exception.getMessage().contains("Showtime"));
        assertTrue(exception.getMessage().contains("1"));
        verify(showtimeRepository).findById(1L);
        verify(showtimeRepository, never()).save(any(Showtime.class));
    }

    @Test
    void deleteShowtime_WithExistingId_ShouldDeleteShowtime() {
        // Arrange
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(testShowtime));

        // Act
        showtimeService.deleteShowtime(1L);  // No result captured since method is void

        // Assert
        verify(showtimeRepository).findById(1L);
        verify(showtimeRepository).delete(testShowtime);
    }

    @Test
    void deleteShowtime_WithNonExistentId_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> showtimeService.deleteShowtime(1L)
        );
        
        assertTrue(exception.getMessage().contains("Showtime"));
        assertTrue(exception.getMessage().contains("1"));
        verify(showtimeRepository).findById(1L);
        verify(showtimeRepository, never()).delete(any(Showtime.class));
    }

    @Test
    void deleteShowtime_WithPastShowtime_ShouldThrowBusinessLogicException() {
        // Arrange
        Showtime pastShowtime = new Showtime();
        pastShowtime.setId(1L);
        pastShowtime.setMovieId(1L);
        pastShowtime.setTheater("Theater 1");
        pastShowtime.setPrice(12.99);
        pastShowtime.setStartTime(Instant.now().minus(1, ChronoUnit.HOURS)); // 1 hour in the past
        pastShowtime.setEndTime(Instant.now().plus(1, ChronoUnit.HOURS));
        
        when(showtimeRepository.findById(1L)).thenReturn(Optional.of(pastShowtime));

        // Act & Assert
        BusinessLogicException exception = assertThrows(
            BusinessLogicException.class,
            () -> showtimeService.deleteShowtime(1L)
        );
        
        assertTrue(exception.getMessage().contains("Cannot delete showtime"));
        verify(showtimeRepository).findById(1L);
        verify(showtimeRepository, never()).delete(any(Showtime.class));
    }
}
