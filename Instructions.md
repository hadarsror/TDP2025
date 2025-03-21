# Popcorn Palace - Setup Instructions

## Prerequisites
Make sure you have the following installed on your machine:
- Java 21 (or a compatible version)
- Maven
- Docker Desktop

## Getting Started

### 1. Database Setup
The application uses PostgreSQL database running in a Docker container. To set it up:

```bash
# Start the PostgreSQL container
docker-compose up -d
```

This will start a PostgreSQL instance with the following configuration:
- Database: popcorn-palace
- Username: popcorn-palace
- Password: popcorn-palace
- Port: 5432

### 2. Building the Application
```bash
# Navigate to the project root directory
cd popcorn-palace

# Build the project with Maven
mvn clean package
```

### 3. Running the Application
```bash
# Run the application
java -jar target/popcorn-palace-0.0.1-SNAPSHOT.jar
```

Alternatively, you can run it directly with Maven:
```bash
mvn spring-boot:run
```

The application will start on port 8080. You can access it at http://localhost:8080

### 4. Testing the Application
There are several types of tests included in the project:
- Unit tests for services
- Integration tests for controllers

To run the tests:
```bash
mvn test
```

## API Documentation

The application provides the following APIs:

### Movies APIs
- `GET /movies/all` - Get all movies
- `POST /movies` - Add a new movie
- `POST /movies/update/{title}` - Update a movie by title
- `DELETE /movies/{title}` - Delete a movie by title

### Showtimes APIs
- `GET /showtime/{showtimeId}` - Get showtime by ID
- `POST /showtime` - Add a new showtime
- `POST /showtime/update/{showtimeId}` - Update a showtime
- `DELETE /showtime/{showtimeId}` - Delete a showtime

### Bookings APIs
- `POST /bookings` - Book a movie ticket

## Schema Initialization
If you want to initialize the database with the schema and sample data:

1. The schema will be created automatically on application startup due to the `spring.jpa.hibernate.ddl-auto=update` setting.

2. If you want to load the sample data, you can use the provided SQL scripts:
```bash
# Connect to the PostgreSQL container
docker exec -it popcorn-palace_db_1 psql -U popcorn-palace -d popcorn-palace

# In the PostgreSQL prompt, load the data
\i /path/to/data.sql
```

## Troubleshooting

### Database Connection Issues
- Make sure Docker is running
- Check if the PostgreSQL container is running with `docker ps`
- Verify connection details in `application.yaml`

### Application Startup Problems
- Check the logs for error messages
- Ensure port 8080 is not being used by another application
- Verify Java version (21 or compatible)

