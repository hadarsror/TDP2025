package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.exceptions.BusinessLogicException;
import com.att.tdp.popcorn_palace.exceptions.InvalidResourceException;
import com.att.tdp.popcorn_palace.exceptions.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.models.Showtime;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ShowtimeService {

    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;

    public ShowtimeService(ShowtimeRepository showtimeRepository, MovieRepository movieRepository) {
        this.showtimeRepository = showtimeRepository;
        this.movieRepository = movieRepository;
    }

    public Showtime addShowtime(Showtime showtime) {
        validateShowtime(showtime, null);
        return showtimeRepository.save(showtime);
    }

    private void validateShowtime(Showtime showtime, Long showtimeId) {
        // Basic validation
        if (showtime == null) {
            throw new InvalidResourceException("Showtime cannot be null");
        }

        if (showtime.getMovieId() == null) {
            throw new InvalidResourceException("movieId", "must not be null");
        }

        if (showtime.getTheater() == null || showtime.getTheater().trim().isEmpty()) {
            throw new InvalidResourceException("theater", "must not be empty");
        }

        if (showtime.getStartTime() == null) {
            throw new InvalidResourceException("startTime", "must not be null");
        }

        if (showtime.getEndTime() == null) {
            throw new InvalidResourceException("endTime", "must not be null");
        }

        if (showtime.getPrice() == null || showtime.getPrice() <= 0) {
            throw new InvalidResourceException("price", "must be positive");
        }

        // Check if movie exists
        if (!movieRepository.existsById(showtime.getMovieId())) {
            throw new ResourceNotFoundException("Movie", showtime.getMovieId());
        }

        // Validate time logic
        if (showtime.getStartTime().isAfter(showtime.getEndTime())) {
            throw new InvalidResourceException("startTime", "must be before end time");
        }

        if (showtime.getStartTime().isBefore(Instant.now())) {
            throw new InvalidResourceException("startTime", "must be in the future");
        }

        // Ensure showtime duration is reasonable (e.g., not 10 seconds or 10 hours)
        long durationSeconds = showtime.getEndTime().getEpochSecond() - showtime.getStartTime().getEpochSecond();
        if (durationSeconds < 30 * 60) { // Less than 30 minutes
            throw new InvalidResourceException("showtime duration", "must be at least 30 minutes");
        }

        if (durationSeconds > 5 * 60 * 60) { // More than 5 hours
            throw new InvalidResourceException("showtime duration", "must not exceed 5 hours");
        }

        // Check for overlapping showtimes (business rule)
        List<Showtime> overlappingShowtimes = showtimeRepository.findOverlappingShowtimes(
                showtime.getTheater(),
                showtime.getStartTime(),
                showtime.getEndTime(),
                showtimeId);

        if (!overlappingShowtimes.isEmpty()) {
            throw new BusinessLogicException(
                    String.format("Showtime overlaps with existing showtime in theater '%s'. Cannot schedule overlapping showtimes.",
                            showtime.getTheater())
            );
        }
    }

    public void updateShowtime(Showtime updatedShowtime, Long showtimeId) {
        Showtime showtime = showtimeRepository.findById(showtimeId)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime", showtimeId));

        validateShowtime(updatedShowtime, showtimeId);

        showtime.setTheater(updatedShowtime.getTheater());
        showtime.setStartTime(updatedShowtime.getStartTime());
        showtime.setEndTime(updatedShowtime.getEndTime());
        showtime.setMovieId(updatedShowtime.getMovieId());
        showtime.setPrice(updatedShowtime.getPrice());

        showtimeRepository.save(showtime);
    }

    public void deleteShowtime(Long id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime", id));

        // Add business rule: Don't allow deletion of showtimes that have already started
        if (showtime.getStartTime().isBefore(Instant.now())) {
            throw new BusinessLogicException(
                    "Cannot delete showtime that has already started or is in the past"
            );
        }

        showtimeRepository.delete(showtime);
    }

    public Showtime getShowtime(Long id) {
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showtime", id));
    }
}