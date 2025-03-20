# Popcorn Palace Movie Ticket Booking System - Setup Guide

## Overview
Popcorn Palace is a RESTful API for a movie ticket booking system built with Spring Boot and PostgreSQL. The system allows management of movies, showtimes, and ticket bookings with comprehensive error handling and validation.

## Prerequisites
- Java JDK 17+
- Docker Desktop
- Maven (or use the Maven wrapper included in the project)
- An IDE of your choice (IntelliJ IDEA, Eclipse, VS Code with Java extensions)

## Setup Instructions

### 1. Clone the Repository
```bash
git clone [repository-url]  # todo add repository url
cd popcorn-palace
```

### 2. Start the PostgreSQL Database
The project includes a `compose.yml` file that configures a PostgreSQL container:

```bash
# Start the database container
docker-compose up -d
```

This will start PostgreSQL with these connection details:
- Host: localhost
- Port: 5432
- Database: popcorn-palace

### 3. Build the Application
```bash
# Using Maven
mvn clean install

# Or using Maven wrapper
./mvnw clean install
```

### 4. Run the Application
```bash
# Using Maven
mvn spring-boot:run

# Or using Maven wrapper
./mvnw spring-boot:run
```

The application will start and listen on port 8080. The database schema will be automatically created by Hibernate based on the entity models.

## API Endpoints

### Movie Management
- **Get all movies**:
    - `GET /movies/all`
    - Response: 200 OK with list of movies

- **Add a movie**:
    - `POST /movies`
    - Request Body: `{ "title": "Sample Movie Title", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 }`
    - Response: 201 Created with created movie

- **Update a movie**:
    - `POST /movies/update/{movieTitle}`
    - Request Body: `{ "title": "New Title", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 }`
    - Response: 200 OK

- **Delete a movie**:
    - `DELETE /movies/{movieTitle}`
    - Response: 200 OK

### Showtime Management
- **Get showtime by ID**:
    - `GET /showtime/{showtimeId}`
    - Response: 200 OK with showtime details

- **Add a showtime**:
    - `POST /showtime`
    - Request Body: `{ "movieId": 1, "price": 20.2, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" }`
    - Response: 201 Created with created showtime

- **Update a showtime**:
    - `POST /showtime/update/{showtimeId}`
    - Request Body: `{ "movieId": 1, "price": 50.2, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" }`
    - Response: 200 OK

- **Delete a showtime**:
    - `DELETE /showtime/{showtimeId}`
    - Response: 200 OK

### Booking Management
- **Book a ticket**:
    - `POST /bookings`
    - Request Body: `{ "showtimeId": 1, "seatNumber": 15, "userId": "84438967-f68f-4fa0-b