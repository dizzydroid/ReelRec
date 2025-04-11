package com.reelrec;

import java.util.Arrays;

public class Movie implements Comparable<Movie> {
    private String id;
    private String name;
    private String[] genres;

    public Movie(String id, String name) {
        if (id == null || name == null) {
            throw new NullPointerException("ID and name cannot be null");
        }
        this.id = id;
        this.name = name;
        this.genres = new String[0];
    }
    
    public Movie(String id, String name, String[] genre) {
        if (id == null || name == null || genre == null) {
            throw new NullPointerException("ID, name, and genre cannot be null");
        }
        this.id = id;
        this.name = name;
        // Create a defensive copy of the genre array
        this.genres = Arrays.copyOf(genre, genre.length);
    }

    // Getters and Setters
    public String getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getGenre() {
        // Return a defensive copy of the array
        return Arrays.copyOf(genres, genres.length);
    }

    public void setID(String id) {
        if (id == null) {
            throw new NullPointerException("ID cannot be null");
        }
        this.id = id;
    }

    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException("Name cannot be null");
        }
        this.name = name;
    }

    public void setGenre(String[] genre) {
        if (genre == null) {
            throw new NullPointerException("Genre cannot be null");
        }
        // Create a defensive copy of the genre array
        this.genres = Arrays.copyOf(genre, genre.length);
    }
        
    @Override
    public int compareTo(Movie other) {
        if (other == null) {
            throw new NullPointerException("Cannot compare with null Movie");
        }
        
        // Split IDs into letter prefix and numeric suffix
        String thisLetters = id.replaceAll("\\d", ""); // Extract letter part
        String otherLetters = other.id.replaceAll("\\d", "");
        
        // Compare prefixes first
        int prefixComparison = thisLetters.compareTo(otherLetters);
        if (prefixComparison != 0) {
            return prefixComparison;
        }
        
        // If prefixes are equal, extract numeric parts and compare numerically
        String thisNumberStr = id.replaceAll("[^\\d]", ""); // Extract numeric part
        String otherNumberStr = other.id.replaceAll("[^\\d]", "");
        
        try {
            int thisNumber = Integer.parseInt(thisNumberStr);
            int otherNumber = Integer.parseInt(otherNumberStr);
            return Integer.compare(thisNumber, otherNumber);
        } catch (NumberFormatException e) {
            // Fall back to string comparison if number parsing fails
            return thisNumberStr.compareTo(otherNumberStr);
        }
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
}