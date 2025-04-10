package com.reelrec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Movie implements Comparable<Movie> {
    private String id;
    private String name;
    private String[] genres;

    // Default resource paths
    private static final String DEFAULT_MOVIES_PATH = "movies.txt";
    private static final String DEFAULT_USERS_PATH = "users.txt";
    
    // Actual paths to be used - can be overridden
    private String moviesPath = DEFAULT_MOVIES_PATH;
    private String usersPath = DEFAULT_USERS_PATH;
    
    public Movie() {
        this.id = null;
        this.name = null;
        this.genres = null;
    }

    public Movie(String id) {
        this.id = id;
        this.name = null;
        this.genres = null;
    }

    public Movie(String id, String name) {
        this.id = id;
        this.name = name;
        this.genres = null;
    }

    public Movie(String id, String name, String[] genre) {
        this.id = id;
        this.name = name;
        this.genres = genre;
    }

    // Method to set custom resource paths
    public void setResourcePaths(String moviesPath, String usersPath) {
        if (moviesPath != null) this.moviesPath = moviesPath;
        if (usersPath != null) this.usersPath = usersPath;
    }

    // Getters and Setters
    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getGenre() {
        return genres;
    }

    public void setID(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGenre(String[] genre) {
        this.genres = genre;
    }

    // Creates mapping between users and their movies
    public HashMap<String, HashMap<String, Movie>> makeHashMap() {
        HashMap<String, Movie> moviesMap = new HashMap<>();
        HashMap<String, HashMap<String, Movie>> userMoviesMap = new HashMap<>();
        
        try {
            // First, load all movies from resources
            InputStream moviesStream = getClass().getClassLoader().getResourceAsStream(moviesPath);
            if (moviesStream == null) {
                System.err.println("Could not find resource: " + moviesPath);
                return userMoviesMap;
            }
            
            BufferedReader moviesReader = new BufferedReader(new InputStreamReader(moviesStream));
            String line;
            
            while ((line = moviesReader.readLine()) != null) {
                if (line.contains(",")) {  // This is a movie name and ID line
                    String[] movieInfo = line.split(", ");
                    String movieName = movieInfo[0];
                    String movieId = movieInfo[1];
                    
                    // Read the next line which contains genres
                    line = moviesReader.readLine();
                    if (line != null) {
                        String[] genres = line.split(", ");
                        Movie movie = new Movie(movieId, movieName, genres);
                        moviesMap.put(movieId, movie);
                    }
                }
            }
            moviesReader.close();
            
            // Now process users file
            InputStream usersStream = getClass().getClassLoader().getResourceAsStream(usersPath);
            if (usersStream == null) {
                System.err.println("Could not find resource: " + usersPath);
                return userMoviesMap;
            }
            
            BufferedReader usersReader = new BufferedReader(new InputStreamReader(usersStream));
            
            while ((line = usersReader.readLine()) != null) {
                if (line.contains(",")) {  // This is a user name and ID line
                    String[] userInfo = line.split(", ");
                    String userName = userInfo[0];
                    String userId = userInfo[1];
                    
                    // Read the next line which contains movie IDs
                    line = usersReader.readLine();
                    if (line != null) {
                        HashMap<String, Movie> userMovies = new HashMap<>();
                        String[] movieIds = line.split(", ");
                        
                        for (String id : movieIds) {
                            if (moviesMap.containsKey(id)) {
                                userMovies.put(id, moviesMap.get(id));
                            }
                        }
                        
                        userMoviesMap.put(userId, userMovies);
                    }
                }
            }
            usersReader.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return userMoviesMap;
    }
        
    @Override
    public int compareTo(Movie other) {
        return this.id.compareTo(other.id);  // Compare based on ID
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Movie movie = (Movie) obj;
        return id.equals(movie.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    // Test the Movie class
    public static void main(String[] args) {
        Movie movie = new Movie("M1", "Inception", new String[]{"Action", "Sci-Fi"});
        System.out.println("Movie ID: " + movie.getID());
        System.out.println("Movie Name: " + movie.getName());
        System.out.println("Genres: " + String.join(", ", movie.getGenre()));
        
        // Test the makeHashMap method
        HashMap<String, HashMap<String, Movie>> userMoviesMap = movie.makeHashMap();
        System.out.println("User Movies Map: " + userMoviesMap);
        
        // Print out more details
        for (String userId : userMoviesMap.keySet()) {
            System.out.println("User ID: " + userId);
            HashMap<String, Movie> movies = userMoviesMap.get(userId);
            for (String movieId : movies.keySet()) {
                Movie m = movies.get(movieId);
                System.out.println("  Movie: " + m.getName() + " (" + m.getID() + ")");
                System.out.println("  Genres: " + String.join(", ", m.getGenre()));
            }
        }
    }
}