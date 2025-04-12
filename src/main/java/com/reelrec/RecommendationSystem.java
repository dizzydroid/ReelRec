package com.reelrec;

import java.io.*;
import java.util.*;

public class RecommendationSystem {
    
    private TreeMap<Movie, List<String>> movieCategories;
    private TreeMap<String, List<Movie>> categoryMovies;
    private Map<String, Movie> movieIdMap;
    private List<User> users;
    private List<String> userCategories; // To store user categories for debugging
 
    public RecommendationSystem() {
        this.movieCategories = new TreeMap<>();
        this.categoryMovies = new TreeMap<>();
        this.movieIdMap = new HashMap<>();
        this.users = new ArrayList<>();
        this.userCategories = new ArrayList<>(); 
    }
    
    public RecommendationSystem(TreeMap<Movie, List<String>> movieCategories, 
                               TreeMap<String, List<Movie>> categoryMovies) {
        this.movieCategories = movieCategories;
        this.categoryMovies = categoryMovies;
        this.movieIdMap = new HashMap<>();
        this.users = new ArrayList<>();
        
        // Populate movieIdMap for existing movies
        for (Movie movie : movieCategories.keySet()) {
            movieIdMap.put(movie.getID(), movie);
        }
    }
    
    public List<User> getUsers() {
        return users;
    }
    
    public Movie getMovieById(String id) {
        return movieIdMap.get(id);
    }

    public Map<String, Movie> getMovieIdMap() {
        return movieIdMap;
    }
    
    public List<Movie> getMoviesByCategory(String category) {
        List<Movie> movies = categoryMovies.get(category);
        
        if (movies == null) {
            return new ArrayList<>();
        }
        
        // Return all movies in the category
        return new ArrayList<>(movies);
    } 

    /**
     * Loads movies from a text file.
     * Format: "Movie Title, MovieID" followed by categories on next line
     */
    public void loadMoviesFromFile(String filePath, ArrayList<Integer> invalidLines) throws IOException {
        Validator validator = new Validator();  // Use a Validator instance
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int lineNumber = 0;
            String movieInfoLine;
            while ((movieInfoLine = reader.readLine()) != null) {
                lineNumber++;
                movieInfoLine = movieInfoLine.trim();
                if (movieInfoLine.isEmpty() || invalidLines.contains(lineNumber))
                    continue;
                
                if (!movieInfoLine.contains(",")) {
                    // Bad formatting – you might log an error and then skip the record
                    continue;
                }
                String[] parts = movieInfoLine.split(",", 2);
                String title = parts[0].trim();
                String id = parts[1].trim();
                
                // Validate title and id using Validator methods:
                String error = validator.checkMovieTitle(title);
                if (error.equals("")) {
                    error = validator.checkMovieId(id, title);
                }
                if (!error.equals("")) {
                    // (Optional) Log the error message for this movie.
                    // Do not add an invalid movie to the maps.
                    System.out.println("Skipping movie record due to error: " + error + " at line " + lineNumber);
                    // Also, skip reading the genres line.
                    reader.readLine();  // consume the genre line
                    lineNumber++;
                    continue;
                }
                
                // Create Movie with proper constructor ordering.
                Movie currentMovie = new Movie(id, title);
                movieIdMap.put(id, currentMovie);
                movieCategories.put(currentMovie, new ArrayList<>());
                
                // Immediately read and process the genres line.
                String genresLine = reader.readLine();
                lineNumber++;
                if (genresLine != null && !genresLine.trim().isEmpty() && !invalidLines.contains(lineNumber)) {
                    String[] genres = genresLine.split(",");
                    for (String genre : genres) {
                        genre = genre.trim();
                        String genreError = validator.checkMovieGenre(genre);
                        if (!genreError.equals("")) {
                            System.out.println("Skipping genre for movie " + id + ": " + genreError + " at line " + lineNumber);
                            continue;  // Skip this genre if it’s invalid.
                        }
                        movieCategories.get(currentMovie).add(genre);
                        if (!categoryMovies.containsKey(genre)) {
                            categoryMovies.put(genre, new ArrayList<>());
                        }
                        categoryMovies.get(genre).add(currentMovie);
                    }
                }
            }
        }
    }
    
    /**
     * Loads user data from a text file.
     * Format: "Username, UserID" followed by watched movie IDs on next line
     */
    public void loadUsersFromFile(String filePath, ArrayList<Integer> invalidLines) throws IOException {
        Validator validator = new Validator(); // new validator (or reuse one if available)
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String userInfoLine;
            int lineNumber = 0;
            
            while ((userInfoLine = reader.readLine()) != null) {
                lineNumber++;
                userInfoLine = userInfoLine.trim();
                if (userInfoLine.isEmpty() || invalidLines.contains(lineNumber))
                    continue;
                
                String[] parts = userInfoLine.split(",", 2);
                if (parts.length != 2) {
                    // If formatting is wrong, create a dummy user with error.
                    User errorUser = new User("Unknown", "Unknown");
                    errorUser.setErrorMessage("ERROR: User Formatting is wrong at line " + lineNumber);
                    users.add(errorUser);
                    // Skip the next line because it should be the watch list.
                    reader.readLine();
                    lineNumber++;
                    continue;
                }
                String name = parts[0].trim();
                String id = parts[1].trim();
                String error = validator.checkUserName(name);
                if (error.equals("")) {
                    error = validator.checkUserId(id);
                }
                User currentUser = new User(name, id);
                if (!error.equals("")) {
                    // Mark user as having an error.
                    currentUser.setErrorMessage(error + " at line " + lineNumber);
                    // Even if the watchlist line is present, we mark this user as invalid.
                    // But still consume the next line.
                    reader.readLine();
                    lineNumber++;
                    users.add(currentUser);
                    continue;
                }
                
                // Read the next line for watched movies
                String moviesLine = reader.readLine();
                lineNumber++;
                if (moviesLine != null && !moviesLine.trim().isEmpty() && !invalidLines.contains(lineNumber)) {
                    String[] movieIds = moviesLine.split(",");
                    for (String movieId : movieIds) {
                        movieId = movieId.trim();
                        // If the movie is not found (because it might have been invalid), record an error.
                        if (!movieIdMap.containsKey(movieId)) {
                            currentUser.setErrorMessage("ERROR: Movie Id \"" + movieId + "\" at line " + lineNumber + " is not in the movies file");
                            // Optionally, break out after the first error.
                            break;
                        } else {
                            currentUser.addToWatchList(movieIdMap.get(movieId));
                        }
                    }
                } else {
                    // No movies provided
                    currentUser.setErrorMessage("ERROR: User has no movies at line " + lineNumber);
                }
                users.add(currentUser);
            }
        }
    }
    
    /**
     * Writes recommendations for a user to a file.
     * Format: Username, UserID followed by recommended movie titles on the next line
     */
    public void writeRecommendationsToFile(User user, String outputPath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, true))) {
            // Write user name and ID (always output in first line)
            writer.write(user.getName() + ", " + user.getId());
            writer.newLine();
            
            if (user.getErrorMessage() != null) {
                // Write the error message in place of recommendations.
                writer.write(user.getErrorMessage());
            } else {
                // Generate recommendations as before.
                List<Movie> recommendations = recommendMoviesForUser(user);
                if (!recommendations.isEmpty()) {
                    StringJoiner joiner = new StringJoiner(", ");
                    for (Movie movie : recommendations) {
                        joiner.add(movie.getName());
                    }
                    writer.write(joiner.toString());
                } else {
                    writer.write("No recommendations");
                }
            }
            writer.newLine();
            writer.newLine(); // Blank line between user records
        }
    }    
    
    /**
     * Recommends movies based on a user's watch history.
     */
    public List<Movie> recommendMoviesForUser(User user) {
        List<Movie> recommendations = new ArrayList<>();
        
        // Return empty list if user has no watch history
        if (user == null || user.getWatchList() == null || user.getWatchList().isEmpty()) {
            return recommendations;
        }
        
        // Find categories the user has watched
        userCategories.clear();
        for (Movie movie : user.getWatchList()) {
            List<String> genresForMovie = movieCategories.get(movie);
            System.out.println("For user " + user.getName() + ", movie " + movie.getID() + " genres: " + genresForMovie);
            if (genresForMovie != null) {
                for (String category : genresForMovie) {
                    if (!userCategories.contains(category)) {
                        userCategories.add(category);
                    }
                }
            }
            // List<String> categories = movieCategories.get(movie);
            // if (categories != null) {
            //     for (String category : categories) {
            //         if (!userCategories.contains(category)) {
            //             userCategories.add(category);
            //         }
            //     }
            // }
        }
        
        // Create a list of movies the user has already watched
        List<Movie> watchedMovies = user.getWatchList();
        
        // Find movies in those categories that the user hasn't watched
        for (String category : userCategories) {
            List<Movie> moviesInCategory = categoryMovies.get(category);
            if (moviesInCategory != null) {
                for (Movie movie : moviesInCategory) {
                    if (!watchedMovies.contains(movie) && !recommendations.contains(movie)) {
                        recommendations.add(movie);
                    }
                }
            }
        }
        
        return recommendations;
    }

    public List<String> getUserCategories() {
        return new ArrayList<>(userCategories); // Return a copy to avoid external modification
    }

    /**
     * Recommends similar movies to a given movie based on shared categories.
     */
    public List<Movie> recommendSimilarMovies(Movie movie) {
        List<Movie> similarMovies = new ArrayList<>();
        
        // Return empty list if movie doesn't exist
        if (movie == null || !movieCategories.containsKey(movie)) {
            return similarMovies;
        }
        
        // Get categories of the given movie
        List<String> categories = movieCategories.get(movie);
        if (categories == null) {
            return similarMovies;
        }
        
        // Find movies that share at least one category
        for (String category : categories) {
            List<Movie> moviesInCategory = categoryMovies.get(category);
            if (moviesInCategory != null) {
                for (Movie potentialSimilar : moviesInCategory) {
                    // Skip the original movie and already added movies
                    if (!potentialSimilar.equals(movie) && !similarMovies.contains(potentialSimilar)) {
                        similarMovies.add(potentialSimilar);
                    }
                }
            }
        }
        
        return similarMovies;
    }
}