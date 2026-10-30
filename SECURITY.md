# Security notes

Educational project. All sample data is synthetic (example.com emails,
fictional people). No real personal data is stored or processed.

## Handled
- Text fields reject blank values and control characters
  (see Validation.requireText, ValidationSecurityTest).
- Ids and capacities must be positive; invalid input throws
  IllegalArgumentException, invalid state throws IllegalStateException.
- Student and ContactDetails are immutable; changing a contact means
  creating a new Student.
- Dependencies are watched by Dependabot (.github/dependabot.yml).

## Not handled (known risks, accepted)
- No authentication (local console program).
- File input: data/students.txt is trusted input. When user-supplied
  paths appear, add size limits and path containment checks.
- Database (future): parameterized queries only, no concatenated SQL.

## Trust boundary
The only external input today is data/students.txt with synthetic data.
Everything else (ids, capacities, report paths) comes from the code itself.

## History note
Merge commits of PR #3-#5 were authored with a personal email address.
The history was NOT rewritten: those merges are signed by GitHub, and
rewriting would break the signatures and all SHAs. Going forward all
commits use the GitHub noreply address, and "Block command line pushes
that expose my email" is enabled in GitHub Settings.