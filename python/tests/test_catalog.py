"""Behavioral tests for BookNook.

These describe the *intended* behavior. Fix the source in catalog/catalog.py until they
all pass — do not change the tests.

There are 6 planted bugs: 4 are easy to spot from a single failing test, and 2 are subtler
(they only bite on an edge case). Each assertion carries a message describing the intended
behavior, so a failure tells you what the method should do — not just how two values
differ.
"""

import pytest

from catalog import Catalog


# ---------------------------------------------------------------------------
# The 4 easier bugs
# ---------------------------------------------------------------------------

def test_is_available_true_with_one_copy():
    # A book with a single copy on the shelf is available.
    cat = Catalog()
    book = cat.add_book("Dune", "Herbert", copies=1)
    assert cat.is_available(book.id) is True, (
        "is_available should be True whenever at least ONE copy is on the shelf; a book "
        "with exactly 1 copy is available"
    )


def test_available_books_lists_in_stock():
    # available_books returns books that HAVE copies on the shelf, not the sold-out ones.
    cat = Catalog()
    stocked = cat.add_book("Dune", "Herbert", copies=2)
    cat.add_book("Rare Tome", "Anon", copies=0)
    titles = [b.title for b in cat.available_books()]
    assert titles == ["Dune"], (
        "available_books() should return books WITH copies available ('Dune'), not the "
        "out-of-stock ones"
    )


def test_books_by_author_matches_author():
    # books_by_author returns the books written BY that author.
    cat = Catalog()
    cat.add_book("Dune", "Herbert")
    cat.add_book("Foundation", "Asimov")
    titles = [b.title for b in cat.books_by_author("Herbert")]
    assert titles == ["Dune"], (
        "books_by_author('Herbert') should return Herbert's books ('Dune'), not everyone "
        "else's"
    )


def test_total_copies_counts_owned():
    # total_copies counts how many copies the library OWNS, independent of how many are
    # currently checked out.
    cat = Catalog()
    a = cat.add_book("Dune", "Herbert", copies=3)
    cat.add_book("Foundation", "Asimov", copies=2)
    cat.checkout(a.id)  # one copy of Dune is now out, but the library still OWNS 3
    assert cat.total_copies() == 5, (
        "total_copies() should sum copies_total (3 + 2 = 5); checking a copy out doesn't "
        "reduce how many the library owns"
    )


# ---------------------------------------------------------------------------
# The 2 harder bugs (edge cases)
# ---------------------------------------------------------------------------

def test_checkout_refuses_when_none_available():
    # Once every copy is checked out, a further checkout must fail and NOT drive the
    # available count negative.
    cat = Catalog()
    book = cat.add_book("Dune", "Herbert", copies=1)
    assert cat.checkout(book.id) is True, "the first checkout of the only copy should succeed"
    assert cat.checkout(book.id) is False, (
        "with 0 copies left, checkout should return False and refuse to hand out a copy "
        "the library doesn't have"
    )
    assert cat.get_book(book.id).copies_available == 0, (
        "a refused checkout must leave copies_available at 0, never negative"
    )


def test_return_does_not_exceed_owned_copies():
    # Returning copies can never raise availability above what the library owns. An extra
    # (erroneous) return must not invent a copy.
    cat = Catalog()
    book = cat.add_book("Dune", "Herbert", copies=1)
    cat.checkout(book.id)     # available: 1 -> 0
    cat.return_book(book.id)  # available: 0 -> 1 (back to full)
    cat.return_book(book.id)  # already full: must stay at 1, not climb to 2
    assert cat.get_book(book.id).copies_available == 1, (
        "copies_available must never exceed copies_total (1); a second return with nothing "
        "checked out should be a no-op"
    )


# ---------------------------------------------------------------------------
# Correct behavior (kept as clean reference points)
# ---------------------------------------------------------------------------

def test_add_book_assigns_incrementing_ids():
    cat = Catalog()
    a = cat.add_book("Dune", "Herbert")
    b = cat.add_book("Foundation", "Asimov")
    assert a.id == 1, "the first book added should get id 1"
    assert b.id == 2, "the second book added should get id 2"
    assert a.copies_available == a.copies_total, "a new book starts with all copies available"


def test_get_book_unknown_id():
    cat = Catalog()
    cat.add_book("Dune", "Herbert")
    assert cat.get_book(999) is None, "get_book should return None for an id that isn't in the catalog"
