package com.reelrec;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Main {
    private static TreeMap<Movie, List<String>> EachMovieCategories= new TreeMap<>();
    private static TreeMap<String, List<Movie>> EachCategoryMovies=new TreeMap<>();

    public static void main(String[] args) throws IOException {
        List<User> Existingusers = new ArrayList<>();
 /*
  adding the initial users, it's names and their movies
  */
        User user1 = new User("John Doe", new ArrayList<>());
        Movie movie1 = new Movie();
        movie1.setID("M1");
        Movie movie2 = new Movie();
        movie2.setID("M2");
        FileWriter writer = null;
        user1.addToWatchList(movie1);
        user1.addToWatchList(movie2);
        Existingusers.add(user1);
        EachCategoryMovies.put("Action", new ArrayList<>());
        EachCategoryMovies.put("Drama", new ArrayList<>());
        EachCategoryMovies.put("Comedy", new ArrayList<>());
        EachMovieCategories.put(movie1, List.of("Action", "Drama"));
        EachMovieCategories.put(movie2, List.of("Comedy", "Drama"));
        try {
            writer = new FileWriter("src/main/resources/users.txt");

        }
        catch (IOException e) {
            System.out.println("An error occurred in Opening users.txt.");
            e.printStackTrace();
        }


        for (User user : Existingusers)
        {
            List<Movie> userMovies = user.getWatchList();
            for (Movie movie : userMovies) {
                //  List<String> categories = movie.getCategories();
                List<String> categories = EachMovieCategories.get(movie);// this line is prototype to remove errors
                if (!EachMovieCategories.containsKey(movie)) {
                    EachMovieCategories.put(movie, categories);
                }
                if (categories != null) {
                    for (String category : categories) {
                        if (!EachCategoryMovies.containsKey(category)) {
                            EachCategoryMovies.put(category, new ArrayList<>());
                        }
                        if (!EachCategoryMovies.get(category).contains(movie))
                            EachCategoryMovies.get(category).add(movie);
                    }
                }
            }

            String userInfo = user.getName() + " , " + user.getId() + "\n";
            for (Movie movie : userMovies) {
                userInfo += movie.getID() + ",";
            }
            userInfo += "\n";

            try {
                writer.append(userInfo);
            }
            catch (IOException e) {
                System.out.println("An error occurred in Appending in users.txt.");
                e.printStackTrace();
            }
        }


        writer.close();
    }


    public static TreeMap<Movie, List<String>> getEachMovieCategories() {
        return EachMovieCategories;
    }

    public TreeMap<String, List<Movie>> getEachCategoryMovies() {
        return EachCategoryMovies;
    }
}