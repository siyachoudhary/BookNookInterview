package com.example.catalog;

/** A book in the catalog, tracking owned copies and how many are on the shelf. */
public class Book {
    private final int id;
    private final String title;
    private final String author;
    private final int copiesTotal;
    private int copiesAvailable;

    public Book(int id, String title, String author, int copies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.copiesTotal = copies;
        this.copiesAvailable = copies;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getCopiesTotal() {
        return copiesTotal;
    }

    public int getCopiesAvailable() {
        return copiesAvailable;
    }

    public void setCopiesAvailable(int copiesAvailable) {
        this.copiesAvailable = copiesAvailable;
    }

    @Override
    public String toString() {
        return String.format("Book(#%d %s by %s, %d/%d available)",
                id, title, author, copiesAvailable, copiesTotal);
    }
}
