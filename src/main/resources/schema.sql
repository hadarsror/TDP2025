-- Drop tables if they exist
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS showtimes;
DROP TABLE IF EXISTS movies;

-- Create movies table
CREATE TABLE IF NOT EXISTS movies (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL UNIQUE,
    genre VARCHAR(100) NOT NULL,
    duration INTEGER NOT NULL,
    rating DECIMAL(3,1) NOT NULL,
    release_year INTEGER NOT NULL
);

-- Create showtimes table
CREATE TABLE IF NOT EXISTS showtimes (
    id SERIAL PRIMARY KEY,
    movie_id BIGINT NOT NULL REFERENCES movies(id),
    theater VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    start_time TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time TIMESTAMP WITH TIME ZONE NOT NULL
);

-- Create bookings table
CREATE TABLE IF NOT EXISTS bookings (
    booking_id UUID PRIMARY KEY,
    showtime_id BIGINT NOT NULL REFERENCES showtimes(id),
    seat_number INTEGER NOT NULL,
    user_id UUID NOT NULL,
    UNIQUE (showtime_id, seat_number)
);

-- Create indexes for better performance
CREATE INDEX idx_movie_title ON movies(title);
CREATE INDEX idx_showtime_movie ON showtimes(movie_id);
CREATE INDEX idx_showtime_theater ON showtimes(theater);
CREATE INDEX idx_showtime_start ON showtimes(start_time);
CREATE INDEX idx_booking_showtime ON bookings(showtime_id);
CREATE INDEX idx_booking_user ON bookings(user_id);