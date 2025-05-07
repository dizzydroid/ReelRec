package com.reelrec;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap; 
import java.util.List;
import java.util.Map;    

public class AppEndToEndTest {

    @TempDir
    Path tempDir;

    private void createFile(Path directory, String fileName, List<String> lines) throws IOException {
        Files.write(directory.resolve(fileName), lines);
    }

    // Helper to read and parse recommendations
    private Map<String, List<String>> parseRecommendations(Path recommendationsFile) throws IOException {
        Map<String, List<String>> userRecs = new HashMap<>();
        if (!Files.exists(recommendationsFile)) return userRecs;

        List<String> lines = Files.readAllLines(recommendationsFile);
        String currentUserKey = null;
        List<String> currentContent = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) { // Separator line
                if (currentUserKey != null && !currentContent.isEmpty()) {
                    userRecs.put(currentUserKey, new ArrayList<>(currentContent));
                }
                currentUserKey = null;
                currentContent.clear();
                continue;
            }

            if (currentUserKey == null) {
                // Basic check for "Name, ID" format
                if (line.contains(",") && line.split(",").length == 2) {
                     currentUserKey = line;
                } else {
                    // If the line doesn't match the expected format, treat it as an error 
                    userRecs.computeIfAbsent("GLOBAL_ERROR", k -> new ArrayList<>()).add(line);
                }
            } else { // This is content (recommendation or error) for the currentUserKey
                currentContent.add(line);
            }
        }
        // Add the last user's content
        if (currentUserKey != null && !currentContent.isEmpty()) {
            userRecs.put(currentUserKey, currentContent);
        }
        return userRecs;
    }

    @Test
    void testReelRecAppConstructor() {
        new ReelRecApp();
    }

    @Test
    void testApp_recommendationsFileUnwritable_triggersIOException() throws IOException {
        Path moviesFile = tempDir.resolve("movies.txt");
        Path usersFile = tempDir.resolve("users.txt");
        // Create a directory instead of a file for recommendations
        Path recommendationsDir = tempDir.resolve("recommendations.txt");
        Files.createDirectory(recommendationsDir);

        createFile(tempDir, "movies.txt", List.of("The Matrix, TM001", "Action, SciFi"));
        createFile(tempDir, "users.txt", List.of("Neo, 12345678A", "TM001"));

        // This should trigger the catch block for IOException
        ReelRecApp.start(moviesFile.toString(), usersFile.toString(), recommendationsDir.toString());
    }

    @Test
    void testApp_validInputs_generatesRecommendations() throws IOException {
        Path moviesFile = tempDir.resolve("movies.txt");
        Path usersFile = tempDir.resolve("users.txt");
        Path recommendationsFile = tempDir.resolve("recommendations.txt");

        createFile(tempDir, "movies.txt", List.of(
            "The Matrix, TM001", "Action, SciFi",
            "Inception, I002", "Action, SciFi, Thriller"
        ));
        createFile(tempDir, "users.txt", List.of(
            "Neo, 12345678A", "TM001"
        ));

        ReelRecApp.start(moviesFile.toString(), usersFile.toString(), recommendationsFile.toString());

        assertTrue(Files.exists(recommendationsFile));
        Map<String, List<String>> recs = parseRecommendations(recommendationsFile);
        
        List<String> neosRecs = recs.get("Neo, 12345678A");
        assertNotNull(neosRecs, "Neo's recommendations/errors should exist");
        assertTrue(neosRecs.contains("Inception"), "Neo should get Inception recommended");
        assertFalse(neosRecs.contains("The Matrix"), "Neo should not get The Matrix recommended as it was watched");
    }

    @Test
    void testApp_movieFileMissing_outputsErrorToRecFile() throws IOException {
        Path usersFile = tempDir.resolve("users.txt");
        Path recommendationsFile = tempDir.resolve("recommendations.txt");
        String nonExistentMoviesFile = tempDir.resolve("non_existent_movies.txt").toString();

        createFile(tempDir, "users.txt", List.of("Test User, 98765432Z", "ANY001"));

        ReelRecApp.start(nonExistentMoviesFile, usersFile.toString(), recommendationsFile.toString());

        assertTrue(Files.exists(recommendationsFile));
        List<String> outputLines = Files.readAllLines(recommendationsFile); // Read directly for this simple case
        assertTrue(outputLines.stream().anyMatch(line -> line.contains("ERROR: File not found") || line.contains("ERROR: Cannot read file")),
                   "Recommendations file should contain file not found error for movies.txt");
    }

    @Test
    void testApp_usersFileMissing_outputsErrorToRecFile() throws IOException {
        Path moviesFile = tempDir.resolve("movies.txt");
        Path recommendationsFile = tempDir.resolve("recommendations.txt");
        String nonExistentUsersFile = tempDir.resolve("non_existent_users.txt").toString();

        createFile(tempDir, "movies.txt", List.of("Valid Movie, VM001", "Action"));

        ReelRecApp.start(moviesFile.toString(), nonExistentUsersFile, recommendationsFile.toString());

        assertTrue(Files.exists(recommendationsFile));
        List<String> outputLines = Files.readAllLines(recommendationsFile);
        assertTrue(outputLines.stream().anyMatch(line -> line.contains("ERROR: File not found") || line.contains("ERROR: Cannot read file")),
                   "Recommendations file should contain file not found error for users.txt");
    }

    @Test
    void testApp_userFileHasNonCriticalErrors_continuesProcessingAndFlagsUser() throws IOException {
        Path moviesFile = tempDir.resolve("movies_valid.txt");
        Path usersFile = tempDir.resolve("users_with_errors.txt");
        Path recommendationsFile = tempDir.resolve("recommendations.txt");

        createFile(tempDir, "movies_valid.txt", List.of(
            "The Matrix, TM001", "Action, SciFi"
        ));
        createFile(tempDir, "users_with_errors.txt", List.of(
            "Valid User, VU001", "TM001",
            "user with bad id, USER_BAD_ID", "TM001", // Invalid User ID
            "Another Valid, AVU02", "TM001"
        ));

        ReelRecApp.start(moviesFile.toString(), usersFile.toString(), recommendationsFile.toString());

        assertTrue(Files.exists(recommendationsFile));
        Map<String, List<String>> recs = parseRecommendations(recommendationsFile);

        assertNotNull(recs.get("Valid User, VU001"), "Valid User should be processed");
        // The user "user with bad id, USER_BAD_ID" should have an error message
        List<String> badUserContent = recs.get("user with bad id, USER_BAD_ID");
        assertNotNull(badUserContent, "User with bad ID should be in the output");
        assertTrue(badUserContent.stream().anyMatch(s -> s.contains("ERROR: User Id \"USER_BAD_ID\" is wrong")),
                   "User with bad ID should have an error message");
        assertTrue(badUserContent.contains("No recommendations") || badUserContent.size() == 1, // Error + "No Recs" or just error
                   "User with bad ID should have no actual movie recommendations");


        assertNotNull(recs.get("Another Valid, AVU02"), "Another Valid user should be processed");
    }

    @Test
    void testApp_emptyUsersFile_generatesNoUserRecommendations() throws IOException {
        Path moviesFile = tempDir.resolve("movies.txt");
        Path usersFile = tempDir.resolve("empty_users.txt"); // Empty file
        Path recommendationsFile = tempDir.resolve("recommendations.txt");

        createFile(tempDir, "movies.txt", List.of(
            "The Matrix, TM001", "Action, SciFi"
        ));
        createFile(tempDir, "empty_users.txt", List.of()); // Create an empty users file

        ReelRecApp.start(moviesFile.toString(), usersFile.toString(), recommendationsFile.toString());

        assertTrue(Files.exists(recommendationsFile));
        String content = Files.readString(recommendationsFile);
        assertTrue(content.trim().isEmpty(), "Recommendations file should be empty or whitespace if no users");
    }

    @Test
    void testApp_userFileHasInvalidUserIdFormat() throws IOException {
        Path moviesFile = tempDir.resolve("movies_valid.txt");
        Path usersFile = tempDir.resolve("users_invalid_id.txt");
        Path recommendationsFile = tempDir.resolve("recommendations.txt");

        // Create a valid movie file
        createFile(tempDir, "movies_valid.txt", List.of(
            "The Matrix, TM001", "Action, SciFi",
            "Inception, I002", "Action, SciFi, Thriller"
        ));
        
        // Create a users file with one invalid ID and one valid user
        createFile(tempDir, "users_invalid_id.txt", List.of(
            "Agent Smith, BAD_ID_1", "TM001", // Invalid ID format
            "Trinity, 11112222T", "I002"      // Valid user
        ));

        // Run the application
        ReelRecApp.start(moviesFile.toString(), usersFile.toString(), recommendationsFile.toString());

        // Verify the output
        assertTrue(Files.exists(recommendationsFile));
        Map<String, List<String>> recs = parseRecommendations(recommendationsFile);

        // Check the user with the invalid ID
        List<String> badUserContent = recs.get("Agent Smith, BAD_ID_1");
        assertNotNull(badUserContent, "User with bad ID should be in the output");
        assertTrue(badUserContent.stream().anyMatch(s -> s.contains("ERROR: User Id \"BAD_ID_1\" is wrong")), 
                "User with bad ID should have a format error message");

        assertTrue(badUserContent.contains("No recommendations") || badUserContent.size() == 1, 
                "User with bad ID should have no actual movie recommendations");


        // Check the valid user
        List<String> trinityRecs = recs.get("Trinity, 11112222T");
        assertNotNull(trinityRecs, "Trinity's recommendations/errors should exist");
        assertTrue(trinityRecs.contains("The Matrix"), "Trinity should be recommended The Matrix");
        assertFalse(trinityRecs.contains("Inception"), "Trinity should not be recommended Inception (watched)");
    }

    @Test
    void testApp_movieFileMissing_andRecFileUnwritable_triggersNestedIOException() throws IOException {
        Path usersFile = tempDir.resolve("users.txt");
        Path recommendationsDir = tempDir.resolve("recommendations.txt");
        Files.createDirectory(recommendationsDir);
        String nonExistentMoviesFile = tempDir.resolve("non_existent_movies.txt").toString();

        createFile(tempDir, "users.txt", List.of("Test User, 98765432Z", "ANY001"));

        ReelRecApp.start(nonExistentMoviesFile, usersFile.toString(), recommendationsDir.toString());
    }

    @Test
    void testApp_usersFileMissing_andRecFileUnwritable_triggersNestedIOException() throws IOException {
        Path moviesFile = tempDir.resolve("movies.txt");
        Path recommendationsDir = tempDir.resolve("recommendations.txt");
        Files.createDirectory(recommendationsDir);
        String nonExistentUsersFile = tempDir.resolve("non_existent_users.txt").toString();

        createFile(tempDir, "movies.txt", List.of("Valid Movie, VM001", "Action"));

        ReelRecApp.start(moviesFile.toString(), nonExistentUsersFile, recommendationsDir.toString());
    }

    @Test
    void testApp_loadUsersFromFileThrowsIOException_triggersOuterCatch() throws IOException {
        Path moviesFile = tempDir.resolve("movies.txt");
        Path usersDir = tempDir.resolve("users.txt");
        Path recommendationsFile = tempDir.resolve("recommendations.txt");
        Files.createDirectory(usersDir); // users.txt is a directory, not a file

        createFile(tempDir, "movies.txt", List.of("Valid Movie, VM001", "Action"));

        ReelRecApp.start(moviesFile.toString(), usersDir.toString(), recommendationsFile.toString());
    }

    @Test
    void testApp_movieFileUnreadable_triggersCannotReadFileBranch() throws IOException {
        Path usersFile = tempDir.resolve("users.txt");
        Path recommendationsFile = tempDir.resolve("recommendations.txt");
        Path unreadableMoviesFile = tempDir.resolve("unreadable_movies.txt");
        // Create a directory instead of a file to simulate an unreadable file
        Files.createDirectory(unreadableMoviesFile);

        createFile(tempDir, "users.txt", List.of("Test User, 98765432Z", "ANY001"));

        // This should trigger the 'ERROR: Cannot read file' branch
        ReelRecApp.start(unreadableMoviesFile.toString(), usersFile.toString(), recommendationsFile.toString());
    }

    @Test
    void testApp_userIdErrorRegexNotMatched() throws IOException {
        Path moviesFile = tempDir.resolve("movies.txt");
        Path usersFile = tempDir.resolve("users.txt");
        Path recommendationsFile = tempDir.resolve("recommendations.txt");

        createFile(tempDir, "movies.txt", List.of("The Matrix, TM001", "Action, SciFi"));
        // Intentionally malformed error: missing quotes around user id
        createFile(tempDir, "users.txt", List.of("Bad User, BAD_ID", "TM001"));

        ReelRecApp.start(moviesFile.toString(), usersFile.toString(), recommendationsFile.toString());
    }

}