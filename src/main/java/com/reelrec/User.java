package com.reelrec;

import java.util.List;

import java.util.Scanner;

public class User {

    private String name,id;
    private List<Movie> WatchList= new java.util.ArrayList<Movie>();
    Scanner in = new Scanner(System.in);

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<Movie> getWatchList() {
        return WatchList;
    }

    public User() {
        System.out.println("Enter your name");
        this.name =in.nextLine();

    }
    public User(String name,String id) {
        this.name = name;
        this.id = id;
    }
    public User(String name,String id,List<Movie> WatchList) {
  this(name,id);
          this.WatchList = WatchList;
    }

    public User(String name, List<Movie> WatchList) {
        this.name = name;
        this.WatchList = WatchList;

    }

    public void addToWatchList(Movie movie) {
        WatchList.add(movie);

    }


}
