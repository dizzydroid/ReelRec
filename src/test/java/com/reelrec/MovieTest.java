package com.reelrec;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

public class MovieTest {
    
    @Test
    public void testMakeHashMapWithValidData() {
        Movie movie = new Movie();
        // Use default paths - the files should be in the resources folder
        
        HashMap<String, HashMap<String, Movie>> result = movie.makeHashMap();
        
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should have 2 users");
        
        // Check first user's movies
        HashMap<String, Movie> user1Movies = result.get("12345678X");
        assertNotNull(user1Movies, "User 1's movies should not be null");
        assertEquals(2, user1Movies.size(), "User 1 should have 2 movies");
        assertTrue(user1Movies.containsKey("TSR001"), "User 1 should have movie TSR001");
        assertTrue(user1Movies.containsKey("TDK003"), "User 1 should have movie TDK003");
        
        // Check The Shawshank Redemption
        Movie movie1 = user1Movies.get("TSR001");
        assertEquals("TSR001", movie1.getID(), "Movie 1 ID should be TSR001");
        assertEquals("The Shawshank Redemption", movie1.getName(), "Movie 1 name should be The Shawshank Redemption");
        assertArrayEquals(new String[]{"Drama"}, movie1.getGenre(), "Movie 1 genres should match");
        
        // Check The Dark Knight 
        Movie movie2 = user1Movies.get("TDK003");
        assertEquals("TDK003", movie2.getID(), "Movie 2 ID should be TDK003");
        assertEquals("The Dark Knight", movie2.getName(), "Movie 2 name should be The Dark Knight");
        assertArrayEquals(new String[]{"Action", "Crime", "Drama"}, movie2.getGenre(), "Movie 2 genres should match");
        
        // Check second user's movies
        HashMap<String, Movie> user2Movies = result.get("87654321W");
        assertNotNull(user2Movies, "User 2's movies should not be null");
        assertEquals(1, user2Movies.size(), "User 2 should have 1 movie");
        assertTrue(user2Movies.containsKey("TG002"), "User 2 should have movie TG002");
        
        // Check The Godfather
        Movie movie3 = user2Movies.get("TG002");
        assertEquals("TG002", movie3.getID(), "Movie 3 ID should be TG002");
        assertEquals("The Godfather", movie3.getName(), "Movie 3 name should be The Godfather");
        assertArrayEquals(new String[]{"Crime", "Drama"}, movie3.getGenre(), "Movie 3 genres should match");
    }
    
    @Test
    public void testMakeHashMapWithEmptyFiles() {
        Movie movie = new Movie();
        movie.setResourcePaths("Testing/empty_resources/empty_movies.txt", "Testing/empty_resources/empty_users.txt");
        
        HashMap<String, HashMap<String, Movie>> result = movie.makeHashMap();
        
        assertNotNull(result, "Result should not be null even with empty files");
        assertEquals(0, result.size(), "Result should be empty");
    }
    
    @Test
    public void testMakeHashMapWithMissingMovies() {
        Movie movie = new Movie();
        movie.setResourcePaths("Testing/limited/limitedMoviesFile.txt", "Testing/limited/usersWithMissingMoviesFile.txt");
        
        HashMap<String, HashMap<String, Movie>> result = movie.makeHashMap();
        
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Should have 1 user");
        
        HashMap<String, Movie> userMovies = result.get("12345678X");
        assertEquals(1, userMovies.size(), "User should have only 1 valid movie");
        assertTrue(userMovies.containsKey("TSR001"), "User should have movie TSR001");
    }
}