package com.reelrec;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InputValidator {
    Set<String> existingMovieIds = new HashSet<>();
    Set<String> existingMovieIdNumbers = new HashSet<>();
    Set<String> existingUserIds = new HashSet<>();
    List<String> validGenres = Arrays.asList("ACTION", "COMEDY", "CRIME", "DOCUMENTARY", "DRAMA",
                                                     "FAMILY", "HORROR", "ROMANCE", "SCIFI", "THRILLER");
    
    // Validates movie title (each word in the movie title starts with a capital letter or a digit)
    String checkMovieTitle(String title) {
        String[] words = title.trim().split("\\s");
        for (String word : words) {
            if (word.isEmpty() || !Character.isUpperCase(word.charAt(0))) {
                return String.format("ERROR: Movie Title \"%s\" is wrong", title);
            }
        }
        return "";
    }

    // Validates movie ID (starts with capital letters of the title, followed by 3 unique digits)
    // Note: this assumes valid movie title is already checked
    String checkMovieId(String movieId, String title) {
        if(!movieId.matches("^[A-Z]+\\d{3}$")){
            return String.format("ERROR: Movie Id format \"%s\" is wrong", movieId);
        }
        String capitalLetters = new String();
        for (char letter : title.toCharArray()) {
            if (Character.isUpperCase(letter)) {
                capitalLetters+= letter;
            }
        }
        String IdPattern = capitalLetters + "\\d{3}";
        String numberPart = movieId.replaceAll("[^0-9]", "");
        if (!movieId.matches(IdPattern))
            return String.format("ERROR: Movie Id letters \"%s\" are wrong", movieId);
        if (this.existingMovieIdNumbers.contains(numberPart))
            return String.format("ERROR: Movie Id numbers \"%s\" aren't unique", movieId);
        return "";
    }

    // Validates movie genre (only valid genres are allowed)
    String checkMovieGenre(String genre){
        if(!validGenres.contains(genre.toUpperCase())){
            return String.format("ERROR: Movie genre \"%s\" is not supported", genre);
        }
        return "";
    }

    // Validates user name (only alphabet and space, no starting space)
    String checkUserName(String name) {
        if(!name.matches("[A-Za-z]+(\\s[A-Za-z]+)*"))
            return String.format("ERROR: User Name \"%s\" is wrong", name);
        return "";
    }

    // Validates user ID (9 alphanumeric characters, starts with digits, may end with one letter)
    String checkUserId(String userId) {
        if (userId.matches("^\\d{8}[A-Za-z0-9]$")){
            if (!this.existingUserIds.contains(userId)){
                return "";
            }
            else{
                return String.format("ERROR: User Id \"%s\" is not unique", userId);
            }
        }
        return String.format("ERROR: User Id \"%s\" is wrong",userId);
    }

    // Parses and validates movies from a file, returns a list of errors (if empty then no errors)
    public HashMap<Movie,List<String>> parseAndValidateMovies(String filepath){
        HashMap<Movie,List<String>> movies = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            int lineNumber = 1; 
            String line;
            String movieTitle, movieId, result;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                Movie movie = new Movie(null, null);
                List<String> errors = new ArrayList<>();
                if(parts.length == 2){
                    movieTitle = parts[0].trim();
                    movieId = parts[1].trim();
                    movie.setName(movieTitle);
                    movie.setID(movieId);
                    result = checkMovieTitle(movieTitle);
                    if (!result.equals("")) {
                        errors.add(result + " at line " + lineNumber);
                    }
                    else{
                        result = checkMovieId(movieId, movieTitle);
                        if (!result.equals("")) {
                            errors.add(result + " at line " + lineNumber);
                        }
                    }
                }
                else{
                    movie = null;
                    errors.add("ERROR: Movie Formatting is wrong at line " + lineNumber);
                }
                lineNumber++;
                line = reader.readLine();
                if(!line.isEmpty()){
                    parts = line.split(",");
                    List<String> genres = new ArrayList<>();
                    for(String genre : parts){
                        genre = genre.trim();
                        result = checkMovieGenre(genre);
                        if (!result.equals("")) {
                            errors.add(result + " at line " + lineNumber);
                        }
                        else{   
                            genres.add(genre.toUpperCase());
                        }
                    }
                    movie.setGenre(genres.toArray(new String[0]));
                }
                else{
                    errors.add("ERROR: Movie has no genres at line " + lineNumber);
                }
                String numberPart = movie.getID().replaceAll("[^0-9]", "");
                this.existingMovieIds.add(movie.getID());
                this.existingMovieIdNumbers.add(numberPart);
                movies.put(movie, errors);
                lineNumber++;
            }

            return movies;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Parses and validates users from a file, returns a list of errors (if empty then no errors)
    // Note: before calling this call parseAndValidateMovies to populate existingMovieIds
    public HashMap<User,List<String>> parseAndValidateUsers(String filepath){
        HashMap<User,List<String>> users = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            int lineNumber = 1; 
            String line;
            String userName, userId, result;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                User user = new User(null, null);
                List<String> errors = new ArrayList<>();
                if(parts.length == 2){
                    userName = parts[0].trim();
                    userId = parts[1].trim();
                    user.setName(userName);
                    user.setId(userId);
                    result = checkUserName(userName);
                    if (!result.equals("")) {
                        errors.add(result + " at line " + lineNumber);
                    }
                    else{
                        result = checkUserId(userId);
                        if (!result.equals("")) {
                            errors.add(result + " at line " + lineNumber);
                        }
                    }
                }
                else{
                    user = null;
                    errors.add("ERROR: User Formatting is wrong at line " + lineNumber);
                }
                lineNumber++;
                line = reader.readLine();
                // if(!line.isEmpty()){
                //     parts = line.split(",");
                //     List<String> movieIds = new ArrayList<>();
                //     for(String movieId : parts){
                //         movieId = movieId.trim();
                //         if(!existingMovieIds.contains(movieId)){
                //             errors.add("ERROR: Movie Id \"" + movieId + "\" at line " + lineNumber + " is not in the movie list");
                //         }
                //         else{
                //             user.addToWatchList(movieId);
                //         }
                //     }
                //     movie.setGenre(genres.toArray(new String[0]));
                // }
                // else{
                //     errors.add("ERROR: Movie has no genres at line " + lineNumber);
                // }

                this.existingUserIds.add(user.getId());
                users.put(user, errors);
                lineNumber++;
            }

            return users;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
  
}