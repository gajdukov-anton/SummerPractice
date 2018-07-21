package com.example.user.myapplication.Objects;

public class Book {
    private static final String EMPTY = "";

    public String name = EMPTY;
    public String link = EMPTY;
    public String authors = EMPTY;
    public boolean available = false;
    public String description = EMPTY;
    public String year = EMPTY;

    public Book() {
    }

    public boolean checkForAllFieldsAreFilled() {
        return !(name.equals("") || link.equals("") || authors.equals("") || description.equals("") || year.equals(""));
    }
}
