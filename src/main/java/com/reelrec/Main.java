package com.reelrec;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to ReelRec - the Movie Recommendation System");
        
        System.out.println("Enter the path for the movies file (or press Enter to use default):");
        String moviesFilePath = scanner.nextLine().trim();
        if (moviesFilePath.isEmpty()) {
            moviesFilePath = "src/main/resources/movies.txt";
        }
        
        System.out.println("Enter the path for the users file (or press Enter to use default):");
        String usersFilePath = scanner.nextLine().trim();
        if (usersFilePath.isEmpty()) {
            usersFilePath = "src/main/resources/users.txt";
        }
        
        String recommendationsFilePath = "src/main/resources/recommendations.txt";

        scanner.close();

        // Create validator and validate the files
        InputValidator validator = new InputValidator();

        String movieValidationError = validator.parseAndValidateMovies(moviesFilePath);
        if (!movieValidationError.isEmpty()) {
            writeErrorToFile(recommendationsFilePath, movieValidationError);
            System.out.println("Error in movies file: " + movieValidationError);
            return;
        }

        String userValidationError = validator.parseAndValidateUsers(usersFilePath);       
        if (!userValidationError.isEmpty()) {
            writeErrorToFile(recommendationsFilePath, userValidationError);
            System.out.println("Error in users file: " + userValidationError);
            return;
        }

        // Create RecommendationSystem and load movies/users
        RecommendationSystem recSys = new RecommendationSystem();
        
        try {
            recSys.loadMoviesFromFile(moviesFilePath);
            recSys.loadUsersFromFile(usersFilePath);
        } catch (IOException e) {
            writeErrorToFile(recommendationsFilePath, "ERROR: Could not load input files");
            return;
        }

        // For every user, write recommendations to the output file
        List<User> users = recSys.getUsers();
        for (User user : users) {
            try {
                recSys.writeRecommendationsToFile(user, recommendationsFilePath);
            } catch (IOException e) {
                System.out.println("Error writing recommendations for user: " + user.getName());
            }
        }

        System.out.println("Recommendations have been generated in: " + recommendationsFilePath);
    }

    
    /**
     * Writes an error message to the recommendations file and overwrites any existing content.
     */
    private static void writeErrorToFile(String filePath, String errorMsg) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(errorMsg);
        } catch (IOException e) {
            System.out.println("Error writing error message to recommendations file.");
        }
    }
}
