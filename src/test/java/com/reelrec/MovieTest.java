package com.reelrec;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MovieTest {
    
    @Test
    public void testConstructorWithTwoParameters() {
        // Test the constructor with id and name parameters
        String id = "m001";
        String name = "The Shawshank Redemption";
        Movie movie = new Movie(id, name);
        
        assertEquals(id, movie.getID(), "ID should match the constructor parameter");
        assertEquals(name, movie.getName(), "Name should match the constructor parameter");
        assertEquals(0, movie.getGenre().length, "Genres array should be empty");
    }
    
    @Test
    public void testConstructorWithThreeParameters() {
        // Test the constructor with id, name, and genres parameters
        String id = "m002";
        String name = "The Godfather";
        String[] genres = {"Crime", "Drama"};
        Movie movie = new Movie(id, name, genres);
        
        assertEquals(id, movie.getID(), "ID should match the constructor parameter");
        assertEquals(name, movie.getName(), "Name should match the constructor parameter");
        assertArrayEquals(genres, movie.getGenre(), "Genres should match the constructor parameter");
    }
    
    @Test
    public void testGettersAndSetters() {
        Movie movie = new Movie("m003", "Pulp Fiction");
        
        // Test setters
        movie.setID("m004");
        movie.setName("Inception");
        String[] genres = {"Sci-Fi", "Action"};
        movie.setGenre(genres);
        
        // Test getters
        assertEquals("m004", movie.getID(), "getID should return the updated ID");
        assertEquals("Inception", movie.getName(), "getName should return the updated name");
        assertArrayEquals(genres, movie.getGenre(), "getGenre should return the updated genres");
    }
    
    @Test
    public void testCompareTo() {
        Movie movie1 = new Movie("m001", "Movie A");
        Movie movie2 = new Movie("m002", "Movie B");
        Movie movie3 = new Movie("m001", "Different Name");
        
        assertTrue(movie1.compareTo(movie2) < 0, "m001 should be less than m002");
        assertTrue(movie2.compareTo(movie1) > 0, "m002 should be greater than m001");
        assertEquals(0, movie1.compareTo(movie3), "Movies with same ID should be equal");
    }
    
    @Test
    public void testEquals() {
        Movie movie1 = new Movie("m001", "Movie A");
        Movie movie2 = new Movie("m001", "Different Name");
        Movie movie3 = new Movie("m002", "Movie A");
        
        assertEquals(movie1, movie2, "Movies with same ID should be equal");
        assertNotEquals(movie1, movie3, "Movies with different IDs should not be equal");
        assertNotEquals(movie1, null, "Movie should not equal null");
        assertNotEquals(movie1, "Not a movie", "Movie should not equal a different type");
    }
    
    @Test
    public void testHashCode() {
        Movie movie1 = new Movie("m001", "Movie A");
        Movie movie2 = new Movie("m001", "Different Name");
        
        assertEquals(movie1.hashCode(), movie2.hashCode(), "Movies with same ID should have same hash code");
    }

    // Additional tests below
    
    @Test
    public void testEmptyIDAndName() {
        Movie movie = new Movie("", "");
        assertEquals("", movie.getID(), "Empty ID should be allowed");
        assertEquals("", movie.getName(), "Empty name should be allowed");
    }
    
    @Test
    public void testNullIDAndName() {
        assertThrows(NullPointerException.class, () -> {
            new Movie(null, "Test Movie");
        }, "Constructor should throw NullPointerException when ID is null");
        
        assertThrows(NullPointerException.class, () -> {
            new Movie("m001", null);
        }, "Constructor should throw NullPointerException when name is null");
    }
    
    @Test
    public void testNullGenres() {
        assertThrows(NullPointerException.class, () -> {
            new Movie("m001", "Test Movie", null);
        }, "Constructor should throw NullPointerException when genres is null");
    }
    
    @Test
    public void testGenreArrayWithNullElements() {
        String[] genresWithNull = {"Action", null, "Comedy"};
        Movie movie = new Movie("m001", "Test Movie", genresWithNull);
        assertArrayEquals(genresWithNull, movie.getGenre(), "Genres array with null elements should be allowed");
    }
    
    @Test
    public void testSetNullValues() {
        Movie movie = new Movie("m001", "Test Movie");
        
        assertThrows(NullPointerException.class, () -> {
            movie.setID(null);
        }, "setID should throw NullPointerException when ID is null");
        
        assertThrows(NullPointerException.class, () -> {
            movie.setName(null);
        }, "setName should throw NullPointerException when name is null");
        
        assertThrows(NullPointerException.class, () -> {
            movie.setGenre(null);
        }, "setGenre should throw NullPointerException when genres is null");
    }
    
    @Test
    public void testGenreArrayDefensiveCopy() {
        // Test that modifying the original array doesn't affect the Movie object
        String[] originalGenres = {"Action", "Adventure"};
        Movie movie = new Movie("m001", "Test Movie", originalGenres);
        
        originalGenres[0] = "Comedy";
        assertNotEquals("Comedy", movie.getGenre()[0], "Movie should have a defensive copy of genres");
        
        // Test that modifying the returned array doesn't affect the Movie object
        String[] returnedGenres = movie.getGenre();
        returnedGenres[0] = "Horror";
        assertNotEquals("Horror", movie.getGenre()[0], "getGenre should return a defensive copy");
    }
    
    @Test
    public void testCompareToNull() {
        Movie movie = new Movie("m001", "Test Movie");
        
        assertThrows(NullPointerException.class, () -> {
            movie.compareTo(null);
        }, "compareTo should throw NullPointerException when parameter is null");
    }
    
    @Test
    public void testCompareToCaseSensitivity() {
        Movie movie1 = new Movie("M001", "Uppercase ID");
        Movie movie2 = new Movie("m001", "Lowercase ID");
        
        assertNotEquals(0, movie1.compareTo(movie2), "compareTo should be case sensitive");
    }
    
    @Test
    public void testCompareToComplexIDs() {
        Movie movie1 = new Movie("m9", "Movie 9");
        Movie movie2 = new Movie("m10", "Movie 10");
        
        assertTrue(movie1.compareTo(movie2) < 0, "Lexicographic comparison: 'm9' should be less than 'm10'");
    }
    
    @Test
    public void testEqualsContractProperties() {
        Movie movie1 = new Movie("m001", "Movie A");
        Movie movie2 = new Movie("m001", "Movie B");
        Movie movie3 = new Movie("m001", "Movie C");
        
        // Reflexivity
        assertTrue(movie1.equals(movie1), "Object should equal itself");
        
        // Symmetry
        assertTrue(movie1.equals(movie2) && movie2.equals(movie1), "equals should be symmetric");
        
        // Transitivity
        assertTrue(movie1.equals(movie2) && movie2.equals(movie3) && movie1.equals(movie3), 
                "equals should be transitive");
        
        // Consistency
        boolean firstResult = movie1.equals(movie2);
        boolean secondResult = movie1.equals(movie2);
        assertEquals(firstResult, secondResult, "Multiple invocations of equals should return the same result");
    }
    
    @Test
    public void testHashCodeConsistency() {
        Movie movie = new Movie("m001", "Original Name");
        int originalHashCode = movie.hashCode();
        
        // Hash code should be consistent across multiple calls
        assertEquals(originalHashCode, movie.hashCode(), "hashCode should return consistent results");
        
        // Hash code should only depend on ID
        movie.setName("Modified Name");
        assertEquals(originalHashCode, movie.hashCode(), "hashCode should depend only on ID, not on name");
        
        String[] genres = {"Action", "Comedy"};
        movie.setGenre(genres);
        assertEquals(originalHashCode, movie.hashCode(), "hashCode should depend only on ID, not on genres");
    }
    
    @Test
    public void testIDCaseSensitivity() {
        Movie movie1 = new Movie("m001", "Movie A");
        Movie movie2 = new Movie("M001", "Movie A");
        
        // Test case sensitivity in equals method
        assertNotEquals(movie1, movie2, "equals should be case sensitive for IDs");
        
        // Test case sensitivity in hashCode
        assertNotEquals(movie1.hashCode(), movie2.hashCode(), "hashCode should be case sensitive for IDs");
    }
}