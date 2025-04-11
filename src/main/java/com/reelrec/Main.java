package com.reelrec;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to ReelRec - the Movie Recommendation System");

        if (args.length != 2) {
            System.out.println("Usage: java MovieRecommendationSystem <movies_file> <users_file>");
            return;
        }

        String moviesFilePath = args[0];
        String usersFilePath = args[1];

        if (moviesFilePath.isEmpty()) {
            moviesFilePath = "src/main/resources/movies.txt";
        }

        if (usersFilePath.isEmpty()) {
            usersFilePath = "src/main/resources/users.txt";
        }
        
        String recommendationsFilePath = "src/main/resources/recommendations.txt";

        // Create validator and validate the files
        Validator validator = new Validator();
        List<String> movieValidationError = validator.parseAndValidateMovies(moviesFilePath);
        ArrayList<Integer> invalidMovieLines = validator.extractErrorLines(movieValidationError);


        if (!movieValidationError.isEmpty()) {
            writeErrorToFile(recommendationsFilePath, movieValidationError.get(0));
            System.out.println("Error in movies file: " + movieValidationError);
            return;
        }

        List<String> userValidationError = validator.parseAndValidateUsers(usersFilePath);       
        if (!userValidationError.isEmpty()) {
            writeErrorToFile(recommendationsFilePath, userValidationError.get(0));
            System.out.println("Error in users file: " + userValidationError);
            return;
        }

        // Create RecommendationSystem and load movies/users
        RecommendationSystem recSys = new RecommendationSystem();
        try {
            recSys.loadMoviesFromFile(moviesFilePath, invalidMovieLines);
            recSys.loadUsersFromFile(usersFilePath, invalidMovieLines);
        } catch(IOException e){
            writeErrorToFile(recommendationsFilePath, "ERROR: Could not load input files");
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
