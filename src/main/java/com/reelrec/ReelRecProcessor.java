package com.reelrec;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This class encapsulates the core processing logic, making it testable
public class ReelRecProcessor {

    private final String moviesFilePath;
    private final String usersFilePath;
    private final String recommendationsFilePath;
    private final Validator validator;
    private final RecommendationSystem recSys;

    // Constructor to inject dependencies and paths
    public ReelRecProcessor(String moviesFilePath, String usersFilePath, String recommendationsFilePath,
                             Validator validator, RecommendationSystem recSys) {
        this.moviesFilePath = moviesFilePath;
        this.usersFilePath = usersFilePath;
        this.recommendationsFilePath = recommendationsFilePath;
        this.validator = validator; // Use injected validator
        this.recSys = recSys;       // Use injected recSys
    }

    public void execute() {
        // NOTE: We don't call printWelcomeMessage here, as it's not part of the processing logic.

        // --- Clear recommendations file ---
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(recommendationsFilePath, false))) {
           // Cleared
        } catch (IOException e) {
            System.out.println("❌ Error clearing recommendations file in Processor: " + e.getMessage());
        }

        // === Use the INJECTED validator ===
        List<String> movieValidationErrors;
        Set<String> validMovieIds;
        ArrayList<Integer> invalidMovieLines;

        try {
            movieValidationErrors = validator.parseAndValidateMovies(moviesFilePath);
            validMovieIds = validator.getValidMovieIds();
            invalidMovieLines = validator.extractErrorLines(movieValidationErrors);
        } catch (Exception ex) {
             handleProcessingError("ERROR during movie validation: " + ex.getMessage());
             return;
        }

        if (!movieValidationErrors.isEmpty()) {
            printValidationWarnings("MOVIE FILE WARNINGS (Processor)", movieValidationErrors); // Indicate source
            for (String error : movieValidationErrors) {
                if (error.startsWith("ERROR: File not found") || error.startsWith("ERROR: Cannot read file")) {
                    handleProcessingError(error); // Write error and exit this method
                    return;
                }
            }
        }

        // === Use the INJECTED validator ===
        List<String> userValidationErrors;
        Map<String, String> userIdErrors = new HashMap<>();

        try {
             userValidationErrors = validator.parseAndValidateUsers(usersFilePath);
             for (String error : userValidationErrors) {
                if (error.contains("ERROR: User Id")) {
                    Pattern pattern = Pattern.compile("ERROR: User Id \"([^\"]+)\"");
                    Matcher matcher = pattern.matcher(error);
                    if (matcher.find()) {
                        userIdErrors.put(matcher.group(1), error);
                    }
                }
            }
        } catch (Exception ex) {
             handleProcessingError("ERROR during user validation: " + ex.getMessage());
             return;
        }

        if (!userValidationErrors.isEmpty()) {
            printValidationWarnings("USER FILE WARNINGS (Processor)", userValidationErrors);
             for (String error : userValidationErrors) {
                 if (error.startsWith("ERROR: File not found") || error.startsWith("ERROR: Cannot read file")) {
                     handleProcessingError(error);
                     return;
                 }
            }
        }

        // === Use the INJECTED RecommendationSystem (recSys) ===
        List<User> users;
        try {
            // System.out.println("\n📂 Loading data files (Processor)...");
            recSys.loadMoviesFromFile(moviesFilePath, invalidMovieLines);
            recSys.loadUsersFromFile(usersFilePath, new ArrayList<>(), validMovieIds);
            // System.out.println("✅ Data loaded successfully (Processor)!");
            users = recSys.getUsers();
        } catch (IOException e) {
            handleProcessingError("ERROR: Could not load input files: " + e.getMessage());
            return;
        }

        // Apply user ID format/uniqueness errors
        for (User user : users) {
            if (userIdErrors.containsKey(user.getId()) && user.getErrorMessage() == null) {
                user.setErrorMessage(userIdErrors.get(user.getId()));
                user.clearWatchList();
            }
        }

        // Generate recommendations and write output
        // System.out.println("\n🎬 Generating recommendations (Processor)...");
        int processedUsers = 0;
        if (users.isEmpty()) {
             // System.out.println("   No users loaded or found to process (Processor).");
              try (BufferedWriter writer = new BufferedWriter(new FileWriter(recommendationsFilePath, true))) {
                 writer.newLine(); writer.write("-- No users processed --"); writer.newLine();
             } catch (IOException writeEx) { /* Ignore */}
        }

        for (User user : users) {
            try {
                recSys.writeRecommendationsToFile(user, recommendationsFilePath);
                processedUsers++;
            } catch (IOException e) {
                System.out.println("   ❌ Error writing recommendations in Processor for user: " + user.getName() + " - " + e.getMessage());
            }
        }

        System.out.println("\n🎉 Completed (Processor)! Successfully processed " + processedUsers + " users.");
        // System.out.println("📄 Recommendations saved to (Processor): " + recommendationsFilePath);
    }

    // Helper to centralize writing critical errors before returning
    private void handleProcessingError(String errorMessage) {
         System.out.println("   Processing error detected: " + errorMessage);
          try (BufferedWriter writer = new BufferedWriter(new FileWriter(recommendationsFilePath))) { // Overwrite with error
             writer.write("❌ " + errorMessage);
             writer.newLine();
         } catch (IOException writeEx) {
             System.out.println("❌ Error writing processing error message to recommendations file: " + writeEx.getMessage());
         }
    }

    private static void printValidationWarnings(String title, List<String> warnings) {
        System.out.println("\n⚠️ " + title + " ⚠️");
        if (warnings.isEmpty()){
             System.out.println("   No warnings.");
        } else {
            for (String warning : warnings) {
                System.out.println("   • " + warning);
            }
        }
        System.out.println();
    }
}