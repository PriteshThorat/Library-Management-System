# Library Management System

A desktop **Library Management System** built with **Java Swing** (a single
bundled date-picker jar is the only dependency). It manages books, members, and
book issue/return records using
plain **CSV files** as the data store, with a modern custom-drawn UI (sidebar
navigation, dashboard cards, rounded buttons and fields).

---

## Project Summary

This is a **single-user, offline desktop application** that digitises the daily
operations of a small library. Instead of a server or database engine, it keeps
all state in three human-readable CSV files, which makes the project easy to run
anywhere a JDK is installed and easy to inspect or hand-edit.

**What it does at a glance**

- An **administrator logs in** with credentials read from `.env`.
- From a **dashboard** they immediately see the health of the library: how many
  books the catalogue holds, how many members are registered, how many copies
  are currently issued, and how many are overdue.
- They can **maintain the book catalogue** and the **member register** (full
  create / read / update / delete plus search).
- They can **issue a book** to a member and later **return it**, with the book's
  available quantity adjusted automatically and each record's status derived in
  real time (Issued / Overdue / Returned).

**Who it is for**

- Library staff or students who need a simple, dependency-free system.
- Learners who want a clear, self-contained example of a Java Swing CRUD app
  layered on file I/O.

**How it fits together (architecture)**

The code follows a light three-layer separation:

```
┌─────────────────────────────┐
│  frames/  (UI layer)        │  Swing screens: Login, Dashboard,
│                             │  Book, Member, Issue/Return,
│                             │  plus shared Header / Sidebar / AppIcon
└──────────────┬──────────────┘
               │ calls
┌──────────────▼──────────────┐
│  service/  (data layer)     │  BooksService, MembersService,
│                             │  IssueRecordsService — all CSV read/write
└──────────────┬──────────────┘
               │ uses
┌──────────────▼──────────────┐
│  config/  (config layer)    │  EnvConfig (.env reader),
│                             │  FileCreation (bootstraps CSV files),
│                             │  ConstantsConfig (auto-increment IDs)
└──────────────┬──────────────┘
               │ stores in
┌──────────────▼──────────────┐
│  *.csv  (flat-file store)   │  books / members / issue_records
└─────────────────────────────┘
```

**Startup flow**

1. `Main` calls `FileCreation` to make sure the three CSV files exist (creating
   them with headers if they are missing).
2. `LoginFrame` validates the entered username/password against `.env`.
3. On success the app opens `DashboardFrame`; the `Sidebar` then swaps between
   the Dashboard, Books, Members and Issue/Return screens, and handles Logout.

**Core domain rules**

- A book's `quantity` may never be negative; issuing uses one copy, returning
  gives it back.
- A member's email must look valid and the phone number must be 10 digits;
  Member IDs are unique.
- Dates are `YYYY-MM-DD`; the default loan period is **14 days**, and a record
  past its due date while still borrowed is **Overdue**.
- Book, Member and Issue IDs are **generated automatically** (prefix `B` / `M`
  / `I`) from counters kept in `constants.txt`.

**Design goals / scope**

- **Minimal dependencies** – the standard JDK plus a single bundled date-picker
  jar (`lib/jcalendar-1.4.jar`).
- **Portable and transparent storage** – CSVs you can open in any editor.
- **Custom-drawn, modern look** – rounded panels/buttons/fields and vector
  icons painted in code rather than image assets.
- **Scope**: a single administrator on one machine; no networking,
  concurrency, or multi-user access control (by design).

---

## Features

- **Login screen** – authenticates against credentials stored in `.env`.
- **Dashboard** – live stat cards for Total Books, Total Members, Issued Books
  and Overdue Books.
- **Manage Books** – add, update, delete, search books, and view them in a
  table (Book ID, Title, Author, ISBN, Quantity). Quantity must be a
  non-negative integer.
- **Manage Members** – add, update, delete, search members with validation for
  email format and a 10-digit phone number. Duplicate Member IDs are rejected.
- **Issue / Return** – issue a book to a member (auto-decrements book quantity)
  and return it (auto-increments book quantity). Live status column shows
  **Issued**, **Overdue** or **Returned**.
- **Sidebar navigation** between all views, plus **Logout**.

---

## Tech Stack

| Item         | Detail                                        |
| ------------ | --------------------------------------------- |
| Language     | Java                                          |
| UI           | Java Swing / AWT (custom painted components)  |
| Date picker  | [JCalendar](https://toedter.com/jcalendar/) 1.4 (`lib/jcalendar-1.4.jar`), used by the Issue/Return screen |
| Storage      | CSV files (via `java.io` + `Scanner`/`FileWriter`) |
| Config       | `.env` file loaded with `java.util.Properties` |
| Build        | `javac` (plain JDK, no Maven/Gradle)          |
| Tested with  | JDK 26 (`java 26.0.2.1`)                       |

`lib/jcalendar-1.4.jar` is the only external dependency. It provides the
`com.toedter.calendar.JDateChooser` component used on the Issue/Return screen;
everything else uses the standard JDK. Any further `.jar` files dropped into
`lib/` are picked up automatically because the whole folder is on the classpath.

---

## Requirements

- **JDK 8 or newer** (developed and tested on **JDK 26**).
- **Windows** – the commands below are written for **Windows PowerShell / CMD**.

Check your installation:

```powershell
java -version
javac -version
```

---

## Project Structure

```
Library-Management-System/
├── .env                         # local credentials + CSV filenames (NOT committed)
├── env.txt                      # template for .env
├── lib/                         # external jars, on classpath (jcalendar-1.4.jar)
├── bin/                         # compiled .class output
├── src/
│   ├── Main.java                # entry point (package src)
│   ├── config/
│   │   ├── EnvConfig.java       # reads values from .env
│   │   ├── FileCreation.java    # creates CSV files + headers if missing
│   │   └── ConstantsConfig.java # read/update auto-increment ID counters
│   ├── frames/
│   │   ├── LoginFrame.java      # login screen
│   │   ├── DashboardFrame.java  # dashboard with stat cards
│   │   ├── BookFrame.java       # book CRUD + search
│   │   ├── MemberFrame.java     # member CRUD + search
│   │   ├── IssueReturnFrame.java# issue / return books
│   │   ├── Header.java          # top header bar
│   │   ├── Sidebar.java         # left navigation
│   │   ├── AppIcon.java         # custom-drawn icons
│   │   └── BookIcon.java        # custom-drawn book icon
│   └── service/
│       ├── BooksService.java        # book data access
│       ├── MembersService.java      # member data access
│       └── IssueRecordsService.java # issue/return data access
├── books_database.csv           # books data (created at runtime)
├── members_database.csv         # members data (created at runtime)
├── issue_records_database.csv   # issue records (created at runtime)
└── constants.txt                # auto-increment ID counters (created/updated at runtime)
```

---

## Setup

1. **Clone the repository.**

   ```powershell
   git clone <repository-url>
   cd Library-Management-System
   ```

2. **Create the `.env` file** in the project root (copy from `env.txt`) and set
   your values:

   ```properties
   DB_USERNAME=admin
   DB_PASSWORD=adminpass
   DB_BOOKS_FILENAME=books_database.csv
   DB_MEMBERS_FILENAME=members_database.csv
   DB_ISSUE_RECORDS_FILENAME=issue_records_database.csv
   CONSTANTS_FILE_NAME=constants.txt
   ```

   - `DB_USERNAME` / `DB_PASSWORD` are the **login credentials** for the app.
   - The `DB_*_FILENAME` values are the CSV files used for storage. They are
     **created automatically** on first run (with headers) if they do not exist.
   - `CONSTANTS_FILE_NAME` points to the auto-increment ID counter file.
     `env.txt` does not list it, so add this line when you copy the template.

3. **Create the `constants.txt` file** in the project root with the starting
   counter for each auto-generated ID. This file is **required** — the app reads
   it at startup and updates the values in place as records are added, but it
   will **not** create it for you (unlike the CSVs):

   ```properties
   BOOK_ID=0
   MEMBER_ID=0
   ISSUE_ID=0
   ```

   - Each value is the **next numeric part** of the ID. The letters `B`, `M` and
     `I` are prepended automatically, so `BOOK_ID=102` produces `B102`.
   - Start at `0` for a fresh library, or bump the numbers to continue from an
     existing set of records.

4. **Add the `lib/jcalendar-1.4.jar` dependency.** The date-picker jar lives in
   the `lib/` folder and is required to compile and run (the Issue/Return screen
   uses `JDateChooser`). If it is missing, download
   [JCalendar 1.4](https://toedter.com/jcalendar/) and place the jar there:

   ```
   Library-Management-System/
   └── lib/
       └── jcalendar-1.4.jar
   ```

5. Make sure the `.csv` files are in the **working directory** from which you run
   the app (they are created automatically on first run).

---

## Default Login

```
Username: admin
Password: adminpass
```

(Change these in `.env`.)

---

## Data Storage (CSV Schema)

| File                          | Header                                                        |
| ----------------------------- | ------------------------------------------------------------- |
| `books_database.csv`          | `Book ID,Title,Author,isbn,quantity`                          |
| `members_database.csv`        | `member_id,name,email,phone`                                  |
| `issue_records_database.csv`  | `issue_id,book_id,member_id,issue_date,due_date,return_date`  |

- Dates use `YYYY-MM-DD`. Due date defaults to **issue date + 14 days**.
- An unreturned record stores `Not_Returned` in the `return_date` column.

`constants.txt` holds the next numeric part for each auto-generated ID
(loaded/saved via `java.util.Properties`):

```properties
BOOK_ID=102
ISSUE_ID=100
MEMBER_ID=100
```

---

## Build & Run

All commands are run from the **project root** (the folder containing `src/`,
`lib/` and `.env`).

### Compile with the JCalendar jar, then run

This is the most direct way to build every source file (all of `config`,
`service` and `frames`) with the date-picker jar on the classpath, then launch
the app:

```powershell
javac -cp "bin;lib\jcalendar-1.4.jar" -d bin src\Main.java src\config\*.java src\service\*.java src\frames\*.java
java -cp "lib/*;bin" src.Main
```

- `-cp "bin;lib\jcalendar-1.4.jar"` puts `bin` (for previously compiled classes)
  and the JCalendar jar on the compile classpath, so `JDateChooser` resolves.
- `-d bin` writes the `.class` files into `bin/`.
- The `*.java` globs pull in every config, service and frame source.
- At run time `java -cp "lib/*;bin" src.Main` loads all jars in `lib/` plus the
  compiled classes in `bin/`.

> The classpath separator on Windows is `;` (semicolon). The quotes around
> `"lib/*"` are required so the shell passes the wildcard to Java instead of
> expanding it.

### Recommended: compile everything with one command

Because `Main.java` declares `package src`, compile it with the project root on
the **source path** and `javac` will automatically compile every referenced
class in one pass:

```powershell
javac -cp "lib/*" -sourcepath . -d bin src/Main.java
```

Then run:

```powershell
java -cp "lib/*;bin" src.Main
```

> The classpath separator on Windows is `;` (semicolon). The quotes around
> `"lib/*"` are required so the shell passes the wildcard to Java instead of
> expanding it.

### Compile all `.java` files at once (alternative)

If you prefer to name no entry point, compile every source file explicitly:

```powershell
javac -cp "lib/*" -d bin (Get-ChildItem -Recurse src -Filter *.java).FullName
```

or, from a plain `cmd.exe` prompt:

```bat
for /r src %f in (*.java) do javac -cp "lib/*" -d bin "%f"
```

### Reference commands (from the original notes)

```powershell
javac -cp "lib/*" -d bin src/Main.java
javac -d bin src\Main.java src\config\EnvConfig.java src\frames\LoginFrame.java src\frames\DashboardFrame.java src\config\FileCreation.java src\service\BooksService.java
javac -d bin src\Main.java src\config\EnvConfig.java src\frames\LoginFrame.java src\frames\DashboardFrame.java src\config\FileCreation.java src\service\BooksService.java src\frames\BookFrame.java src\frames\MemberFrame.java

java -cp "lib/*;bin" Main

javac -d . *.java
```

> Note: since `Main` is declared inside `package src`, the runnable main class is
> **`src.Main`**. Use `java -cp "lib/*;bin" src.Main`.

---

## Windows Services

To open the Windows Services manager, press **Win + R**, type `services.msc`,
and press **Enter**.

---

## Notes

- `.env`, `*.class`, `*.csv`, `constants.txt` and `dist/` are ignored by git
  (see `.gitignore`). Commit `env.txt` as a template; keep real credentials in
  `.env`.
- CSV parsing is intentionally simple (`String.split(",")`), so **avoid commas
  inside field values** (use underscores, e.g. `Francesc_Miralles`).
