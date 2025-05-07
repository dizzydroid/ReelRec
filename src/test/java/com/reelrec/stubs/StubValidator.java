package com.reelrec.stubs;

import com.reelrec.Validator;
import java.util.*;

public class StubValidator extends Validator {
    public List<String> movieErrorsToReturn = new ArrayList<>();
    public List<String> userErrorsToReturn = new ArrayList<>();
    public Set<String> validMovieIdsToReturn = new HashSet<>();
    public ArrayList<Integer> invalidLinesToReturn = new ArrayList<>();
    public boolean throwMovieReadError = false;
    public boolean throwUserReadError = false;

    @Override
    public List<String> parseAndValidateMovies(String filepath) {
        System.out.println("STUB VALIDATOR: parseAndValidateMovies called for: " + filepath);
        if (throwMovieReadError) {
            return List.of("ERROR: Cannot read file " + filepath); 
        }
        return new ArrayList<>(movieErrorsToReturn);
    }

    @Override
    public List<String> parseAndValidateUsers(String filepath) {
        System.out.println("STUB VALIDATOR: parseAndValidateUsers called for: " + filepath);
        if (throwUserReadError) {
            return List.of("ERROR: Cannot read file " + filepath); 
        }
        return new ArrayList<>(userErrorsToReturn);
    }

    @Override
    public Set<String> getValidMovieIds() {
        System.out.println("STUB VALIDATOR: getValidMovieIds called, returning: " + validMovieIdsToReturn);
        return new HashSet<>(validMovieIdsToReturn);
    }

    @Override
    public ArrayList<Integer> extractErrorLines(List<String> errors) {
         System.out.println("STUB VALIDATOR: extractErrorLines called with errors: " + errors);
        return new ArrayList<>(invalidLinesToReturn);
    }
}