package com.reelrec.stubs;

import com.reelrec.RecommendationSystem;
import com.reelrec.User;
import java.io.IOException;
import java.util.*;

public class StubRecommendationSystem extends RecommendationSystem {
    public List<User> usersToReturn = new ArrayList<>();
    public boolean throwLoadMoviesError = false;
    public boolean throwLoadUsersError = false;
    public boolean throwWriteRecsErrorForUser = false;
    public List<String> loadedMoviesFiles = new ArrayList<>();
    public List<String> loadedUsersFiles = new ArrayList<>();
    public List<User> usersForWhichRecsWereWritten = new ArrayList<>();
    public int writeRecommendationsCallCount = 0;

    @Override
    public void loadMoviesFromFile(String filePath, ArrayList<Integer> invalidLines) throws IOException {
        System.out.println("STUB RECSYS: loadMoviesFromFile called for: " + filePath + " (ignoring invalidLines)");
        loadedMoviesFiles.add(filePath);
        if (throwLoadMoviesError) {
            throw new IOException("Stubbed loadMoviesFromFile error");
        }
    }

    @Override
    public void loadUsersFromFile(String filePath, ArrayList<Integer> invalidLines, Set<String> validMovieIds) throws IOException {
        System.out.println("STUB RECSYS: loadUsersFromFile called for: " + filePath + " (ignoring invalidLines, using validMovieIds: " + validMovieIds + ")");
        loadedUsersFiles.add(filePath);
        if (throwLoadUsersError) {
            throw new IOException("Stubbed loadUsersFromFile error");
        }
    }

    @Override
    public List<User> getUsers() {
        System.out.println("STUB RECSYS: getUsers called, returning " + usersToReturn.size() + " users.");
        return usersToReturn;
    }

    @Override
    public void writeRecommendationsToFile(User user, String outputPath) throws IOException {
         System.out.println("STUB RECSYS: writeRecommendationsToFile called for user: " + user.getName() + " to path: " + outputPath);
        writeRecommendationsCallCount++;
        usersForWhichRecsWereWritten.add(user);
        if (throwWriteRecsErrorForUser) { // Simplified error simulation
             throw new IOException("Stubbed writeRecommendationsToFile error for user " + user.getName());
        }
    }
}