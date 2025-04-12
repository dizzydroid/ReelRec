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
        List<Movie> result = new ArrayList<>();
        List<Movie> movies = categoryMovies.get(category);
        
        if (movies == null) {
            return result;
        }
        
        // Return all movies in the category
        return new ArrayList<>(movies);
    }

    /**
     * Loads movies from a text file.
     * Format: "Movie Title, MovieID" followed by categories on next line
     */
    public void loadMoviesFromFile(String filePath, ArrayList<Integer> invalidLines) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int lineNumber = 0;
            String movieInfoLine;
            while ((movieInfoLine = reader.readLine()) != null) {
                lineNumber++;
                movieInfoLine = movieInfoLine.trim();
                if (movieInfoLine.isEmpty() || invalidLines.contains(lineNumber))
                    continue;
                
                // This is the movie info line (title and ID)
                if (!movieInfoLine.contains(",")) {
                    // Handle bad formatting if needed
                    continue;
                }
                String[] parts = movieInfoLine.split(",", 2);
                String title = parts[0].trim();
                String id = parts[1].trim();
                
                // Construct Movie with correct parameter order: (id, title)
                Movie currentMovie = new Movie(id, title);
                movieIdMap.put(id, currentMovie);
                movieCategories.put(currentMovie, new ArrayList<>());
                
                // Immediately read the next line for genres
                String genresLine = reader.readLine();
                lineNumber++;
                if (genresLine != null && !genresLine.trim().isEmpty() && !invalidLines.contains(lineNumber)) {
                    String[] genres = genresLine.split(",");
                    for (String genre : genres) {
                        genre = genre.trim();
                        // Add genre to this movie
                        movieCategories.get(currentMovie).add(genre);
                        
                        // Add this movie to the corresponding category in the map
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
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String userInfoLine;
            int lineNumber = 0;
            
            while ((userInfoLine = reader.readLine()) != null) {
                lineNumber++;
                userInfoLine = userInfoLine.trim();
                if (userInfoLine.isEmpty() || invalidLines.contains(lineNumber))
                    continue;
                
                // Parse the user info line (Name, UserID)
                String[] parts = userInfoLine.split(",", 2);
                if(parts.length != 2) {
                    // Log or skip improperly formatted line
                    continue;
                }
                String name = parts[0].trim();
                String id = parts[1].trim();
                User currentUser = new User(name, id);
                users.add(currentUser);

                System.out.println("User " + currentUser.getName() + " watch list: ");
                for (Movie m : currentUser.getWatchList()) {
                    System.out.println(" - " + m.getID() + ": " + m.getName());
                }
    
                // Read the next line for movie IDs (watch list)
                String moviesLine = reader.readLine();
                lineNumber++;
                if (moviesLine != null && !moviesLine.trim().isEmpty() && !invalidLines.contains(lineNumber)) {
                    String[] movieIds = moviesLine.split(",");
                    for (String movieId : movieIds) {
                        movieId = movieId.trim();
                        Movie movie = movieIdMap.get(movieId);
                        if (movie != null) {
                            currentUser.addToWatchList(movie);
                        }
                    }
                }
            }
        }
    }
          
    
    /**
     * Writes recommendations for a user to a file.
     * Format:nUsername, UserID followed by recommended movie titles on the next line
     */
    public void writeRecommendationsToFile(User user, String outputPath) throws IOException {
        List<Movie> recommendations = recommendMoviesForUser(user);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, true))) {
            // Write user name and ID
            writer.write(user.getName() + ", " + user.getId());
            writer.newLine();
            
            // Write recommended movie titles (not IDs)
            if (!recommendations.isEmpty()) {
                StringJoiner joiner = new StringJoiner(", ");
                for (Movie movie : recommendations) {
                    joiner.add(movie.getName());
                }
                writer.write(joiner.toString());
            } else {
                writer.write("No recommendations");
            }
            writer.newLine();
            writer.newLine(); // Empty line between users
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