package com.reelrec;

import java.io.*;
import java.util.*;

public class RecommendationSystem {
    
    private TreeMap<Movie, List<String>> movieCategories;
    private TreeMap<String, List<Movie>> categoryMovies;
    private Map<String, Movie> movieIdMap;

    public RecommendationSystem() {
        this.movieCategories = new TreeMap<>();
        this.categoryMovies = new TreeMap<>();
        this.movieIdMap = new HashMap<>();
    }

    
    public RecommendationSystem(TreeMap<Movie, List<String>> movieCategories, 
                               TreeMap<String, List<Movie>> categoryMovies) {
        this.movieCategories = movieCategories;
        this.categoryMovies = categoryMovies;
        this.movieIdMap = new HashMap<>();
        
        // Populate movieIdMap for existing movies
        for (Movie movie : movieCategories.keySet()) {
            movieIdMap.put(movie.getID(), movie);
        }
    }
    
    /**
     * Loads movies from a text file.
     * Format: "Movie Title, MovieID" followed by categories on next line
     */
    public void loadMoviesFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            Movie currentMovie = null;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                if (line.contains(",")) {
                    // This is a movie title and ID line
                    String[] parts = line.split(",", 2);
                    String title = parts[0].trim();
                    String id = parts[1].trim();
                    
                    currentMovie = new Movie(title, id);
                    movieIdMap.put(id, currentMovie);
                    movieCategories.put(currentMovie, new ArrayList<>());
                } else if (currentMovie != null) {
                    // This is a categories line
                    String[] categories = line.split(",");
                    for (String category : categories) {
                        category = category.trim();
                        
                        // Add category to movie
                        movieCategories.get(currentMovie).add(category);
                        
                        // Add movie to category
                        if (!categoryMovies.containsKey(category)) {
                            categoryMovies.put(category, new ArrayList<>());
                        }
                        categoryMovies.get(category).add(currentMovie);
                    }
                }
            }
        }
    }
    
    /**
     * Loads user data from a text file.
     * Format: "Username, UserID" followed by watched movie IDs on next line
     */
    public void loadUsersFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            User currentUser = null;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                if (line.contains(",")) {
                    // This is a user name and ID line
                    String[] parts = line.split(",", 2);
                    String name = parts[0].trim();
                    String id = parts[1].trim();
                    
                    currentUser = new User(name, id);  // add in user class
                } else if (currentUser != null) {
                    // This is a watched movies line
                    String[] movieIds = line.split(",");
                    for (String movieId : movieIds) {
                        movieId = movieId.trim();
                        Movie movie = movieIdMap.get(movieId);
                        if (movie != null) {
                            currentUser.addToWatchList(movie); // add in user class
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Writes recommendations for a user to a file
     */
    public void writeRecommendationsToFile(User user, String outputPath) throws IOException {
        List<Movie> recommendations = recommendMoviesForUser(user);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, true))) {
            writer.write(user.getName() + ", " + user.getId());
            writer.newLine();
            
            if (!recommendations.isEmpty()) {
                StringJoiner joiner = new StringJoiner(", ");
                for (Movie movie : recommendations) {
                    joiner.add(movie.getID());
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
     * Gets a movie by its ID
     */
    public Movie getMovieById(String id) {
        return movieIdMap.get(id);
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
        List<String> userCategories = new ArrayList<>();
        for (Movie movie : user.getWatchList()) {
            List<String> categories = movieCategories.get(movie);
            if (categories != null) {
                for (String category : categories) {
                    if (!userCategories.contains(category)) {
                        userCategories.add(category);
                    }
                }
            }
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
    
    //Gets movies from a specific category.
    public List<Movie> getMoviesByCategory(String category) {
        List<Movie> result = new ArrayList<>();
        List<Movie> movies = categoryMovies.get(category);
        
        if (movies == null) {
            return result;
        }
        
        // Return all movies in the category
        return new ArrayList<>(movies);
    }
}