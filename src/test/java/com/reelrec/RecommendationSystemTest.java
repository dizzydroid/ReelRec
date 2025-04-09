package com.reelrec;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class RecommendationSystemTest {
    
    private RecommendationSystem recommendationSystem;
    private TreeMap<Movie, List<String>> movieCategories;
    private TreeMap<String, List<Movie>> categoryMovies;
    private User testUser;
    private Movie movie1, movie2, movie3, movie4, movie5;
    
    @Before
    public void setUp() {
        // Create movies with title and ID
        movie1 = new Movie("Movie 1", "M1");
        movie2 = new Movie("Movie 2", "M2");
        movie3 = new Movie("Movie 3", "M3");
        movie4 = new Movie("Movie 4", "M4");
        movie5 = new Movie("Movie 5", "M5");
        
        // Set up movie categories
        movieCategories = new TreeMap<>();
        
        List<String> movie1Categories = new ArrayList<>();
        movie1Categories.add("Action");
        movie1Categories.add("Drama");
        movieCategories.put(movie1, movie1Categories);
        
        List<String> movie2Categories = new ArrayList<>();
        movie2Categories.add("Comedy");
        movie2Categories.add("Drama");
        movieCategories.put(movie2, movie2Categories);
        
        List<String> movie3Categories = new ArrayList<>();
        movie3Categories.add("Action");
        movie3Categories.add("Thriller");
        movieCategories.put(movie3, movie3Categories);
        
        List<String> movie4Categories = new ArrayList<>();
        movie4Categories.add("Drama");
        movie4Categories.add("Romance");
        movieCategories.put(movie4, movie4Categories);
        
        List<String> movie5Categories = new ArrayList<>();
        movie5Categories.add("Comedy");
        movie5Categories.add("Family");
        movieCategories.put(movie5, movie5Categories);
        
        // Set up category movies
        categoryMovies = new TreeMap<>();
        
        List<Movie> actionMovies = new ArrayList<>();
        actionMovies.add(movie1);
        actionMovies.add(movie3);
        categoryMovies.put("Action", actionMovies);
        
        List<Movie> dramaMovies = new ArrayList<>();
        dramaMovies.add(movie1);
        dramaMovies.add(movie2);
        dramaMovies.add(movie4);
        categoryMovies.put("Drama", dramaMovies);
        
        List<Movie> comedyMovies = new ArrayList<>();
        comedyMovies.add(movie2);
        comedyMovies.add(movie5);
        categoryMovies.put("Comedy", comedyMovies);
        
        List<Movie> thrillerMovies = new ArrayList<>();
        thrillerMovies.add(movie3);
        categoryMovies.put("Thriller", thrillerMovies);
        
        List<Movie> romanceMovies = new ArrayList<>();
        romanceMovies.add(movie4);
        categoryMovies.put("Romance", romanceMovies);
        
        List<Movie> familyMovies = new ArrayList<>();
        familyMovies.add(movie5);
        categoryMovies.put("Family", familyMovies);
        
        // Create test user with user ID
        testUser = new User("Test User", "USER001");
        testUser.addToWatchList(movie1);  // Add movie1 (Action, Drama) to watch list
        
        // Initialize recommendation system
        recommendationSystem = new RecommendationSystem(movieCategories, categoryMovies);
    }
    
    @Test
    public void testRecommendMoviesForUser() {
        // User has watched movie1 (Action, Drama)
        List<Movie> recommendations = recommendationSystem.recommendMoviesForUser(testUser);
        
        // Check that we got recommendations
        assertFalse("Should return recommendations", recommendations.isEmpty());
        
        // Check that recommendations don't include watched movies
        assertFalse("Should not recommend already watched movies", 
                 recommendations.contains(movie1));
        
        // Check that recommendations include movies from watched categories
        boolean containsActionOrDrama = false;
        for (Movie movie : recommendations) {
            List<String> categories = movieCategories.get(movie);
            if (categories.contains("Action") || categories.contains("Drama")) {
                containsActionOrDrama = true;
                break;
            }
        }
        assertTrue("Should recommend movies from categories user has watched", containsActionOrDrama);
    }
    
    @Test
    public void testRecommendSimilarMovies() {
        // movie1 is Action, Drama
        List<Movie> similarMovies = recommendationSystem.recommendSimilarMovies(movie1);
        
        // Check that we got recommendations
        assertFalse("Should return similar movies", similarMovies.isEmpty());
        
        // Check that the original movie is not in the recommendations
        assertFalse("Should not include the original movie", similarMovies.contains(movie1));
        
        // Check that the recommendations are from shared categories
        boolean hasSharedCategory = false;
        List<String> movie1Categories = movieCategories.get(movie1);
        
        for (Movie movie : similarMovies) {
            List<String> movieCategories = this.movieCategories.get(movie);
            for (String category : movieCategories) {
                if (movie1Categories.contains(category)) {
                    hasSharedCategory = true;
                    break;
                }
            }
        }
        
        assertTrue("Similar movies should share at least one category", hasSharedCategory);
    }
    
    @Test
    public void testGetMoviesByCategory() {
        // Test getting action movies
        List<Movie> actionMovies = recommendationSystem.getMoviesByCategory("Action");
        
        // Should get movie1 and movie3
        assertEquals("Should return 2 action movies", 2, actionMovies.size());
        assertTrue("Should contain movie1", actionMovies.contains(movie1));
        assertTrue("Should contain movie3", actionMovies.contains(movie3));
    }
    
    @Test
    public void testEdgeCases() {
        // Test with null user
        List<Movie> recommendations1 = recommendationSystem.recommendMoviesForUser(null);
        assertTrue("Should return empty list for null user", recommendations1.isEmpty());
        
        // Test with user having no watch history
        User emptyUser = new User("Empty User", "EMPTY001");
        List<Movie> recommendations2 = recommendationSystem.recommendMoviesForUser(emptyUser);
        assertTrue("Should return empty list for user with no watch history", recommendations2.isEmpty());
        
        // Test with non-existent movie
        Movie nonExistentMovie = new Movie("Non-existent", "NonExistent");
        List<Movie> similarMovies = recommendationSystem.recommendSimilarMovies(nonExistentMovie);
        assertTrue("Should return empty list for non-existent movie", similarMovies.isEmpty());
        
        // Test with non-existent category
        List<Movie> nonExistentCategoryMovies = recommendationSystem.getMoviesByCategory("NonExistentCategory");
        assertTrue("Should return empty list for non-existent category", nonExistentCategoryMovies.isEmpty());
    }
}