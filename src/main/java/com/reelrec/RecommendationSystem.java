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
        this.userCategories = new ArrayList<>();

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
        Validator validator = new Validator(); // Use a Validator instance
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
                    reader.readLine(); // consume the genre line
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
                            System.out.println(
                                    "Skipping genre for movie " + id + ": " + genreError + " at line " + lineNumber);
                            continue; // Skip this genre if it's invalid.
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
    public void loadUsersFromFile(String filePath, ArrayList<Integer> invalidLines, Set<String> validMovieIds)
            throws IOException {
        // Do not create a new Validator here – assume validation was already done in
        // Main.
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String userInfoLine;
            int lineNumber = 0;

            while ((userInfoLine = reader.readLine()) != null) {
                lineNumber++;
                userInfoLine = userInfoLine.trim();
                // Skip empty lines or lines flagged as invalid by the validator
                if (userInfoLine.isEmpty() || invalidLines.contains(lineNumber))
                    continue;

                String[] parts = userInfoLine.split(",", 2);
                User currentUser;
                if (parts.length != 2) {
                    // Create a dummy user with an error message for a formatting error
                    currentUser = new User("Unknown", "Unknown");
                    currentUser.setErrorMessage("ERROR: User Formatting is wrong at line " + lineNumber);
                    users.add(currentUser);
                    // Consume the next line, which should be the watch list, and increment line
                    // number.
                    reader.readLine();
                    lineNumber++;
                    continue;
                }
                String name = parts[0].trim();
                String id = parts[1].trim();
                currentUser = new User(name, id);

                // Read the next line for the watch list
                String moviesLine = reader.readLine();
                lineNumber++;
                if (moviesLine == null || moviesLine.trim().isEmpty() || invalidLines.contains(lineNumber)) {
                    currentUser.setErrorMessage("ERROR: User has no movies at line " + lineNumber);
                } else {
                    String[] movieIds = moviesLine.split(",");
                    boolean hasValidMovies = false;
                    boolean hasInvalidMovies = false;
                    String lastInvalidMovieId = "";
                    int lastInvalidLineNumber = 0;

                    for (String movieId : movieIds) {
                        movieId = movieId.trim();
                        if (!validMovieIds.contains(movieId)) {
                            hasInvalidMovies = true;
                            lastInvalidMovieId = movieId;
                            lastInvalidLineNumber = lineNumber;
                            // Don't break here, continue processing other movie IDs
                        } else {
                            currentUser.addToWatchList(movieIdMap.get(movieId));
                            hasValidMovies = true;
                        }
                    }

                    // Set error message only if there were invalid movies and record the last one
                    // found
                    if (hasInvalidMovies) {
                        currentUser.setErrorMessage("ERROR: Movie Id \"" + lastInvalidMovieId + "\" at line "
                                + lastInvalidLineNumber + " is not in the movies file");
                    }

                    // If no valid movies were found, clear the watchlist to ensure no
                    // recommendations
                    if (!hasValidMovies && hasInvalidMovies) {
                        currentUser.clearWatchList();
                    }
                }
                // Always add the user record—even if it has an error message.
                users.add(currentUser);
            }
        }
    }

    /**
     * Writes recommendations for a user to a file.
     * Format: Username, UserID followed by recommended movie titles on the next
     * line
     */
    public void writeRecommendationsToFile(User user, String outputPath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath, true))) {
            // Add a decorative header for each user
            writer.write("╔══════════════════════════════════════════════════════════════╗");
            writer.newLine();
            writer.write("║                      REELREC RECOMMENDATIONS                 ║");
            writer.newLine();
            writer.write("╚══════════════════════════════════════════════════════════════╝");
            writer.newLine();
            writer.newLine();

            // Write user info with better formatting
            writer.write("📋 USER PROFILE");
            writer.newLine();
            writer.write("   Name: " + user.getName());
            writer.newLine();
            writer.write("   ID: " + user.getId());
            writer.newLine();
            writer.newLine();

            // If the user has an error message, output that with clear formatting
            if (user.getErrorMessage() != null) {
                writer.write("⚠️ VALIDATION ERROR");
                writer.newLine();
                writer.write("   " + user.getErrorMessage());
                writer.newLine();
                writer.newLine();
            }

            // Generate recommendations with better formatting
            List<Movie> recommendations = recommendMoviesForUser(user);
            writer.write("🎬 MOVIE RECOMMENDATIONS");
            writer.newLine();

            if (!recommendations.isEmpty()) {
                int count = 1;
                for (Movie movie : recommendations) {
                    writer.write("   " + count + ". " + movie.getName());
                    writer.newLine();
                    count++;
                }
            } else {
                writer.write("   No recommendations available for this user.");
                writer.newLine();
            }

            // Add watch list summary if available
            List<Movie> watchList = user.getWatchList();
            if (watchList != null && !watchList.isEmpty()) {
                writer.newLine();
                writer.write("🍿 MOVIES YOU LIKE");
                writer.newLine();
                int count = 1;
                for (Movie movie : watchList) {
                    writer.write("   " + count + ". " + movie.getName());
                    writer.newLine();
                    count++;
                }
            }

            // Add a decorative footer
            writer.newLine();
            writer.write("────────────────────────────────────────────────────────────────");
            writer.newLine();
            writer.newLine();
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
            // Skip null movies or movies not in movieCategories map
            if (movie == null || !movieCategories.containsKey(movie)) {
                continue;
            }

            List<String> genresForMovie = movieCategories.get(movie);
            if (genresForMovie != null) {
                for (String category : genresForMovie) {
                    if (!userCategories.contains(category)) {
                        userCategories.add(category);
                    }
                }
            }
        }

        // Return empty list if no valid categories found
        if (userCategories.isEmpty()) {
            return recommendations;
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