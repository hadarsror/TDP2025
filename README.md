# Popcorn Palace Movie Booking System

A Spring Boot application for managing movie ticket bookings, showtimes, and movie information.

## Project Overview

This is a backend service built with Java Spring Boot that provides APIs for a movie theater booking system. The system allows managing movies, showtimes, and ticket bookings with the following features:

- **Movie Management**: Add, update, delete, and list movies
- **Showtime Management**: Create and manage movie showtimes
- **Booking System**: Book movie tickets with seat selection

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Docker
- JUnit & Spring Test for testing

## Project Structure

The application follows a standard layered architecture:

- **Controllers**: Handle HTTP requests and responses
- **Services**: Implement business logic
- **Repositories**: Provide data access
- **Models**: Define data entities
- **Exceptions**: Custom exception handling

## Features

### Movie Management
- Add movies with details (title, genre, duration, rating, release year)
- Update existing movies
- Delete movies (with validation to prevent deletion of movies with active showtimes)
- List all available movies

### Showtime Management
- Schedule movie showtimes with details (movie, theater, price, start/end times)
- Update showtime information
- Delete showtimes (with validation to prevent deletion of past showtimes)
- Retrieve showtime details
- Business validation to prevent overlapping showtimes in the same theater

### Ticket Booking
- Book seats for specific showtimes
- Validation to ensure seats cannot be double-booked
- User-associated bookings

## Running the Application

Detailed instructions for setting up and running the application can be found in the [Instructions.md](Instructions.md) file.

## API Reference

### Movies APIs

| API Description    | Endpoint                       | Method | Request Body                                                                           | Response Status |
|--------------------|--------------------------------|--------|----------------------------------------------------------------------------------------|-----------------|
| Get all movies     | `/movies/all`                  | GET    | -                                                                                      | 200 OK          |
| Add a movie        | `/movies`                      | POST   | `{ "title": "Movie Title", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 }` | 201 Created     |
| Update a movie     | `/movies/update/{movieTitle}`  | POST   | `{ "title": "Updated Title", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 }` | 200 OK          |
| Delete a movie     | `/movies/{movieTitle}`         | DELETE | -                                                                                      | 200 OK          |

### Showtimes APIs

| API Description    | Endpoint                         | Method | Request Body                                                                                                       | Response Status |
|--------------------|----------------------------------|--------|-------------------------------------------------------------------------------------------------------------------|-----------------|
| Get showtime by ID | `/showtime/{showtimeId}`         | GET    | -                                                                                                                   | 200 OK          |
| Add a showtime     | `/showtime`                      | POST   | `{ "movieId": 1, "price": 20.2, "theater": "Theater 1", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" }` | 201 Created     |
| Update a showtime  | `/showtime/update/{showtimeId}`  | POST   | `{ "movieId": 1, "price": 50.2, "theater": "Theater 1", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" }` | 200 OK          |
| Delete a showtime  | `/showtime/{showtimeId}`         | DELETE | -                                                                                                                   | 200 OK          |

### Bookings APIs

| API Description | Endpoint     | Method | Request Body                                                                  | Response Status |
|-----------------|--------------|--------|-------------------------------------------------------------------------------|-----------------|
| Book a ticket   | `/bookings`  | POST   | `{ "showtimeId": 1, "seatNumber": 15, "userId": "84438967-f68f-4fa0-b620-0f08217e76af" }` | 201 Created     |

## Testing

The application includes both unit tests and integration tests:

- Unit tests for service logic
- Integration tests for controller endpoints and repository interactions

Run the tests with Maven:
```
mvn test
```

## Error Handling

The application implements global exception handling with custom exceptions:

- `ResourceNotFoundException`: When a requested resource doesn't exist
- `ResourceAlreadyExistsException`: When trying to create a duplicate resource
- `InvalidResourceException`: For validation errors
- `BusinessLogicException`: For business rule violations
- `OperationNotAllowedException`: For unauthorized operations

Each exception returns appropriate HTTP status codes and informative error messages.
