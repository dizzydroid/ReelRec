package com.reelrec;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ReelRecCLI {
    public static void main(String[] args) {
        String moviesFilePath;
        String usersFilePath;
        String recommendationsFilePath;
        
        // Non-interactive mode: use command-line arguments if provided
        if (args.length >= 2) {
            moviesFilePath = args[0];
            usersFilePath = args[1];
            if (args.length >= 3) {
                recommendationsFilePath = args[2];
            } else {
                recommendationsFilePath = "recommendations.txt";
            }
        } else {
            // Interactive mode using console prompts
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Welcome to ReelRec - CLI Mode");
                System.out.print("Enter the movies file path (default: resources/movies.txt): ");
                moviesFilePath = reader.readLine().trim();
                if (moviesFilePath.isEmpty()) {
                    moviesFilePath = "resources/movies.txt";
                }
                System.out.print("Enter the users file path (default: resources/users.txt): ");
                usersFilePath = reader.readLine().trim();
                if (usersFilePath.isEmpty()) {
                    usersFilePath = "resources/users.txt";
                }
                System.out.print("Enter the recommendations output file path (default: recommendations.txt): ");
                recommendationsFilePath = reader.readLine().trim();
                if (recommendationsFilePath.isEmpty()) {
                    recommendationsFilePath = "recommendations.txt";
                }
            } catch (Exception e) {
                System.err.println("Error reading input: " + e.getMessage());
                return;
            }
        }
        
        // Print a welcome message and the file paths being used
        System.out.println("\n--- ReelRec CLI ---");
        System.out.println("Movies file: " + moviesFilePath);
        System.out.println("Users file: " + usersFilePath);
        System.out.println("Output recommendations file: " + recommendationsFilePath);
        System.out.println("-------------------\n");
        
        // Invoke the core functionality using ReelRecApp
        ReelRecApp.start(moviesFilePath, usersFilePath, recommendationsFilePath);
    }
}
