package com.att.tdp.popcorn_palace.models;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name="bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bookingId;
    private Long showtimeId;
    private Integer seatNumber;
    private UUID UserId;

    // Constructors
    public Booking() {
    }

    public Booking( Long showtimeId, Integer seatNumber, UUID UserId) {
        this.showtimeId = showtimeId;
        this.seatNumber = seatNumber;
        this.UserId = UserId;
    }

    // Getters and Setters
    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public Long getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(Long showtimeId) {
        this.showtimeId = showtimeId;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public UUID getUserId() {
        return UserId;
    }

    public void setUserId(UUID UserId) {
        this.UserId = UserId;
    }


    // toString -for testing mainly
    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", showtimeId=" + showtimeId +
                ", seatNumber=" + seatNumber +
                ", UserId=" + UserId +
                '}';
    }

    // equals - for testing mainly
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Booking)) return false;
        Booking booking = (Booking) o;
        return getBookingId() == booking.getBookingId() &&
                getShowtimeId().equals(booking.getShowtimeId()) &&
                getSeatNumber().equals(booking.getSeatNumber()) &&
                getUserId().equals(booking.getUserId());
    }


}
