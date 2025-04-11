package com.reelrec;

import java.util.List;

import java.util.Scanner;

public class User {

    private String name,id;
    private List<Movie> WatchList;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Movie> getWatchList() {
        return WatchList;
    }

    private User() {
    }
    public User(String name,String id) {
        this.name = name;
        this.id = id;
        this.WatchList = new java.util.ArrayList<Movie>();

    }
    public User(String name,String id,List<Movie> WatchList) {
  this(name,id);
          this.WatchList = WatchList;
    }

    public void addToWatchList(Movie movie) {
        WatchList.add(movie);

    }


}
