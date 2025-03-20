package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.models.Movie;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ShowtimeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long movieId;
    private Instant futureTime;

    @BeforeEach
    public void setup() throws Exception {
        // We'll use one of the existing movies from data.sql (ID 1)
        // If we want to be safer, we could query for movies first and use an existing ID
        movieId = 1L;

        // Set a future time for showtime tests
        futureTime = Instant.now().plus(30, ChronoUnit.DAYS);
    }

    @Test
    public void addShowtime_ShouldReturnCreatedShowtime_WhenDataIsValid() throws Exception {
        Showtime showtime = new Showtime();
        showtime.setMovieId(movieId);
        showtime.setTheater("Theater Test 1");
        showtime.setPrice(12.99);
        showtime.setStartTime(futureTime);
        showtime.setEndTime(futureTime.plus(2, ChronoUnit.HOURS));

        mockMvc.perform(MockMvcRequestBuilders.post("/showtime")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.movieId").value(movieId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.theater").value("Theater Test 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(12.99));
    }

    @Test
    public void addShowtime_ShouldReturnBadRequest_WhenDataIsInvalid() throws Exception {
        Showtime showtime = new Showtime();
        showtime.setMovieId(movieId);
        showtime.setTheater(""); // Empty theater
        showtime.setPrice(12.99);
        showtime.setStartTime(futureTime);
        showtime.setEndTime(futureTime.plus(2, ChronoUnit.HOURS));

        mockMvc.perform(MockMvcRequestBuilders.post("/showtime")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void addShowtime_ShouldReturnNotFound_WhenMovieDoesNotExist() throws Exception {
        Showtime showtime = new Showtime();
        showtime.setMovieId(9999L); // Non-existent movie ID
        showtime.setTheater("Theater Test 1");
        showtime.setPrice(12.99);
        showtime.setStartTime(futureTime);
        showtime.setEndTime(futureTime.plus(2, ChronoUnit.HOURS));

        mockMvc.perform(MockMvcRequestBuilders.post("/showtime")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void addShowtime_ShouldReturnBadRequest_WhenStartTimeIsAfterEndTime() throws Exception {
        Showtime showtime = new Showtime();
        showtime.setMovieId(movieId);
        showtime.setTheater("Theater Test 1");
        showtime.setPrice(12.99);
        showtime.setStartTime(futureTime.plus(3, ChronoUnit.HOURS)); // Start after end
        showtime.setEndTime(futureTime);

        mockMvc.perform(MockMvcRequestBuilders.post("/showtime")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void addShowtime_ShouldReturnBadRequest_WhenStartTimeIsInPast() throws Exception {
        Showtime showtime = new Showtime();
        showtime.setMovieId(movieId);
        showtime.setTheater("Theater Test 1");
        showtime.setPrice(12.99);
        showtime.setStartTime(Instant.now().minus(1, ChronoUnit.DAYS)); // Past time
        showtime.setEndTime(Instant.now().plus(1, ChronoUnit.HOURS));

        mockMvc.perform(MockMvcRequestBuilders.post("/showtime")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // Helper method to create a test showtime and return its ID
    private Long createTestShowtime() throws Exception {
        Showtime showtime = new Showtime();
        showtime.setMovieId(movieId);
        showtime.setTheater("Theater Test Get");
        showtime.setPrice(12.99);
        showtime.setStartTime(futureTime);
        showtime.setEndTime(futureTime.plus(2, ChronoUnit.HOURS));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/showtime")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andReturn();

        // Extract the showtime ID from the response
        String responseJson = result.getResponse().getContentAsString();
        Showtime createdShowtime = objectMapper.readValue(responseJson, Showtime.class);
        return createdShowtime.getId();
    }

    @Test
    public void getShowtime_ShouldReturnShowtime_WhenShowtimeExists() throws Exception {
        // First, create a showtime
        Long showtimeId = createTestShowtime();

        // Get the showtime
        mockMvc.perform(MockMvcRequestBuilders.get("/showtime/{showtimeId}", showtimeId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(showtimeId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.movieId").value(movieId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.theater").value("Theater Test Get"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(12.99));
    }

    @Test
    public void getShowtime_ShouldReturnNotFound_WhenShowtimeDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/showtime/9999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void updateShowtime_ShouldUpdateShowtime_WhenDataIsValid() throws Exception {
        // First, create a showtime
        Showtime showtime = new Showtime();
        showtime.setMovieId(movieId);
        showtime.setTheater("Original Test Theater");
        showtime.setPrice(12.99);
        showtime.setStartTime(futureTime);
        showtime.setEndTime(futureTime.plus(2, ChronoUnit.HOURS));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/showtime")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andReturn();


        // Extract the showtime ID from the response
        String responseJson = result.getResponse().getContentAsString();
        System.out.println("Response JSON: " + responseJson); //todo delete


        Showtime createdShowtime = objectMapper.readValue(responseJson, Showtime.class);
        System.out.println("Parsed Showtime ID: " + createdShowtime.getId());// todo delete
        Long showtimeId = createdShowtime.getId();

        // Update the showtime
        Showtime updatedShowtime = new Showtime();
        updatedShowtime.setMovieId(movieId);
        updatedShowtime.setTheater("Updated Test Theater");
        updatedShowtime.setPrice(14.99);
        updatedShowtime.setStartTime(futureTime.plus(1, ChronoUnit.HOURS));
        updatedShowtime.setEndTime(futureTime.plus(3, ChronoUnit.HOURS));

        System.out.println("Using showtime ID: " + showtimeId);

        mockMvc.perform(MockMvcRequestBuilders.post("/showtime/update/{showtimeId}", showtimeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedShowtime)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify the showtime was updated
        mockMvc.perform(MockMvcRequestBuilders.get("/showtime/{showtimeId}", showtimeId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.theater").value("Updated Test Theater"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(14.99));
    }

    @Test
    public void updateShowtime_ShouldReturnNotFound_WhenShowtimeDoesNotExist() throws Exception {
        Showtime showtime = new Showtime();
        showtime.setMovieId(movieId);
        showtime.setTheater("Theater Test 1");
        showtime.setPrice(12.99);
        showtime.setStartTime(futureTime);
        showtime.setEndTime(futureTime.plus(2, ChronoUnit.HOURS));

        mockMvc.perform(MockMvcRequestBuilders.post("/showtime/update/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteShowtime_ShouldDeleteShowtime_WhenShowtimeExists() throws Exception {
        // First, create a showtime
        Showtime showtime = new Showtime();
        showtime.setMovieId(movieId);
        showtime.setTheater("Theater To Delete");
        showtime.setPrice(12.99);
        showtime.setStartTime(futureTime);
        showtime.setEndTime(futureTime.plus(2, ChronoUnit.HOURS));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/showtime")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        // Extract the showtime ID from the response
        String responseJson = result.getResponse().getContentAsString();
        Showtime createdShowtime = objectMapper.readValue(responseJson, Showtime.class);
        Long showtimeId = createdShowtime.getId();

        // Delete the showtime
        mockMvc.perform(MockMvcRequestBuilders.delete("/showtime/{showtimeId}", showtimeId))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify the showtime was deleted
        mockMvc.perform(MockMvcRequestBuilders.get("/showtime/{showtimeId}", showtimeId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteShowtime_ShouldReturnNotFound_WhenShowtimeDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/showtime/9999"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void addShowtime_ShouldReturnUnprocessableEntity_WhenShowtimesOverlap() throws Exception {
        // First, create a showtime
        Showtime showtime1 = new Showtime();
        showtime1.setMovieId(movieId);
        showtime1.setTheater("Theater Overlap Test");
        showtime1.setPrice(12.99);
        showtime1.setStartTime(futureTime);
        showtime1.setEndTime(futureTime.plus(2, ChronoUnit.HOURS));

        mockMvc.perform(MockMvcRequestBuilders.post("/showtime")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime1)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Try to create an overlapping showtime
        Showtime showtime2 = new Showtime();
        showtime2.setMovieId(movieId);
        showtime2.setTheater("Theater Overlap Test"); // Same theater
        showtime2.setPrice(14.99);
        showtime2.setStartTime(futureTime.plus(1, ChronoUnit.HOURS)); // Overlaps with first showtime
        showtime2.setEndTime(futureTime.plus(3, ChronoUnit.HOURS));

        mockMvc.perform(MockMvcRequestBuilders.post("/showtime")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(showtime2)))
                .andExpect(MockMvcResultMatchers.status().isUnprocessableEntity());
    }
}