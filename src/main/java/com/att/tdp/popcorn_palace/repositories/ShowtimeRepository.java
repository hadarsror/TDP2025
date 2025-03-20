package com.att.tdp.popcorn_palace.repositories;

import com.att.tdp.popcorn_palace.models.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    @Query("SELECT s FROM Showtime s WHERE s.theater = :theater AND " +
            "((s.startTime <= :endTime AND s.endTime >= :startTime) OR " +
            "(s.startTime >= :startTime AND s.startTime <= :endTime)) AND " +
            "(:id IS NULL OR s.id <> :id)")
    List<Showtime> findOverlappingShowtimes(
            @Param("theater") String theater,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime,
            @Param("id") Long id
    );

    // Add method to check if a movie has any showtimes
    boolean existsByMovieId(Long movieId);

    // Add method to find future showtimes for a movie
    @Query("SELECT s FROM Showtime s WHERE s.movieId = :movieId AND s.startTime > :now")
    List<Showtime> findFutureShowtimesByMovieId(Long movieId, Instant now);
}