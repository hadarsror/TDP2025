package com.att.tdp.popcorn_palace.services;

import com.att.tdp.popcorn_palace.exceptions.BusinessLogicException;
import com.att.tdp.popcorn_palace.exceptions.InvalidResourceException;
import com.att.tdp.popcorn_palace.exceptions.ResourceAlreadyExistsException;
import com.att.tdp.popcorn_palace.exceptions.ResourceNotFoundException;
import com.att.tdp.popcorn_palace.models.Movie;
import com.att.tdp.popcorn_palace.repositories.MovieRepository;
import com.att.tdp.popcorn_palace.repositories.ShowtimeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;

    public MovieService(MovieRepository movieRepository, ShowtimeRepository showtimeRepository) {
        this.movieRepository = movieRepository;
        this.showtimeRepository = showtimeRepository;
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Movie addMovie(Movie movie) {
        // Validate movie data
        validateMovie(movie);

        // Check if movie with same title already exists
        if (movieRepository.existsByTitle(movie.getTitle())) {
            throw new ResourceAlreadyExistsException("Movie", movie.getTitle());
        }

        return movieRepository.save(movie);
    }

    public void updateMovie(String title, Movie updatedMovie) {
        // Validate movie data
        validateMovie(updatedMovie);

        // Get existing movie
        Movie movie = movieRepository.findByTitle(title)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", title));

        // Check if new title conflicts with existing movie (excluding this one)
        if (!updatedMovie.getTitle().equals(title) && movieRepository.existsByTitle(updatedMovie.getTitle())) {
            throw new ResourceAlreadyExistsException("Movie", updatedMovie.getTitle());
        }

        // Update movie properties
        movie.setTitle(updatedMovie.getTitle());
        movie.setGenre(updatedMovie.getGenre());
        movie.setRating(updatedMovie.getRating());
        movie.setDuration(updatedMovie.getDuration());
        movie.setReleaseYear(updatedMovie.getReleaseYear());

        movieRepository.save(movie);
    }

    public boolean deleteMovie(String title) {
        // Get existing movie
        Movie movie = movieRepository.findByTitle(title)
                .orElseThrow(() -> new ResourceNotFoundException("Movie", title));

        // Check if movie has any associated showtimes
        Long movieId = movie.getId();

        if (showtimeRepository.existsByMovieId(movieId)) {
            throw new BusinessLogicException("Cannot delete movie with existing showtimes. Remove all showtimes for this movie first.");
        }

        movieRepository.delete(movie);
        return true;
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
}