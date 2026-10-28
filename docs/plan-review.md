# Plan review

Method: every code block from `docs/plan.md` was extracted into a scratch Maven
project (JDK 21, `maven.compiler.release` 17) and built exactly as written.
Result: it compiles, all 42 tests pass (5 Student, 11 Workshop, 9 Academy,
13 Reports, 4 FileIntegration) and `Main` prints the expected report.
So there are no compile or test errors. The notes below are improvements.

## Fix before relying on it
1. **Section 8.1 typo claim is right.** `arl` is not a substring of `Chitrakalpa`.
   Keep the substring search; ask the instructor about the example.
2. **Sorting is case-sensitive.** `Comparator.comparing(Student::name)` puts
   `alice` after `Zed`. Use `String.CASE_INSENSITIVE_ORDER` in step 3/4.
3. **Records already have `equals`/`hashCode`.** `Student` and `ContactDetails`
   are records, so they compare by value. The plan says `equals`/`hashCode` is a
   later extension for `Set<Student>`; the explanation in "Why List" should say
   that value equality already exists and duplicates are still checked by ID.

## Worth improving later (not blocking)
4. `ContactDetails` accepts any non-blank email (`"abc"`). Add a minimal `@` check
   as an explicit extension, not silently, because the spec only says "blank".
5. `StudentFileLoader` errors do not name the line number, and a UTF-8 BOM at the
   start of `students.txt` (Windows Notepad) breaks the id parse. Strip `\uFEFF`
   and wrap the error with the line number.
6. `SimpleFiles.readLines` re-implements `Files.readAllLines`. Fine for teaching,
   mention it.
7. `Academy` keeps two maps; if students can change city (variant B in the plan)
   the workshop lists must be updated too. Decide this before step 7.

## Scope of this repo so far
Steps 0 and 1 only. Each action is a separate commit so any of them can be
reverted or reworked with `git revert`.
