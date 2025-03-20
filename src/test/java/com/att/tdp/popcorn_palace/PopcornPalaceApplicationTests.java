package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.controllers.BookingController;
import com.att.tdp.popcorn_palace.controllers.MovieController;
import com.att.tdp.popcorn_palace.controllers.ShowtimeController;
import com.att.tdp.popcorn_palace.services.BookingService;
import com.att.tdp.popcorn_palace.services.MovieService;
import com.att.tdp.popcorn_palace.services.ShowtimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PopcornPalaceApplicationTests {

	@Autowired
	private MovieController movieController;

	@Autowired
	private ShowtimeController showtimeController;

	@Autowired
	private BookingController bookingController;

	@Autowired
	private MovieService movieService;

	@Autowired
	private ShowtimeService showtimeService;

	@Autowired
	private BookingService bookingService;

	@Test
	void contextLoads() {
		// Verify that all controllers are loaded
		assertNotNull(movieController);
		assertNotNull(showtimeController);
		assertNotNull(bookingController);

		// Verify that all services are loaded
		assertNotNull(movieService);
		assertNotNull(showtimeService);
		assertNotNull(bookingService);
	}
}
