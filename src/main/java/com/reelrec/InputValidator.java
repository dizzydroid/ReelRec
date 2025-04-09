package com.reelrec;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InputValidator {

    private Set<String> existingMovieIdNumbers = new HashSet<>();
    private Set<String> existingUserIds = new HashSet<>();

    // Validates that each word in the movie title starts with a capital letter
    private String checkMovieTitle(String title) {
        String[] words = title.trim().split("\\s+");
        for (String word : words) {
            if (word.isEmpty() || !Character.isUpperCase(word.charAt(0))) {
                return String.format("ERROR: Movie Title \"%s\" is wrong", title);
            }
        }
        return "";
    }

    // Validates movie ID format and uniqueness based on title
    private String checkMovieId(String movieId, String title) {
        String capitalLetters = new String();
        for (char letter : title.toCharArray()) {
            if (Character.isUpperCase(letter)) {
                capitalLetters+= letter;
            }
        }
        String IdPattern = capitalLetters + "\\d{3}";
        String numberPart = movieId.replaceAll("[^0-9]", "");  // Remove all non-numeric characters
        if (!movieId.matches(IdPattern))
            return String.format("ERROR: Movie Id letters \"%s\" are wrong", movieId);
        if (this.existingMovieIdNumbers.contains(numberPart))
            return String.format("ERROR: Movie Id numbers \"%s\" aren't unique", movieId);
        this.existingMovieIdNumbers.add(movieId);
        return "";
    }

    // Validates user name: only alphabet and space, no starting space
    private String checkUserName(String name) {
        if(!name.matches("[A-Za-z]+(\\s+[A-Za-z]+)*"))
            return String.format("ERROR: User Name \"%s\" is wrong", name);
        return "";
    }

    // Validates user ID: 9 alphanumeric characters, starts with digits, may end with one letter
    private String checkUserId(String userId) {
        if (userId.matches("^\\d{8}[A-Za-z0-9]$")){
            if (!this.existingUserIds.contains(userId)){
                this.existingUserIds.add(userId);
                return "";
            }
        }
        return String.format("ERROR: User Id \"%s\" is wrong",userId);
    }

    public String parseAndValidateMovies(String filepath){
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            //int lineNumber = 1; 
            String line;
            String movetitle, movieId, result;
            // Loop until we have read all lines
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                movetitle = parts[0].trim();
                movieId = parts[1].trim();
                result = checkMovieTitle(movetitle);
                if(!result.equals("")){
                    return result;
                }
                result = checkMovieId(movieId, movetitle);
                if(!result.equals("")){
                    return result;
                }
                reader.readLine();  // Read the next line (ignore genres)
                //lineNumber+=2;
            }
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR: File not found or cannot be read";
        }
    }

    public String parseAndValidateUsers(String filepath){
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            //int lineNumber = 1; 
            String line;
            String userName, userId, result;
            // Loop until we have read all lines
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                userName = parts[0].trim();
                userId = parts[1].trim();
                result = checkUserName(userName);
                if(!result.equals("")){
                    return result;
                }
                result = checkUserId(userId);
                if(!result.equals("")){
                    return result;
                }
                reader.readLine();  // Read the next line (ignore movies)
                //lineNumber+=2;
            }
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "ERROR: File not found or cannot be read";
        }
    }
  
}