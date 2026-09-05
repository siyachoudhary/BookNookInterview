"""BookNook — a tiny in-memory library catalog.

Each book tracks how many copies the library owns (`copies_total`) and how many are
currently on the shelf (`copies_available`). Checking a book out lowers the available
count; returning it raises it back (never above what the library owns).

The docstrings below describe what each method is *supposed* to do. The behavioral tests
in tests/test_catalog.py check that intent. Some implementations don't match their
description — those are the bugs you're here to find.
"""


class Book:
    def __init__(self, id, title, author, copies):
        self.id = id
        self.title = title
        self.author = author
        self.copies_total = copies
        self.copies_available = copies

    def __repr__(self):
        return (f"Book(#{self.id} {self.title!r} by {self.author}, "
                f"{self.copies_available}/{self.copies_total} available)")


class Catalog:
    def __init__(self):
        self.books = []
        self._next_id = 1

    def add_book(self, title, author, copies=1):
        """Add a book to the catalog and return it. It starts with all copies available."""
        book = Book(self._next_id, title, author, copies)
        self.books.append(book)
        self._next_id += 1
        return book

    def get_book(self, book_id):
        """Return the book with the given id, or None if there isn't one."""
        for book in self.books:
            if book.id == book_id:
                return book
        return None

    def checkout(self, book_id):
        """Check out one copy.

        If at least one copy is available, decrement the available count and return True.
        If none are available, change nothing and return False.
        """
        book = self.get_book(book_id)
        book.copies_available -= 1
        return True

    def return_book(self, book_id):
        """Return one copy, raising the available count back up.

        The available count must never exceed the number of copies the library owns
        (copies_total).
        """
        book = self.get_book(book_id)
        book.copies_available += 1

    def is_available(self, book_id):
        """True if at least one copy of the book is currently ON THE SHELF.

        This is about copies_available (what's on the shelf right now), not copies_total
        (what the library owns): a book with every copy checked out is not available.
        """
        book = self.get_book(book_id)
        return book.copies_total > 0

    def available_books(self):
        """Return every book with at least one copy on the shelf (copies_available > 0).

        A partially-borrowed book (some copies out, some still on the shelf) still counts as
        available; only a fully checked-out book is excluded.
        """
        return [book for book in self.books if book.copies_available == book.copies_total]

    def books_by_author(self, author):
        """Return every book whose author EXACTLY equals the given name.

        This is an exact match on the author, not a substring or prefix test.
        """
        return [book for book in self.books if author in book.author]

    def total_copies(self):
        """Return the total number of copies the library owns across all books."""
        return sum(book.copies_available for book in self.books)
