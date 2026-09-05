"""Behavioral tests for BookNook.

These describe the *intended* behavior. Fix the source in catalog/catalog.py until they
all pass — do not change the tests.

There are 6 planted bugs. None of them announce themselves with a crash or an obviously
absurd value — every one is a plausible-looking implementation that quietly disagrees with
the docstring. The library distinguishes two counts: `copies_total` (how many the library
OWNS) and `copies_available` (how many are ON THE SHELF right now). Several bugs hinge on
that distinction. Read the docstring, then the code, and find the mismatch. Two waves:

  * Wave 1 — a careful read of the docstring is enough to spot the mismatch.
  * Wave 2 — the bug only bites on an edge case (a partially-borrowed book, the last copy,
    or one return too many).

Each assertion carries a message describing the intended behavior.
"""

import pytest

from catalog import Catalog


# ---------------------------------------------------------------------------
# Wave 1 — read the docstring carefully
# ---------------------------------------------------------------------------

def test_is_available_reflects_copies_on_the_shelf():
    # is_available is about what's ON THE SHELF (copies_available), not what the library
    # OWNS (copies_total). A book with a copy on the shelf is available; a book with every
    # copy checked out is NOT — even though the library still owns copies.
    cat = Catalog()
    stocked = cat.add_book("Dune", "Herbert", copies=2)
    out = cat.add_book("Rare Tome", "Anon", copies=2)
    out.copies_available = 0   # both copies are checked out; the library still owns 2
    assert cat.is_available(stocked.id) is True, (
        "is_available should be True when at least one copy is on the shelf"
    )
    assert cat.is_available(out.id) is False, (
        "is_available should be False when copies_available is 0, even though the library "
        "still OWNS copies (copies_total is 2)"
    )


def test_books_by_author_matches_exactly():
    # books_by_author matches the author EXACTLY, not as a substring. 'Frank Herbertson'
    # merely contains the letters of 'Herbert' and must not be returned for author 'Herbert'.
    cat = Catalog()
    cat.add_book("Dune", "Herbert")
    cat.add_book("Ringworld", "Larry Niven")
    cat.add_book("A Guide", "Frank Herbertson")   # contains "Herbert" but is a different author
    titles = sorted(b.title for b in cat.books_by_author("Herbert"))
    assert titles == ["Dune"], (
        "books_by_author('Herbert') should match the author EXACTLY (just 'Dune'); "
        "'Frank Herbertson' only contains the substring and must be excluded"
    )


def test_total_copies_counts_owned():
    # total_copies counts how many copies the library OWNS (copies_total), independent of how
    # many are currently checked out.
    cat = Catalog()
    a = cat.add_book("Dune", "Herbert", copies=3)
    cat.add_book("Foundation", "Asimov", copies=2)
    a.copies_available = 2   # one copy of Dune is out, but the library still OWNS 3
    assert cat.total_copies() == 5, (
        "total_copies() should sum copies_total (3 + 2 = 5); checking a copy out doesn't "
        "reduce how many the library owns"
    )


# ---------------------------------------------------------------------------
# Wave 2 — edge cases: partial stock, the last copy, one return too many
# ---------------------------------------------------------------------------

def test_available_books_includes_partially_borrowed():
    # available_books returns every book with at least one copy on the shelf — INCLUDING a
    # book that has some copies out and some still available. Only a fully checked-out book
    # is excluded.
    cat = Catalog()
    full = cat.add_book("Dune", "Herbert", copies=2)          # 2 of 2 on the shelf
    partial = cat.add_book("Foundation", "Asimov", copies=2)  # will be 1 of 2 on the shelf
    partial.copies_available = 1
    out = cat.add_book("Rare Tome", "Anon", copies=1)         # 0 of 1 on the shelf
    out.copies_available = 0
    titles = sorted(b.title for b in cat.available_books())
    assert titles == ["Dune", "Foundation"], (
        "available_books() should list every book with >= 1 copy on the shelf, including the "
        "partially-borrowed 'Foundation'; only the fully checked-out 'Rare Tome' is excluded"
    )


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
# Correct behavior (these pass out of the box — clean reference points)
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
