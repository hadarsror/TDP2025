INSERT INTO movies (title, genre, duration, rating, release_year) VALUES
('The Shawshank Redemption', 'Drama', 142, 9.3, 1994),
('The Godfather', 'Crime', 175, 9.2, 1972),
('The Dark Knight', 'Action', 152, 9.0, 2008),
('The Lord of the Rings: The Return of the King', 'Adventure', 201, 8.9, 2003),
('Pulp Fiction', 'Crime', 154, 8.9, 1994);

INSERT INTO showtimes (movie_id, theater, price, start_time, end_time) VALUES
(1, 'Theater 1', 12.99, '2025-06-15 14:00:00+00', '2025-06-15 16:30:00+00'),
(1, 'Theater 2', 14.99, '2025-06-15 18:00:00+00', '2025-06-15 20:30:00+00'),
(2, 'Theater 1', 12.99, '2025-06-15 20:00:00+00', '2025-06-15 23:00:00+00'),
(3, 'Theater 3', 15.99, '2025-06-16 15:00:00+00', '2025-06-16 17:30:00+00'),
(3, 'Theater 3', 15.99, '2025-06-16 18:00:00+00', '2025-06-16 20:30:00+00'),
(4, 'Theater 2', 14.99, '2025-06-16 17:00:00+00', '2025-06-16 20:30:00+00'),
(5, 'Theater 1', 12.99, '2025-06-17 19:00:00+00', '2025-06-17 21:30:00+00');

INSERT INTO bookings (booking_id, showtime_id, seat_number, user_id) VALUES
('f47ac10b-58cc-4372-a567-0e02b2c3d479', 1, 5, '550e8400-e29b-41d4-a716-446655440000'),
('f47ac10b-58cc-4372-a567-0e02b2c3d480', 1, 6, '550e8400-e29b-41d4-a716-446655440000'),
('f47ac10b-58cc-4372-a567-0e02b2c3d481', 2, 10, 'c6235813-3ba9-4e5d-b3f5-4d9f957e3c5e'),
('f47ac10b-58cc-4372-a567-0e02b2c3d482', 3, 15, 'f7235813-3ba9-4e5d-b3f5-4d9f957e3c5e'),
('f47ac10b-58cc-4372-a567-0e02b2c3d483', 4, 7, '550e8400-e29b-41d4-a716-446655440000');