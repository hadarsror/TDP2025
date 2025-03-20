package com.att.tdp.popcorn_palace.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "showtimes")
public class Showtime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double price;
    private Long movieId;
    private String theater;
    private Instant startTime;
    private Instant endTime;

    // Constructors
    public Showtime() {
    }

    public Showtime(Double price, Long movieId, String theater, Instant startTime, Instant endTime) {
        this.price = price;
        this.movieId = movieId;
        this.theater = theater;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getTheater() {
        return theater;
    }

    public void setTheater(String theater) {
        this.theater = theater;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    // toString -for testing mainly
    @Override
    public String toString() {
        return "Showtime{" +
                "id=" + id +
                ", price=" + price +
                ", movieId=" + movieId +
                ", theater='" + theater + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    // equals  -for testing mainly
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Showtime showtime)) return false;
        return id.equals(showtime.id) &&
                price.equals(showtime.price) &&
                movieId.equals(showtime.movieId) &&
                theater.equals(showtime.theater) &&
                startTime.equals(showtime.startTime) &&
                endTime.equals(showtime.endTime);
    }




}
