package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.exceptions.InvalidResourceException;
import com.att.tdp.popcorn_palace.exceptions.ResourceAlreadyExistsException;
import com.att.tdp.popcorn_palace.exceptions.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.models.Movie;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import com.att.tdp.popcorn_palace.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private ShowtimeRepository showtimeRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie testMovie;

    @BeforeEach
    void setUp() {
        testMovie = new Movie();
        testMovie.setId(1L);
        testMovie.setTitle("Test Movie");
        testMovie.setGenre("Action");
        testMovie.setDuration(120);
        testMovie.setRating(8.5);
        testMovie.setReleaseYear(2023);
    }

    @Test
    void getAllMovies_ShouldReturnListOfMovies() {
        // Arrange
        List<Movie> expectedMovies = Arrays.asList(testMovie);
        when(movieRepository.findAll()).thenReturn(expectedMovies);

        // Act
        List<Movie> actualMovies = movieService.getAllMovies();

        // Assert
        assertEquals(expectedMovies, actualMovies);
        verify(movieRepository).findAll();
    }

    @Test
    void addMovie_WithValidMovie_ShouldSaveAndReturnMovie() {
        // Arrange
        when(movieRepository.existsByTitle(anyString())).thenReturn(false);
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        // Act
        Movie result = movieService.addMovie(testMovie);

        // Assert
        assertEquals(testMovie, result);
        verify(movieRepository).existsByTitle(testMovie.getTitle());
        verify(movieRepository).save(testMovie);
    }

    @Test
    void addMovie_WithExistingTitle_ShouldThrowResourceAlreadyExistsException() {
        // Arrange
        when(movieRepository.existsByTitle(anyString())).thenReturn(true);

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(
            ResourceAlreadyExistsException.class,
            () -> movieService.addMovie(testMovie)
        );
        
        assertTrue(exception.getMessage().contains(testMovie.getTitle()));
        verify(movieRepository).existsByTitle(testMovie.getTitle());
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void addMovie_WithInvalidData_ShouldThrowInvalidResourceException() {
        // Arrange
        Movie invalidMovie = new Movie();
        invalidMovie.setTitle(""); // Empty title
        invalidMovie.setGenre("Action");
        invalidMovie.setDuration(120);
        invalidMovie.setRating(8.5);
        invalidMovie.setReleaseYear(2023);

        // Act & Assert
        InvalidResourceException exception = assertThrows(
            InvalidResourceException.class,
            () -> movieService.addMovie(invalidMovie)
        );
        
        assertTrue(exception.getMessage().contains("title"));
        verify(movieRepository, never()).existsByTitle(anyString());
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void updateMovie_WithValidData_ShouldUpdateExistingMovie() {
        // Arrange
        Movie updatedMovie = new Movie();
        updatedMovie.setTitle("Updated Title");
        updatedMovie.setGenre("Comedy");
        updatedMovie.setDuration(90);
        updatedMovie.setRating(7.5);
        updatedMovie.setReleaseYear(2024);

        when(movieRepository.findByTitle("Test Movie")).thenReturn(Optional.of(testMovie));
        
        // Act
        movieService.updateMovie("Test Movie", updatedMovie);

        // Assert
        assertEquals("Updated Title", testMovie.getTitle());
        assertEquals("Comedy", testMovie.getGenre());
        assertEquals(90, testMovie.getDuration());
        assertEquals(7.5, testMovie.getRating());
        assertEquals(2024, testMovie.getReleaseYear());
        
        verify(movieRepository).findByTitle("Test Movie");
        verify(movieRepository).save(testMovie);
    }

    @Test
    void updateMovie_WithNonExistentTitle_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(movieRepository.findByTitle(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> movieService.updateMovie("Non-existent Movie", testMovie)
        );
        
        assertTrue(exception.getMessage().contains("Non-existent Movie"));
        verify(movieRepository).findByTitle("Non-existent Movie");
        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void deleteMovie_WithExistingTitle_ShouldDeleteMovie() {
        // Arrange
        when(movieRepository.findByTitle("Test Movie")).thenReturn(Optional.of(testMovie));
        when(showtimeRepository.existsByMovieId(1L)).thenReturn(false);

        // Act
        boolean result = movieService.deleteMovie("Test Movie");

        // Assert
        assertTrue(result);
        verify(movieRepository).findByTitle("Test Movie");
        verify(showtimeRepository).existsByMovieId(1L);
        verify(movieRepository).delete(testMovie);
    }

    @Test
    void deleteMovie_WithNonExistentTitle_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(movieRepository.findByTitle(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> movieService.deleteMovie("Non-existent Movie")
        );
        
        assertTrue(exception.getMessage().contains("Non-existent Movie"));
        verify(movieRepository).findByTitle("Non-existent Movie");
        verify(movieRepository, never()).delete(any(Movie.class));
    }

    @Test
    void deleteMovie_WithExistingShowtimes_ShouldThrowBusinessLogicException() {
        // Arrange
        when(movieRepository.findByTitle("Test Movie")).thenReturn(Optional.of(testMovie));
        when(showtimeRepository.existsByMovieId(1L)).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(
            Exception.class,
            () -> movieService.deleteMovie("Test Movie")
        );
        
        assertTrue(exception.getMessage().contains("Cannot delete movie with existing showtimes"));
        verify(movieRepository).findByTitle("Test Movie");
        verify(showtimeRepository).existsByMovieId(1L);
        verify(movieRepository, never()).delete(any(Movie.class));
    }
}
