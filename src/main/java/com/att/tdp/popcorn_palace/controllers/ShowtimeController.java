package com.att.tdp.popcorn_palace.controllers;

import com.att.tdp.popcorn_palace.exceptions.InvalidResourceException;
import com.att.tdp.popcorn_palace.models.Showtime;
import com.att.tdp.popcorn_palace.services.ShowtimeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/showtime")
public class ShowtimeController {
    private ShowtimeService showtimeService;

    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    @GetMapping("/{showtimeId}")
    public ResponseEntity<Showtime> getShowtime(@PathVariable Long showtimeId) {
        validateId(showtimeId);
        return ResponseEntity.ok(showtimeService.getShowtime(showtimeId));
    }

    @PostMapping
    public ResponseEntity<Showtime> addShowtime(@RequestBody Showtime showtime) {
        validateShowtime(showtime);
        Showtime addedShowtime = showtimeService.addShowtime(showtime);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedShowtime);
    }

    @PostMapping("/update/{showtimeId}")
    public ResponseEntity<Void> updateShowtime(@PathVariable Long showtimeId, @RequestBody Showtime updatedShowtime) {
        validateId(showtimeId);
        validateShowtime(updatedShowtime);
        showtimeService.updateShowtime(updatedShowtime, showtimeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{showtimeId}")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long showtimeId) {
        validateId(showtimeId);
        showtimeService.deleteShowtime(showtimeId);
        return ResponseEntity.ok().build();
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new InvalidResourceException("showtimeId", "must be a positive number");
        }
    }

    private void validateShowtime(Showtime showtime) {
        if (showtime == null) {
            throw new InvalidResourceException("Showtime cannot be null");
        }

        if (showtime.getMovieId() == null || showtime.getMovieId() <= 0) {
            throw new InvalidResourceException("movieId", "must be a positive number");
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
    }
}