package com.reelrec;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {
    Set<String> existingMovieIds = new HashSet<>();
    Set<String> existingMovieIdNumbers = new HashSet<>();
    Set<String> existingUserIds = new HashSet<>();
    List<String> validGenres = Arrays.asList("ACTION", "COMEDY", "CRIME", "DOCUMENTARY", "DRAMA",
            "FAMILY", "HORROR", "ROMANCE", "SCIFI", "THRILLER");

    // Validates movie title (each word in the movie title starts with a capital
    // letter or a digit)

    String checkMovieTitle(String title) {
        if (title.trim().matches("^\\d+$")) {
            return String.format("ERROR: Movie Title \"%s\" is wrong", title);
        }
        String[] words = title.trim().split("\\s");
        for (String word : words) {
            if (word.isEmpty()) {
                return String.format("ERROR: Movie Title \"%s\" is wrong", title);
            }
            char firstChar = word.charAt(0);
            if (!Character.isUpperCase(firstChar) && !Character.isDigit(firstChar)) {
                return String.format("ERROR: Movie Title \"%s\" is wrong", title);
            }
        }
        return "";
    }

    // Validates movie ID (starts with capital letters of the title, followed by 3
    // unique digits)
    // Note: this assumes valid movie title is already checked
    String checkMovieId(String movieId, String title) {
        if (!movieId.matches("^[A-Z]+\\d{3}$")) {
            return String.format("ERROR: Movie Id format \"%s\" is wrong", movieId);
        }
        String capitalLetters = new String();
        for (char letter : title.toCharArray()) {
            if (Character.isUpperCase(letter)) {
                capitalLetters += letter;
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
    String checkMovieGenre(String genre) {
        if (!validGenres.contains(genre.toUpperCase())) {
            return String.format("ERROR: Movie genre \"%s\" is not supported", genre);
        }
        return "";
    }

    // Validates user name (only alphabet and space, no starting space)
    String checkUserName(String name) {
        if (!name.matches("[A-Za-z]+(\\s[A-Za-z]+)*"))
            return String.format("ERROR: User Name \"%s\" is wrong", name);
        return "";
    }

    // Validates user ID (9 characters, first 8 must be digits, last can be digit or letter)
    String checkUserId(String userId) {
        if (userId.matches("^\\d{8}[A-Za-z0-9]$")) {
            if (!this.existingUserIds.contains(userId)) {
                return "";
            } else {
                return String.format("ERROR: User Id \"%s\" is not unique", userId);
            }
        }
        return String.format("ERROR: User Id \"%s\" is wrong", userId);
    }

    // Parses and validates movies from a file, returns a list of errors (if empty
    // then no errors)
    // public boolean isFileRead(String filepath){
    //     try {
    //         BufferedReader reader = new BufferedReader(new FileReader(filepath));
    //         return true;
    //     } catch(IOException e) {
    //         e.printStackTrace();
    //         return false;
    //     }
    // }

    public List<String> parseAndValidateMovies(String filepath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            int lineNumber = 0;
            String line;
            String movetitle, movieId = null, result;
            boolean validId;
            List<String> errors = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                validId = false;
                lineNumber++;
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    movetitle = parts[0].trim();
                    movieId = parts[1].trim();
                    result = checkMovieTitle(movetitle);
                    if (!result.equals("")) {
                        errors.add(result + " at line " + lineNumber + " in the movies file.");
                    } else {
                        result = checkMovieId(movieId, movetitle);
                        if (!result.equals("")) {
                            errors.add(result + " at line " + lineNumber + " in the movies file.");
                        } else {
                            validId = true;
                        }
                    }
                } else {
                    errors.add("ERROR: Movie Formatting is wrong at line " + lineNumber + " in the movies file.");
                }
                line = reader.readLine();
                lineNumber++;
                if (!line.isEmpty()) {
                    parts = line.split(",");
                    for (String genre : parts) {
                        genre = genre.trim();
                        result = checkMovieGenre(genre);
                        if (!result.equals("")) {
                            errors.add(result + " at line " + lineNumber + " in the movies file.");
                        }
                    }
                } else {
                    errors.add("ERROR: Movie has no genres at line " + lineNumber + " in the movies file.");
                }
                if(validId){
                    String numberPart = movieId.replaceAll("[^0-9]", "");
                    this.existingMovieIds.add(movieId);
                    this.existingMovieIdNumbers.add(numberPart);
                }
            }

            return errors;

        } catch (IOException e) {
            e.printStackTrace();
            List<String> errors = new ArrayList<>();
            errors.add("ERROR: File not found or cannot be read");
            return errors;
        }
    }

    // Parses and validates users from a file, returns a list of errors (if empty
    // then no errors)
    // Note: before calling this call parseAndValidateMovies to populate
    // existingMovieIds
    public List<String> parseAndValidateUsers(String filepath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            int lineNumber = 0;
            String line;
            String username, userId = null, result;
            List<String> errors = new ArrayList<>();
            boolean validId;
            while ((line = reader.readLine()) != null) {
                validId = false;
                lineNumber++;
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    username = parts[0].trim();
                    userId = parts[1].trim();
                    result = checkUserName(username);
                    if (!result.equals("")) {
                        errors.add(result + " at line " + lineNumber + " in the users file.");
                    } else {
                        result = checkUserId(userId);
                        if (!result.equals("")) {
                            errors.add(result + " at line " + lineNumber + " in the users file.");
                        } else {
                            validId = true;
                        }
                    }
                } else {
                    errors.add("ERROR: User Formatting is wrong at line " + lineNumber + " in the users file.");
                }
                line = reader.readLine();
                lineNumber++;
                if (!line.isEmpty()) {
                    parts = line.split(",");
                    for (String movieId : parts) {
                        movieId = movieId.trim();
                        if (!this.existingMovieIds.contains(movieId)) {
                            errors.add("ERROR: Movie Id \"" + movieId + "\" at line " + lineNumber
                                    + " is not in the movies file");
                        }
                    }
                } else {
                    errors.add("ERROR: User has no movies at line " + lineNumber + " in the users file.");
                }
                if(validId){
                    this.existingUserIds.add(userId);
                }
                
            }

            return errors;

        } catch (IOException e) {
            e.printStackTrace();
            List<String> errors = new ArrayList<>();
            errors.add("ERROR: File not found or cannot be read");
            return errors;
        }
    }

    public ArrayList<Integer> extractErrorLines(List<String> errors) {
        Set<Integer> errorLines = new HashSet<>();
        for (String error : errors) {
            // Regex to find "at line {number}"
            Matcher matcher = Pattern.compile("at line (\\d+)").matcher(error);
            if (matcher.find()) {
                int lineNumber = Integer.parseInt(matcher.group(1));
                errorLines.add(lineNumber);
                if (lineNumber % 2 == 1) {
                    errorLines.add(lineNumber + 1); // Add the next line number
                }
            }
        }
        return new ArrayList<>(errorLines);
    }

    public Set<String> getValidMovieIds() {
        return existingMovieIds;
    }
}