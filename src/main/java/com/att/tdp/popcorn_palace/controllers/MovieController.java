package com.att.tdp.popcorn_palace.controllers;

import com.att.tdp.popcorn_palace.exceptions.InvalidResourceException;
import com.att.tdp.popcorn_palace.models.Movie;
import com.att.tdp.popcorn_palace.services.MovieService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Movie>> getAllMovies() {
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @PostMapping
    public ResponseEntity<Movie> addMovie(@RequestBody Movie movie) {
        validateMovie(movie);
        Movie addedMovie = movieService.addMovie(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedMovie);
    }

    @PostMapping("/update/{title}")
    public ResponseEntity<Void> updateMovie(@PathVariable String title, @RequestBody Movie updatedMovie) {
        validateTitle(title);
        validateMovie(updatedMovie);
        movieService.updateMovie(title, updatedMovie);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{title}")
    public ResponseEntity<Void> deleteMovie(@PathVariable String title) {
        validateTitle(title);
        movieService.deleteMovie(title);
        return ResponseEntity.ok().build();
    }

    private void validateMovie(Movie movie) {
        if (movie == null) {
            throw new InvalidResourceException("Movie cannot be null");
        }

        if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
            throw new InvalidResourceException("title", "must not be empty");
        }

        if (movie.getGenre() == null || movie.getGenre().trim().isEmpty()) {
            throw new InvalidResourceException("genre", "must not be empty");
        }

        if (movie.getDuration() <= 0) {
            throw new InvalidResourceException("duration", "must be positive");
        }

        if (movie.getRating() < 0 || movie.getRating() > 10) {
            throw new InvalidResourceException("rating", "must be between 0 and 10");
        }

        if (movie.getReleaseYear() < 1900 || movie.getReleaseYear() > 2100) {
            throw new InvalidResourceException("releaseYear", "must be between 1900 and 2100");
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidResourceException("title", "must not be empty");
        }
    }
}