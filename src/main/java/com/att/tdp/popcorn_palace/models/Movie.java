package com.att.tdp.popcorn_palace.models;
import jakarta.persistence.*;


@Entity
@Table(name="movies")
public class Movie {
    /***
     * This class represents a Movie object- which is a movie entity in the database.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
    private Long id;

    private String title;
    private String genre;
    private int duration;
    private double rating;
    private int releaseYear;


    // Constructors
    public Movie() {
    }

    public Movie(String title, String genre, int duration, double rating, int releaseYear) {
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.releaseYear = releaseYear;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getDuration() {  return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getReleaseYear() { return releaseYear; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }


    // toString -for testing mainly
    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", duration=" + duration +
                ", rating=" + rating +
                ", releaseYear=" + releaseYear +
                '}';
    }

    // equals -for testing mainly
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        Movie movie = (Movie) o;
        return getDuration() == movie.getDuration() &&
                Double.compare(movie.getRating(), getRating()) == 0 &&
                getReleaseYear() == movie.getReleaseYear() &&
                getId().equals(movie.getId()) &&
                getTitle().equals(movie.getTitle()) &&
                getGenre().equals(movie.getGenre());
    }
}
