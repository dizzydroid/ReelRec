package com.reelrec;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        printWelcomeMessage();

        String moviesFilePath = "src/main/resources/movies.txt";
        String usersFilePath = "src/main/resources/users.txt";

        String recommendationsFilePath = "src/main/resources/recommendations.txt";
        // Clear recommendations file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(recommendationsFilePath, false))) {
        } catch (IOException e) {
            System.out.println("❌ Error clearing recommendations file: " + e.getMessage());
        }

        // Create one Validator instance
        Validator validator = new Validator();

        // Validate movies
        List<String> movieValidationErrors = validator.parseAndValidateMovies(moviesFilePath);
        Set<String> validMovieIds = validator.getValidMovieIds(); // valid IDs from movies file
        ArrayList<Integer> invalidMovieLines = validator.extractErrorLines(movieValidationErrors);
        if (!movieValidationErrors.isEmpty()) {
            printValidationWarnings("MOVIE FILE WARNINGS", movieValidationErrors);
            // Check if there are critical errors that prevent any processing
            for (String error : movieValidationErrors) {
                if (error.contains("ERROR: File not found") || error.contains("ERROR: Cannot read file")) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(recommendationsFilePath))) {
                        writer.write(error);
                    } catch (IOException e) {
                        System.out.println("❌ Error writing error message to recommendations file.");
                    }
                    return; // Exit program if critical error
                }
            }
        }

        // Validate users
        List<String> userValidationErrors = validator.parseAndValidateUsers(usersFilePath);
        // ArrayList<Integer> invalidUserLines =
        // validator.extractErrorLines(userValidationErrors);

        // Create a map to store user ID errors
        Map<String, String> userIdErrors = new HashMap<>();
        for (String error : userValidationErrors) {
            // Check if this is a user ID error
            if (error.contains("ERROR: User Id")) {
                // Try to extract the user ID from the error message
                Pattern pattern = Pattern.compile("ERROR: User Id \"([^\"]+)\"");
                Matcher matcher = pattern.matcher(error);
                if (matcher.find()) {
                    String userId = matcher.group(1);
                    userIdErrors.put(userId, error);
                }
            }
        }

        if (!userValidationErrors.isEmpty()) {
            printValidationWarnings("USER FILE WARNINGS", userValidationErrors);
            // Check if there are critical errors that prevent any processing
            for (String error : userValidationErrors) {
                if (error.contains("ERROR: File not found") || error.contains("ERROR: Cannot read file")) {
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(recommendationsFilePath))) {
                        writer.write(error);
                    } catch (IOException e) {
                        System.out.println("❌ Error writing error message to recommendations file.");
                    }
                    return; // Exit program if critical error
                }
            }
        }

        // Create RecommendationSystem and load movies/users
        RecommendationSystem recSys = new RecommendationSystem();
        try {
            System.out.println("\n📂 Loading data files...");
            recSys.loadMoviesFromFile(moviesFilePath, invalidMovieLines);

            // We want to process all users, including those with validation errors
            recSys.loadUsersFromFile(usersFilePath, new ArrayList<>(), validMovieIds);
            System.out.println("✅ Data loaded successfully!");
        } catch (IOException e) {
            System.out.println("❌ ERROR: Could not load input files: " + e.getMessage());
            return;
        }

        // Apply user ID errors to the loaded users
        List<User> users = recSys.getUsers();
        for (User user : users) {
            // Check if this user has an ID error
            if (userIdErrors.containsKey(user.getId())) {
                // ID error takes precedence over movie errors
                user.setErrorMessage(userIdErrors.get(user.getId()));
                user.clearWatchList(); // Clear watchlist since user is invalid
            }
        }

        // For every user, write recommendations to the output file
        System.out.println("\n🎬 Generating recommendations...");
        int processedUsers = 0;
        for (User user : users) {
            try {
                recSys.writeRecommendationsToFile(user, recommendationsFilePath);
                System.out.println("   ✅ Processed user: " + user.getName());
                processedUsers++;
            } catch (IOException e) {
                System.out.println("   ❌ Error writing recommendations for user: " + user.getName());
            }
        }

        System.out.println("\n🎉 Completed! Successfully processed " + processedUsers + " users.");
        System.out.println("📄 Recommendations saved to: " + recommendationsFilePath);
    }

    /**
     * Prints a fancy welcome message for the application
     */
    private static void printWelcomeMessage() {
        System.out.println("\n" +
                "╔══════════════════════════════════════════════════════════════╗\n" +
                "║                                                              ║\n" +
                "║  🎬 🍿  WELCOME TO REELREC - THE MOVIE RECOMMENDER  🍿 🎬   ║\n" +
                "║                                                              ║\n" +
                "╚══════════════════════════════════════════════════════════════╝\n");
        System.out.println("Your personal movie recommendation system is starting up...\n");
    }

    /**
     * Prints validation warnings in a more readable format
     */
    private static void printValidationWarnings(String title, List<String> warnings) {
        System.out.println("\n⚠️ " + title + " ⚠️");
        for (String warning : warnings) {
            System.out.println("   • " + warning);
        }
        System.out.println();
    }
}
