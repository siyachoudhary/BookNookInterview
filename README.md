# BookNook — Debugging Technical Interview

Welcome! This is a **timeboxed (~60 minute)** technical interview built around a small
library-catalog library called **BookNook**. There are two identical implementations in
the same repo — **Python** and **Java** — so pick whichever language you're most
comfortable in.

The interview has two parts:

1. **Debugging (~35 min)** — The library ships with a failing test suite. Six bugs have
   been planted (four easy, two subtle). Find and fix them until the tests are green.
2. **Feature (~20 min)** — Once tests pass, add a new feature. This part is open-ended:
   use any external resources you like (docs, Google, StackOverflow, AI assistants such
   as Copilot/ChatGPT/Claude, etc.). We care about how you approach the problem.

We're not looking for perfection. We want to see how you read unfamiliar code, form
hypotheses, verify them, and communicate as you go. **Think out loud.**

---

## What is BookNook?

A tiny in-memory library catalog. Each book records how many copies the library owns
(`copies_total`) and how many are on the shelf right now (`copies_available`). The
`Catalog` class lets you add books, look them up, check copies out and back in, ask
whether a title is available, list what's in stock, filter by author, and total the
collection.

The two implementations behave identically — same classes, same methods, same bugs.

---

## Setup

### Prerequisites

| Language | Needs |
|----------|-------|
| Python   | Python 3.9+ and `pytest` (`pip install pytest`) |
| Java     | JDK 17+ and Maven 3.8+ |

### Python

```bash
cd python
python -m pip install pytest        # once
python -m pytest -v                 # run the tests
```

### Java

```bash
cd java
mvn test                            # compiles and runs the tests
```

> Tip: run a single test while iterating.
> - Python: `python -m pytest tests/test_catalog.py::test_checkout_refuses_when_none_available -v`
> - Java: `mvn -Dtest=CatalogTest#checkoutRefusesWhenNoneAvailable test`

---

## Part 1 — Debugging (~35 min)

1. Run the test suite. You should see multiple failures.
2. Read the failing tests to understand the *intended* behavior (each assertion has a
   message describing it).
3. Open the source (`catalog/catalog.py` or
   `src/main/java/com/example/catalog/Catalog.java`) — each method's docstring/Javadoc
   states what it should do — and fix the bugs.
4. Re-run until everything is green.

There are **six** planted bugs: **four are easy to spot** from a single failing test, and
**two are subtler** — they only surface on an edge case (think: the last copy, or one
return too many), so the failing test won't point straight at the buggy line. Fix the
source, **not** the tests.

**As you work, tell us:** what does the failing test expect, what did you observe, what's
your hypothesis, and how did the fix confirm it?

---

## Part 2 — Add a Feature (~20 min)

Once the suite is green, pick **one** feature below (or propose your own) and implement it,
**including at least one test**.

- **Waitlist.** When `checkout` fails because a book is out, record the requester; expose
  `waitlist(id)` returning who is waiting.
- **Search.** Add `search(keyword)` returning books whose title contains the keyword,
  case-insensitively.
- **Most borrowed.** Track checkouts per book and add `most_borrowed()`.
- **Remove a book.** Add `remove_book(id)`, deciding what to do if copies are still out.

Walk us through your design choices, edge cases, and how you'd extend it further.

---

## What we're evaluating

- **Debugging method** — reading code, isolating faults, verifying fixes.
- **Communication** — narrating your reasoning and trade-offs.
- **Code quality** — clean, readable changes that match the surrounding style.
- **Feature judgment** — sensible design, edge-case awareness, and a test that proves it.

Good luck — and remember to think out loud!
