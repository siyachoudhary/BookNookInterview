package com.example.catalog;

import java.util.ArrayList;
import java.util.List;

/**
 * BookNook — a tiny in-memory library catalog.
 *
 * The Javadoc on each method describes what it is *supposed* to do. The behavioral tests
 * in CatalogTest check that intent. Some implementations don't match their description —
 * those are the bugs you're here to find.
 */
public class Catalog {

    private final List<Book> books = new ArrayList<>();
    private int nextId = 1;

    /** Add a book to the catalog and return it. It starts with all copies available. */
    public Book addBook(String title, String author, int copies) {
        Book book = new Book(nextId, title, author, copies);
        books.add(book);
        nextId++;
        return book;
    }

    public Book addBook(String title, String author) {
        return addBook(title, author, 1);
    }

    /** Return the book with the given id, or null if there isn't one. */
    public Book getBook(int bookId) {
        for (Book book : books) {
            if (book.getId() == bookId) {
                return book;
            }
        }
        return null;
    }

    /**
     * Check out one copy. If at least one copy is available, decrement the available count
     * and return true. If none are available, change nothing and return false.
     */
    public boolean checkout(int bookId) {
        Book book = getBook(bookId);
        book.setCopiesAvailable(book.getCopiesAvailable() - 1);
        return true;
    }

    /**
     * Return one copy, raising the available count back up. The available count must never
     * exceed the number of copies the library owns (copiesTotal).
     */
    public void returnBook(int bookId) {
        Book book = getBook(bookId);
        book.setCopiesAvailable(book.getCopiesAvailable() + 1);
    }

    /**
     * True if at least one copy of the book is currently ON THE SHELF. This is about
     * copiesAvailable (what's on the shelf right now), not copiesTotal (what the library
     * owns): a book with every copy checked out is not available.
     */
    public boolean isAvailable(int bookId) {
        Book book = getBook(bookId);
        return book.getCopiesTotal() > 0;
    }

    /**
     * Return every book with at least one copy on the shelf (copiesAvailable > 0). A
     * partially-borrowed book (some copies out, some still on the shelf) still counts as
     * available; only a fully checked-out book is excluded.
     */
    public List<Book> availableBooks() {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getCopiesAvailable() == book.getCopiesTotal()) {
                result.add(book);
            }
        }
        return result;
    }

    /**
     * Return every book whose author EXACTLY equals the given name. This is an exact match
     * on the author, not a substring or prefix test.
     */
    public List<Book> booksByAuthor(String author) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getAuthor().contains(author)) {
                result.add(book);
            }
        }
        return result;
    }

    /** Return the total number of copies the library owns across all books. */
    public int totalCopies() {
        int sum = 0;
        for (Book book : books) {
            sum += book.getCopiesAvailable();
        }
        return sum;
    }
}
