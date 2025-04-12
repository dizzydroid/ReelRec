package com.reelrec;

import java.util.List;

public class User {

    private String name,id;
    private List<Movie> WatchList;
    private String errorMessage = null;  // New field for validation errors

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

    public void clearWatchList() {
        WatchList.clear();
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
        
    public String getErrorMessage() {
        return errorMessage;
    }

}
