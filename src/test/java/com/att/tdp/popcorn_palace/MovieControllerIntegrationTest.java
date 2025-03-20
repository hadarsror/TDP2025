package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.models.Movie;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
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

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MovieControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllMovies_ShouldReturnMovies() throws Exception {
        // Modified to expect existing movies from data.sql
        mockMvc.perform(MockMvcRequestBuilders.get("/movies/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists());
    }

    @Test
    public void addMovie_ShouldReturnCreatedMovie_WhenDataIsValid() throws Exception {
        Movie movie = new Movie();
        movie.setTitle("Unique Test Movie");  // Unique title to avoid conflicts
        movie.setGenre("Action");
        movie.setDuration(120);
        movie.setRating(8.5);
        movie.setReleaseYear(2023);

        mockMvc.perform(MockMvcRequestBuilders.post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Unique Test Movie"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.genre").value("Action"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.duration").value(120))
                .andExpect(MockMvcResultMatchers.jsonPath("$.rating").value(8.5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseYear").value(2023));
    }

    @Test
    public void addMovie_ShouldReturnBadRequest_WhenDataIsInvalid() throws Exception {
        Movie movie = new Movie();
        movie.setTitle(""); // Empty title
        movie.setGenre("Action");
        movie.setDuration(120);
        movie.setRating(8.5);
        movie.setReleaseYear(2023);

        mockMvc.perform(MockMvcRequestBuilders.post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void addMovie_ShouldReturnConflict_WhenMovieWithSameTitleExists() throws Exception {
        // First, add a movie
        Movie movie = new Movie();
        movie.setTitle("Duplicate Movie Test");  // Unique title
        movie.setGenre("Action");
        movie.setDuration(120);
        movie.setRating(8.5);
        movie.setReleaseYear(2023);

        mockMvc.perform(MockMvcRequestBuilders.post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Try to add the same movie again
        mockMvc.perform(MockMvcRequestBuilders.post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    public void getAllMovies_ShouldIncludeNewlyAddedMovie() throws Exception {
        // Get initial count of movies
        MvcResult initialResult = mockMvc.perform(MockMvcRequestBuilders.get("/movies/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        int initialCount = JsonPath.read(initialResult.getResponse().getContentAsString(), "$.length()");

        // Add a movie
        Movie movie = new Movie();
        movie.setTitle("New Test Movie for Count");
        movie.setGenre("Action");
        movie.setDuration(120);
        movie.setRating(8.5);
        movie.setReleaseYear(2023);

        mockMvc.perform(MockMvcRequestBuilders.post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Get all movies and verify count increased
        mockMvc.perform(MockMvcRequestBuilders.get("/movies/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(initialCount + 1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.title == 'New Test Movie for Count')]").exists());
    }

    @Test
    public void updateMovie_ShouldUpdateMovie_WhenDataIsValid() throws Exception {
        // First, add a movie
        Movie movie = new Movie();
        movie.setTitle("Original Update Test Title");
        movie.setGenre("Action");
        movie.setDuration(120);
        movie.setRating(8.5);
        movie.setReleaseYear(2023);

        mockMvc.perform(MockMvcRequestBuilders.post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Update the movie
        Movie updatedMovie = new Movie();
        updatedMovie.setTitle("Updated Test Title");
        updatedMovie.setGenre("Comedy");
        updatedMovie.setDuration(90);
        updatedMovie.setRating(7.5);
        updatedMovie.setReleaseYear(2024);

        mockMvc.perform(MockMvcRequestBuilders.post("/movies/update/Original Update Test Title")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMovie)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify the movie was updated using title search
        mockMvc.perform(MockMvcRequestBuilders.get("/movies/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.title == 'Updated Test Title')]").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.title == 'Updated Test Title')].genre").value("Comedy"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.title == 'Updated Test Title')].duration").value(90))
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.title == 'Updated Test Title')].rating").value(7.5))
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.title == 'Updated Test Title')].releaseYear").value(2024));
    }

    @Test
    public void updateMovie_ShouldReturnNotFound_WhenMovieDoesNotExist() throws Exception {
        Movie updatedMovie = new Movie();
        updatedMovie.setTitle("Updated Title");
        updatedMovie.setGenre("Comedy");
        updatedMovie.setDuration(90);
        updatedMovie.setRating(7.5);
        updatedMovie.setReleaseYear(2024);

        mockMvc.perform(MockMvcRequestBuilders.post("/movies/update/Non Existent Movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedMovie)))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteMovie_ShouldDeleteMovie_WhenMovieExists() throws Exception {
        // First, add a movie
        Movie movie = new Movie();
        movie.setTitle("Movie To Delete Test");
        movie.setGenre("Action");
        movie.setDuration(120);
        movie.setRating(8.5);
        movie.setReleaseYear(2023);

        mockMvc.perform(MockMvcRequestBuilders.post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // Get initial state and verify our movie exists
        MvcResult initialResult = mockMvc.perform(MockMvcRequestBuilders.get("/movies/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.title == 'Movie To Delete Test')]").exists())
                .andReturn();

        int initialCount = JsonPath.read(initialResult.getResponse().getContentAsString(), "$.length()");

        // Delete the movie
        mockMvc.perform(MockMvcRequestBuilders.delete("/movies/Movie To Delete Test"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify the movie was deleted
        mockMvc.perform(MockMvcRequestBuilders.get("/movies/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(initialCount - 1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.title == 'Movie To Delete Test')]").doesNotExist());
    }

    @Test
    public void deleteMovie_ShouldReturnNotFound_WhenMovieDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/movies/Non Existent Movie"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}