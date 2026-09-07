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
 * There are 8 planted bugs. None of them announce themselves with a crash or an obviously
 * absurd value — every one is a plausible-looking implementation that quietly disagrees with
 * the Javadoc. The library distinguishes two counts: copiesTotal (how many the library OWNS)
 * and copiesAvailable (how many are ON THE SHELF right now). Several bugs hinge on that
 * distinction. Read the Javadoc, then the code, and find the mismatch. Two waves:
 *
 *   - Wave 1: a careful read of the Javadoc is enough to spot the mismatch.
 *   - Wave 2: the bug only bites on an edge case (a partially-borrowed book, the last copy,
 *     one return too many, an adjacent pair dropped while weeding, or restocked copies that
 *     never reach the shelf).
 *
 * Each assertion carries a message describing the intent.
 */
class CatalogTest {

    // -----------------------------------------------------------------------
    // Wave 1 — read the Javadoc carefully
    // -----------------------------------------------------------------------

    @Test
    void isAvailableReflectsCopiesOnTheShelf() {
        // is_available is about what's ON THE SHELF (copiesAvailable), not what the library
        // OWNS (copiesTotal). A book with every copy checked out is NOT available, even
        // though the library still owns copies.
        Catalog cat = new Catalog();
        Book stocked = cat.addBook("Dune", "Herbert", 2);
        Book out = cat.addBook("Rare Tome", "Anon", 2);
        out.setCopiesAvailable(0);   // both copies checked out; the library still owns 2
        assertTrue(cat.isAvailable(stocked.getId()),
                "isAvailable should be true when at least one copy is on the shelf");
        assertFalse(cat.isAvailable(out.getId()),
                "isAvailable should be false when copiesAvailable is 0, even though the library still "
                        + "OWNS copies (copiesTotal is 2)");
    }

    @Test
    void booksByAuthorMatchesExactly() {
        // books_by_author matches the author EXACTLY, not as a substring. 'Frank Herbertson'
        // merely contains the letters of 'Herbert' and must not be returned for 'Herbert'.
        Catalog cat = new Catalog();
        cat.addBook("Dune", "Herbert");
        cat.addBook("Ringworld", "Larry Niven");
        cat.addBook("A Guide", "Frank Herbertson");   // contains "Herbert" but a different author
        List<String> titles = cat.booksByAuthor("Herbert").stream()
                .map(Book::getTitle)
                .sorted()
                .collect(Collectors.toList());
        assertEquals(List.of("Dune"), titles,
                "booksByAuthor('Herbert') should match the author EXACTLY (just 'Dune'); "
                        + "'Frank Herbertson' only contains the substring and must be excluded");
    }

    @Test
    void totalCopiesCountsOwned() {
        // total_copies counts how many copies the library OWNS (copiesTotal), independent of
        // how many are currently checked out.
        Catalog cat = new Catalog();
        Book a = cat.addBook("Dune", "Herbert", 3);
        cat.addBook("Foundation", "Asimov", 2);
        a.setCopiesAvailable(2);   // one copy of Dune is out, but the library still OWNS 3
        assertEquals(5, cat.totalCopies(),
                "totalCopies() should sum copiesTotal (3 + 2 = 5); checking a copy out doesn't reduce it");
    }

    // -----------------------------------------------------------------------
    // Wave 2 — edge cases: partial stock, the last copy, one return too many
    // -----------------------------------------------------------------------

    @Test
    void availableBooksIncludesPartiallyBorrowed() {
        // available_books returns every book with at least one copy on the shelf — INCLUDING
        // a book that has some copies out and some available. Only a fully checked-out book
        // is excluded.
        Catalog cat = new Catalog();
        cat.addBook("Dune", "Herbert", 2);                  // 2 of 2 on the shelf
        Book partial = cat.addBook("Foundation", "Asimov", 2);
        partial.setCopiesAvailable(1);                      // 1 of 2 on the shelf
        Book out = cat.addBook("Rare Tome", "Anon", 1);
        out.setCopiesAvailable(0);                          // 0 of 1 on the shelf
        List<String> titles = cat.availableBooks().stream()
                .map(Book::getTitle)
                .sorted()
                .collect(Collectors.toList());
        assertEquals(List.of("Dune", "Foundation"), titles,
                "availableBooks() should list every book with >= 1 copy on the shelf, including the "
                        + "partially-borrowed 'Foundation'; only the fully checked-out 'Rare Tome' is excluded");
    }

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

    @Test
    void weedRemovesEveryThinlyStockedTitle() {
        // weed pulls EVERY title stocked under the cutoff. NOTE: the order these are added
        // in is load-bearing — keep the two thin titles adjacent.
        Catalog cat = new Catalog();
        cat.addBook("Pamphlet A", "Anon", 1);   // under 2
        cat.addBook("Pamphlet B", "Anon", 1);   // under 2, right after A
        cat.addBook("Encyclopedia", "Britannica", 5);
        cat.weed(2);
        List<String> titles = cat.getBooks().stream()
                .map(Book::getTitle)
                .sorted()
                .collect(Collectors.toList());
        assertEquals(List.of("Encyclopedia"), titles,
                "weed(2) should remove EVERY title owned in fewer than 2 copies (both pamphlets), "
                        + "leaving only the Encyclopedia");
    }

    @Test
    void restockPutsNewCopiesOnTheShelf() {
        // Restocking acquires copies that are ready to borrow: it must raise BOTH what the
        // library owns and what's on the shelf. A restock that only bumps the owned count
        // leaves the new copies permanently off the shelf.
        Catalog cat = new Catalog();
        Book book = cat.addBook("Dune", "Herbert", 1);
        cat.checkout(book.getId());     // available 1 -> 0, still owns 1
        cat.restock(book.getId(), 2);   // acquire 2 more: owns 3, and 2 should now be on the shelf
        assertEquals(3, cat.getBook(book.getId()).getCopiesTotal(),
                "restock should raise copiesTotal (1 + 2 = 3)");
        assertEquals(2, cat.getBook(book.getId()).getCopiesAvailable(),
                "restock should also put the new copies on the shelf (0 + 2 = 2); bumping only the "
                        + "owned count leaves the shelf empty");
    }

    // -----------------------------------------------------------------------
    // Correct behavior (these pass out of the box — clean reference points)
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
