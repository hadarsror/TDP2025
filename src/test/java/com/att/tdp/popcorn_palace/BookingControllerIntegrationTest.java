package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.models.Booking;
import com.att.tdp.popcorn_palace.models.Showtime;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class BookingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long showtimeId;
    private UUID userId;

    @BeforeEach
    public void setup() throws Exception {
        // Create a random user ID
        userId = UUID.randomUUID();

        // We'll use existing showtime ID 1 from data.sql
        showtimeId = 1L;

        // Alternatively, we could create a new showtime, but for simplicity we'll use existing one
    }

    @Test
    public void bookTicket_ShouldReturnBookingId_WhenDataIsValid() throws Exception {
        Booking booking = new Booking();
        booking.setShowtimeId(showtimeId);
        booking.setSeatNumber(100); // Use high seat number to avoid conflicts with existing bookings
        booking.setUserId(userId);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.bookingId").exists());
    }

    @Test
    public void bookTicket_ShouldReturnBadRequest_WhenDataIsInvalid() throws Exception {
        // Missing seat number
        Booking booking = new Booking();
        booking.setShowtimeId(showtimeId);
        booking.setUserId(userId);
        // No seat number set

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void bookTicket_ShouldReturnBadRequest_WhenSeatNumberIsNegative() throws Exception {
        Booking booking = new Booking();
        booking.setShowtimeId(showtimeId);
        booking.setSeatNumber(-5); // Negative seat number
        booking.setUserId(userId);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void bookTicket_ShouldReturnNotFound_WhenShowtimeDoesNotExist() throws Exception {
        Booking booking = new Booking();
        booking.setShowtimeId(9999L); // Non-existent showtime ID
        booking.setSeatNumber(15);
        booking.setUserId(userId);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void bookTicket_ShouldReturnConflict_WhenSeatIsAlreadyBooked() throws Exception {
        // First booking
        Booking booking1 = new Booking();
        booking1.setShowtimeId(showtimeId);
        booking1.setSeatNumber(200); // Use high number to avoid conflicts
        booking1.setUserId(userId);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking1)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Attempt to book the same seat again
        Booking booking2 = new Booking();
        booking2.setShowtimeId(showtimeId);
        booking2.setSeatNumber(200); // Same seat number
        booking2.setUserId(UUID.randomUUID()); // Different user

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking2)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void bookTicket_ShouldAllowDifferentSeatsForSameShowtime() throws Exception {
        // First booking
        Booking booking1 = new Booking();
        booking1.setShowtimeId(showtimeId);
        booking1.setSeatNumber(201);
        booking1.setUserId(userId);

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking1)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Different seat, same showtime
        Booking booking2 = new Booking();
        booking2.setShowtimeId(showtimeId);
        booking2.setSeatNumber(202); // Different seat number
        booking2.setUserId(userId); // Same user even

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking2)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void bookTicket_ShouldReturnBadRequest_WhenUserIdIsMissing() throws Exception {
        Booking booking = new Booking();
        booking.setShowtimeId(showtimeId);
        booking.setSeatNumber(15);
        // No user ID set

        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}