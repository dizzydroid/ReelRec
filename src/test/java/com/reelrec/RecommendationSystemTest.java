package com.reelrec;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import static org.junit.jupiter.api.Assertions.*;

public class RecommendationSystemTest {

    private RecommendationSystem recommendationSystem;
    private TreeMap<Movie, List<String>> movieCategories;
    private TreeMap<String, List<Movie>> categoryMovies;
    private User testUser;
    private Movie movie1, movie2, movie3, movie4, movie5;

    @BeforeEach
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
        testUser.addToWatchList(movie1); // Add movie1 (Action, Drama) to watch list

        // Initialize recommendation system
        recommendationSystem = new RecommendationSystem(movieCategories, categoryMovies);
    }

    @Test
    // recommending movies based on a user's watching history
    public void testRecommendMoviesForUser() {
        // Test user with movie1 in watch history (Action, Drama)
        List<Movie> recommendations = recommendationSystem.recommendMoviesForUser(testUser);

        // Check that we got recommendations (movie2, movie3, movie4) without the
        // watched one (movie1)
        assertAll(
                () -> assertFalse(recommendations.isEmpty(), "Should return recommendations"),
                () -> assertFalse(recommendations.contains(movie1), "Shouln't recommend watched movies"),
                () -> assertTrue(recommendations.contains(movie2), "Should contain movie2 (Comedy, Drama)"),
                () -> assertTrue(recommendations.contains(movie3), "Should contain movie3 (Action, Thriller)"),
                () -> assertTrue(recommendations.contains(movie4), "Should contain movie4 (Drama, Romance)"),
                () -> assertFalse(recommendations.contains(movie5), "Shouldn't recomment movie5 (Comedy, Family)"));

        // Test with multiple movies in watch history
        User multiMovieUser = new User("Multi Movie User", "MULTI002");
        multiMovieUser.addToWatchList(movie1); // Action, Drama
        multiMovieUser.addToWatchList(movie2); // Comedy, Drama

        List<Movie> multiMovieRecommendations = recommendationSystem.recommendMoviesForUser(multiMovieUser);

        // Check that we got recommendations (movie3, movie4, movie5) without the
        // watched ones (movie1, movie2)
        assertAll(
                () -> assertFalse(multiMovieRecommendations.isEmpty(), "Should return recommendations"),
                () -> assertFalse(multiMovieRecommendations.contains(movie1), "Shouldn't recommend watched movies"),
                () -> assertFalse(multiMovieRecommendations.contains(movie2), "Shouldn't recommend watched movies"),
                () -> assertTrue(multiMovieRecommendations.contains(movie3),
                        "Should contain movie3 (Action, Thriller)"),
                () -> assertTrue(multiMovieRecommendations.contains(movie4), "Should contain movie4 (Drama, Romance)"),
                () -> assertTrue(multiMovieRecommendations.contains(movie5), "Should contain movie5 (Comedy, Family)"));

        // Verify User categories are updated correctly
        List<String> userCategories = recommendationSystem.getUserCategories();
        assertAll(
                () -> assertTrue(userCategories.contains("Action"), "Should contain Action category"),
                () -> assertTrue(userCategories.contains("Drama"), "Should contain Drama category"),
                () -> assertTrue(userCategories.contains("Comedy"), "Should contain Comedy category"));
    }

    @Test
    // finding movies similar to a given reference movie
    public void testRecommendSimilarMovies() {
        // movie1 is Action, Drama
        List<Movie> similarMovies = recommendationSystem.recommendSimilarMovies(movie1);

        // Check that we got recommendations
        assertFalse(similarMovies.isEmpty(), "Should return similar movies");

        // Check that the original movie is not in the recommendations
        assertFalse(similarMovies.contains(movie1), "Should not include the original movie");

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

        assertTrue(hasSharedCategory, "Similar movies should share at least one category");
    }

    @Test
    // getting movies with the specified category
    public void testGetMoviesByCategory() {
        // Test getting action movies
        List<Movie> actionMovies = recommendationSystem.getMoviesByCategory("Action");

        // Should get movie1 and movie3
        assertAll(
                () -> assertEquals(2, actionMovies.size(), "Should return 2 action movies"),
                () -> assertTrue(actionMovies.contains(movie1), "Should contain movie1"),
                () -> assertTrue(actionMovies.contains(movie3), "Should contain movie3"));
    }

    @Test
    public void testGetUsers() {
        // Initial test with empty users list
        List<User> initialUsers = recommendationSystem.getUsers();
        assertTrue(initialUsers.isEmpty(), "Initial users list should be empty");

        // Add testUser to the recommendation system's users list manually
        recommendationSystem.getUsers().add(testUser);

        // Test after adding a user
        List<User> updatedUsers = recommendationSystem.getUsers();
        assertEquals(1, updatedUsers.size(), "Users list should contain one user");
        assertEquals(testUser, updatedUsers.get(0), "Users list should contain testUser");
    }

    @Test
    public void testEdgeCases() {
        // Test with null user
        List<Movie> recommendations1 = recommendationSystem.recommendMoviesForUser(null);
        assertTrue(recommendations1.isEmpty(), "Should return empty list for null user");

        // Test with user having no watch history
        User emptyUser = new User("Empty User", "EMPTY001");
        List<Movie> recommendations2 = recommendationSystem.recommendMoviesForUser(emptyUser);
        assertTrue(recommendations2.isEmpty(), "Should return empty list for user with no watch history");

        // Test with non-existent movie
        Movie nonExistentMovie = new Movie("Non-existent", "NonExistent");
        List<Movie> similarMovies = recommendationSystem.recommendSimilarMovies(nonExistentMovie);
        assertTrue(similarMovies.isEmpty(), "Should return empty list for non-existent movie");

        // Test with non-existent category
        List<Movie> nonExistentCategoryMovies = recommendationSystem.getMoviesByCategory("NonExistentCategory");
        assertTrue(nonExistentCategoryMovies.isEmpty(), "Should return empty list for non-existent category");
    }

    @Test
    public void testWriteRecommendationsToFile() throws IOException {
        // Create a temporary file for testing
        String tempFilePath = "src/test/resources/temp_recommendations.txt";

        // Ensure the directory exists
        Files.createDirectories(Paths.get("src/test/resources"));

        // Remove file if it already exists
        File tempFile = new File(tempFilePath);
        if (tempFile.exists()) {
            tempFile.delete();
        }

        // Test writing recommendations for a user without errors
        User regularUser = new User("Regular User", "REG001");
        regularUser.addToWatchList(movie1);
        recommendationSystem.getUsers().add(regularUser);

        recommendationSystem.writeRecommendationsToFile(regularUser, tempFilePath);

        // Verify file contents - updated for new formatting
        String content1 = new String(Files.readAllBytes(Paths.get(tempFilePath)));
        assertAll(
                () -> assertTrue(content1.contains("USER PROFILE"), "Should contain USER PROFILE section"),
                () -> assertTrue(content1.contains("Name: Regular User"), "Should contain user name"),
                () -> assertTrue(content1.contains("ID: REG001"), "Should contain user ID"),
                () -> assertTrue(content1.contains("MOVIE RECOMMENDATIONS"), "Should contain recommendations section"),
                () -> assertFalse(content1.contains("No recommendations available"), "Should have recommendations"));

        // Test writing for a user with an error message
        User userWithError = new User("Error User", "ERR001");
        userWithError.setErrorMessage("ERROR: Test error message");
        recommendationSystem.getUsers().add(userWithError);

        recommendationSystem.writeRecommendationsToFile(userWithError, tempFilePath);

        // Verify file contents again (file should have both users now) - updated for
        // new formatting
        String content2 = new String(Files.readAllBytes(Paths.get(tempFilePath)));
        assertAll(
                () -> assertTrue(content2.contains("Name: Error User"), "Should contain error user name"),
                () -> assertTrue(content2.contains("ID: ERR001"), "Should contain error user ID"),
                () -> assertTrue(content2.contains("VALIDATION ERROR"), "Should contain validation error section"),
                () -> assertTrue(content2.contains("ERROR: Test error message"), "Should contain error message"),
                () -> assertTrue(content2.contains("No recommendations available"),
                        "Should have no recommendations for error user"));

        // Clean up
        tempFile.delete();
    }

    @Test
    public void testLoadMoviesFromFile() throws IOException {
        // Create a temporary test movie file
        String tempMovieFilePath = "src/test/resources/test_movies.txt";
        Files.write(Paths.get(tempMovieFilePath),
                Arrays.asList("Test Movie, TM001", "Action, Drama",
                        "Another Movie, AM002", "Comedy"),
                StandardOpenOption.CREATE);

        // Load movies from the test file
        RecommendationSystem testSystem = new RecommendationSystem();
        testSystem.loadMoviesFromFile(tempMovieFilePath, new ArrayList<>());

        // Verify movies were loaded correctly
        assertEquals(2, testSystem.getMovieIdMap().size(), "Should load 2 movies");
        assertNotNull(testSystem.getMovieById("TM001"), "Should contain movie with ID TM001");
        assertNotNull(testSystem.getMovieById("AM002"), "Should contain movie with ID AM002");

        // Verify categories were loaded correctly
        assertFalse(testSystem.getMoviesByCategory("Action").isEmpty(), "Should have Action category");
        assertEquals(1, testSystem.getMoviesByCategory("Comedy").size(), "Should have 1 Comedy movie");

        // Clean up
        Files.delete(Paths.get(tempMovieFilePath));
    }

    @Test
    // Tests file I/O and data parsing
    public void testLoadUsersFromFile() throws IOException {
        // Set up movie data first
        String tempMovieFilePath = "src/test/resources/test_movies.txt";
        Files.write(Paths.get(tempMovieFilePath),
                Arrays.asList("Test Movie, TM001", "Action, Drama",
                        "Another Movie, AM002", "Comedy"),
                StandardOpenOption.CREATE);

        // Create a test user file
        String tempUserFilePath = "src/test/resources/test_users.txt";
        Files.write(Paths.get(tempUserFilePath),
                Arrays.asList("Test User, 12345678X", "TM001, AM002",
                        "Another User, 87654321Y", "TM001"),
                StandardOpenOption.CREATE);

        // Create validator to get valid movie IDs
        Validator validator = new Validator();
        validator.parseAndValidateMovies(tempMovieFilePath);
        Set<String> validMovieIds = validator.getValidMovieIds();

        // Load movies and users
        RecommendationSystem testSystem = new RecommendationSystem();
        testSystem.loadMoviesFromFile(tempMovieFilePath, new ArrayList<>());
        testSystem.loadUsersFromFile(tempUserFilePath, new ArrayList<>(), validMovieIds);

        // Verify users were loaded
        List<User> users = testSystem.getUsers();
        assertEquals(2, users.size(), "Should load 2 users");

        // Verify watch lists were populated
        User firstUser = users.get(0);
        assertEquals(2, firstUser.getWatchList().size(), "First user should have 2 movies in watchlist");

        // Clean up
        // Files.delete(Paths.get(tempMovieFilePath));
        // Files.delete(Paths.get(tempUserFilePath));
    }
}