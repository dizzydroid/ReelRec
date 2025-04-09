package com.reelrec;

import java.util.List;

import java.util.Scanner;

public class User {
    private int  id, NumberOfRegisteredUsers = 0;
    private String name;
    private List<Movie> WatchList;
    Scanner in = new Scanner(System.in);

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public List<Movie> getWatchList() {
        return WatchList;
    }

    public User() {
        System.out.println("Enter your name");
        this.name =in.nextLine();
        NumberOfRegisteredUsers++;
        id = NumberOfRegisteredUsers;
    }

    public User(String name, List<Movie> WatchList) {
        this.name = name;
        this.WatchList = WatchList;
        NumberOfRegisteredUsers++;
        id = NumberOfRegisteredUsers;
    }

    public void addMovie(Movie movie) {
        WatchList.add(movie);

    }


}
