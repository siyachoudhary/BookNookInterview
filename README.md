# BookNook — Debugging Technical Interview

Welcome! This is a **timeboxed (~60 minute)** technical interview built around a small
library-catalog library called **BookNook**. There are two identical implementations in
the same repo — **Python** and **Java** — so pick whichever language you're most
comfortable in.

The interview is really **one main task with an optional bonus**:

1. **Debugging (the whole interview)** — The library ships with a failing test suite. Eight
   bugs have been planted. Find and fix them until the tests are green. None of them are
   one-liners that scream at you — they're the kind of plausible-looking code that quietly
   does the wrong thing, so take your time and reason carefully.
2. **Add a Feature (extra credit)** — *Only if you finish the debugging comfortably early*
   (roughly, all tests green in under 30 minutes) we'll spend the remaining time adding a
   small feature together. This is a bonus, not a requirement — a thorough, well-narrated
   debugging pass is the main thing we're evaluating.

We're not looking for perfection. We want to see how you read unfamiliar code, form
hypotheses, verify them, and communicate as you go. **Think out loud.**

---

## What is BookNook?

A tiny in-memory library catalog. Each book records how many copies the library owns
(`copies_total`) and how many are on the shelf right now (`copies_available`). The
`Catalog` class lets you add books, look them up, check copies out and back in, ask
whether a title is available, list what's in stock, filter by author, total the collection,
weed thinly-stocked titles, and restock existing ones.

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

## Part 1 — Debugging (the main task)

1. Run the test suite. You should see multiple failures.
2. Read the failing tests to understand the *intended* behavior (each assertion has a
   message describing it).
3. Open the source (`catalog/catalog.py` or
   `src/main/java/com/example/catalog/Catalog.java`) — each method's docstring/Javadoc
   states what it should do — and fix the bugs.
4. Re-run until everything is green.

There are **eight** planted bugs, and **none of them are loud** — there are no crashes or
wildly-wrong values to point the way. Each is a plausible implementation that quietly
disagrees with the method's docstring. A recurring theme: this library tracks two numbers,
`copies_total` (how many the library **owns**) and `copies_available` (how many are **on the
shelf** right now), and several bugs quietly confuse the two. Others are subtler still — a
loop that mutates the shelf list while it walks it. The **docstring on each method states
what it is supposed to do** — the bug is (almost) always a mismatch between that description
and the code.

The tests come in two waves: *Wave 1* is catchable from a careful read of the docstring;
*Wave 2* only bites on an edge case (a partially-borrowed book, the last copy, one return
too many, an adjacent pair dropped while weeding, or restocked copies that never reach the
shelf). Fix the source, **not** the tests.

**As you work, tell us:** what does the failing test expect, what did you observe, what's
your hypothesis, and how did the fix confirm it?

---

## Part 2 — Add a Feature (extra credit — only if you finish early)

**This part is a bonus.** We only reach it if you've finished the debugging comfortably
early — as a rough rule of thumb, all tests green in **under 30 minutes** with time to
spare. If debugging takes the whole session, that's completely fine; a careful, well-
narrated debugging pass is what we're really evaluating. Don't rush Part 1 to get here.

If we do have time: pick **one** feature below (or propose your own) and implement it,
**including at least one test**. Reach for whatever tools and references you'd normally use
(docs, Google, StackOverflow, AI assistants such as Copilot/ChatGPT/Claude, etc.).

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
