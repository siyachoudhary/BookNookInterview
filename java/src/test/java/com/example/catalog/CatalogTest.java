package com.example.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * Behavioral tests for BookNook. These describe the *intended* behavior.
 * Fix the source in Catalog.java until they all pass — do not change the tests.
 *
 * There are 6 planted bugs: 4 easy to spot from a single failing test, and 2 subtler ones
 * that only bite on an edge case. Each assertion carries a message describing the intent.
 */
class CatalogTest {

    // -----------------------------------------------------------------------
    // The 4 easier bugs
    // -----------------------------------------------------------------------

    @Test
    void isAvailableTrueWithOneCopy() {
        Catalog cat = new Catalog();
        Book book = cat.addBook("Dune", "Herbert", 1);
        assertTrue(cat.isAvailable(book.getId()),
                "isAvailable should be true whenever at least ONE copy is on the shelf (1 copy = available)");
    }

    @Test
    void availableBooksListsInStock() {
        Catalog cat = new Catalog();
        cat.addBook("Dune", "Herbert", 2);
        cat.addBook("Rare Tome", "Anon", 0);
        List<String> titles = cat.availableBooks().stream()
                .map(Book::getTitle)
                .collect(Collectors.toList());
        assertEquals(List.of("Dune"), titles,
                "availableBooks() should return books WITH copies ('Dune'), not the out-of-stock ones");
    }

    @Test
    void booksByAuthorMatchesAuthor() {
        Catalog cat = new Catalog();
        cat.addBook("Dune", "Herbert");
        cat.addBook("Foundation", "Asimov");
        List<String> titles = cat.booksByAuthor("Herbert").stream()
                .map(Book::getTitle)
                .collect(Collectors.toList());
        assertEquals(List.of("Dune"), titles,
                "booksByAuthor('Herbert') should return Herbert's books ('Dune'), not everyone else's");
    }

    @Test
    void totalCopiesCountsOwned() {
        Catalog cat = new Catalog();
        Book a = cat.addBook("Dune", "Herbert", 3);
        cat.addBook("Foundation", "Asimov", 2);
        cat.checkout(a.getId());  // one copy of Dune is out, but the library still OWNS 3
        assertEquals(5, cat.totalCopies(),
                "totalCopies() should sum copiesTotal (3 + 2 = 5); checking a copy out doesn't reduce it");
    }

    // -----------------------------------------------------------------------
    // The 2 harder bugs (edge cases)
    // -----------------------------------------------------------------------

    @Test
    void checkoutRefusesWhenNoneAvailable() {
        Catalog cat = new Catalog();
        Book book = cat.addBook("Dune", "Herbert", 1);
        assertTrue(cat.checkout(book.getId()), "the first checkout of the only copy should succeed");
        assertFalse(cat.checkout(book.getId()),
                "with 0 copies left, checkout should return false and refuse a copy the library lacks");
        assertEquals(0, cat.getBook(book.getId()).getCopiesAvailable(),
                "a refused checkout must leave copiesAvailable at 0, never negative");
    }

    @Test
    void returnDoesNotExceedOwnedCopies() {
        Catalog cat = new Catalog();
        Book book = cat.addBook("Dune", "Herbert", 1);
        cat.checkout(book.getId());     // available: 1 -> 0
        cat.returnBook(book.getId());   // available: 0 -> 1 (back to full)
        cat.returnBook(book.getId());   // already full: must stay at 1, not climb to 2
        assertEquals(1, cat.getBook(book.getId()).getCopiesAvailable(),
                "copiesAvailable must never exceed copiesTotal (1); an extra return should be a no-op");
    }

    // -----------------------------------------------------------------------
    // Correct behavior (kept as clean reference points)
    // -----------------------------------------------------------------------

    @Test
    void addBookAssignsIncrementingIds() {
        Catalog cat = new Catalog();
        Book a = cat.addBook("Dune", "Herbert");
        Book b = cat.addBook("Foundation", "Asimov");
        assertEquals(1, a.getId(), "the first book added should get id 1");
        assertEquals(2, b.getId(), "the second book added should get id 2");
        assertEquals(a.getCopiesTotal(), a.getCopiesAvailable(),
                "a new book starts with all copies available");
    }

    @Test
    void getBookUnknownId() {
        Catalog cat = new Catalog();
        cat.addBook("Dune", "Herbert");
        assertNull(cat.getBook(999), "getBook should return null for an id that isn't in the catalog");
    }
}
