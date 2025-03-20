package com.att.tdp.popcorn_palace.repositories;

import com.att.tdp.popcorn_palace.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long>{
    boolean existsByShowtimeIdAndSeatNumber(Long showtimeId, Integer seatNumber);
}
