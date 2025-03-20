package com.att.tdp.popcorn_palace.repositories;


import com.att.tdp.popcorn_palace.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByTitle(String title);
    boolean existsByTitle(String title);

}
