package com.reelrec;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class UserTest {

    @Test
    public void testUserCreation() {
        User user = new User("John Doe","M1", new ArrayList<>());
        assert user.getName().equals("John Doe");
        assert user.getId().equals("M1"); // Assuming this is the first user created
    }
    @Test
    public void testAddMovie() {
        User user = new User("Jane Doe","123456789X", new ArrayList<>());
        Movie movie = new Movie("M1", "Movie 1");
        
        user.addToWatchList(movie);
        user.addToWatchList(new Movie("M2", "Movie 2"));
        assert user.getWatchList().size() == 2;
        assert user.getWatchList().get(0).getID().equals("M1");
        assert user.getWatchList().get(1).getID().equals("M2");
    }
}
