package com.reelrec;

import com.reelrec.stubs.StubRecommendationSystem;
import com.reelrec.stubs.StubValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests demonstrating Top-Down technique for ReelRecProcessor
 * using Stubs for Validator and RecommendationSystem dependencies.
 */
public class IntegrationTest {

    @TempDir
    Path tempDir;

    private StubValidator stubValidator;
    private StubRecommendationSystem stubRecSys;
    private Path moviesFile;
    private Path usersFile;
    private Path recommendationsFile;
    private ReelRecProcessor processor; // The object under test

    @BeforeEach
    void setUp() throws IOException {
        stubValidator = new StubValidator();
        stubRecSys = new StubRecommendationSystem();

        moviesFile = tempDir.resolve("movies.txt");
        usersFile = tempDir.resolve("users.txt");
        recommendationsFile = tempDir.resolve("recommendations.txt");

        // Create dummy files needed for paths to be valid
        if (!Files.exists(moviesFile)) Files.createFile(moviesFile);
        if (!Files.exists(usersFile)) Files.createFile(usersFile);

        // Instantiate the REAL processor but inject STUBS
        processor = new ReelRecProcessor(
            moviesFile.toString(), usersFile.toString(), recommendationsFile.toString(),
            stubValidator, stubRecSys
        );
    }

    @Test
    void testProcessorExecute_happyPath_callsDependencies() {
        // --- Arrange ---
        stubValidator.movieErrorsToReturn = List.of();
        stubValidator.userErrorsToReturn = List.of();
        stubValidator.validMovieIdsToReturn = Set.of("M1", "M2");
        User user1 = new User("User One", "U1");
        User user2 = new User("User Two", "U2");
        stubRecSys.usersToReturn = List.of(user1, user2);

        // --- Act ---
        processor.execute(); // Call the method under test

        // --- Assert ---
        // Verify interactions with stubs
        assertEquals(1, stubRecSys.loadedMoviesFiles.size());
        assertEquals(1, stubRecSys.loadedUsersFiles.size());
        assertEquals(2, stubRecSys.writeRecommendationsCallCount);
        assertTrue(stubRecSys.usersForWhichRecsWereWritten.contains(user1));
        assertTrue(stubRecSys.usersForWhichRecsWereWritten.contains(user2));
    }

    @Test
    void testProcessorExecute_criticalMovieValidationError_exitsEarly() {
        // --- Arrange ---
        stubValidator.movieErrorsToReturn = List.of("ERROR: Cannot read file " + moviesFile.toString());

        // --- Act ---
        processor.execute();

        // --- Assert ---
        assertTrue(stubRecSys.loadedMoviesFiles.isEmpty());
        assertTrue(stubRecSys.loadedUsersFiles.isEmpty());
        assertEquals(0, stubRecSys.writeRecommendationsCallCount);
         try {
             assertTrue(Files.exists(recommendationsFile));
             List<String> output = Files.readAllLines(recommendationsFile);
             assertTrue(output.stream().anyMatch(s -> s.contains("ERROR: Cannot read file")), "Output file should contain critical error");
         } catch (IOException e) { fail("IOException reading recommendations file: " + e); }
    }

     @Test
    void testProcessorExecute_recSysLoadError_exitsWithErrorWritten() {
        // --- Arrange ---
        stubValidator.movieErrorsToReturn = List.of();
        stubValidator.userErrorsToReturn = List.of();
        stubValidator.validMovieIdsToReturn = Set.of("M1");
        stubRecSys.throwLoadMoviesError = true;

        // --- Act ---
        processor.execute();

        // --- Assert ---
        assertEquals(1, stubRecSys.loadedMoviesFiles.size()); // Load was attempted
        assertTrue(stubRecSys.loadedUsersFiles.isEmpty());
        assertEquals(0, stubRecSys.writeRecommendationsCallCount);
         try {
             assertTrue(Files.exists(recommendationsFile));
             List<String> output = Files.readAllLines(recommendationsFile);
             assertTrue(output.stream().anyMatch(s -> s.contains("ERROR: Could not load input files")), "Output file should contain load error");
         } catch (IOException e) { fail("IOException reading recommendations file: " + e); }
    }

     @Test
    void testProcessorExecute_appliesNonCriticalUserIdErrorToUser() {
         // --- Arrange ---
        stubValidator.movieErrorsToReturn = List.of();
        stubValidator.userErrorsToReturn = List.of("ERROR: User Id \"BAD_ID\" is wrong");
        stubValidator.validMovieIdsToReturn = Set.of("M1");

        User badUser = new User("Agent Smith", "BAD_ID");
        User goodUser = new User("Trinity", "GOOD_ID");
        stubRecSys.usersToReturn = new ArrayList<>(List.of(badUser, goodUser));

        // --- Act ---
        processor.execute();

         // --- Assert ---
         assertEquals(2, stubRecSys.writeRecommendationsCallCount);

         User passedBadUser = stubRecSys.usersForWhichRecsWereWritten.stream()
                                     .filter(u -> u.getId().equals("BAD_ID")).findFirst().orElse(null);
         User passedGoodUser = stubRecSys.usersForWhichRecsWereWritten.stream()
                                     .filter(u -> u.getId().equals("GOOD_ID")).findFirst().orElse(null);

         assertNotNull(passedBadUser);
         assertNotNull(passedBadUser.getErrorMessage());
         assertTrue(passedBadUser.getErrorMessage().contains("ERROR: User Id \"BAD_ID\" is wrong"));
         assertTrue(passedBadUser.getWatchList().isEmpty()); // Check watchlist cleared

         assertNotNull(passedGoodUser);
         assertNull(passedGoodUser.getErrorMessage());
    }
}