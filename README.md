# Library Management System

A Java Swing desktop application for managing books, members, and issue/return records. Data is persisted in CSV files in the project root. The application has no external JAR dependency.

## Features

- Username/password login from `.env`.
- Book CRUD: add, update, delete, search, and list.
- Member CRUD: add, update, delete, search, and list.
- Issue a book for 14 days.
- Return an issued book and restore its quantity.
- Prevent issuing unavailable or already-issued books.
- Dashboard statistics for books, members, issued records, and overdue records.
- Bulk member import from CSV with duplicate and invalid-row reporting.
- Runs from VS Code, Eclipse, or a terminal.

## Project Structure

```text
Library-Management-System/
|-- .env                         Runtime credentials and CSV filenames
|-- .classpath                   Eclipse source/output configuration
|-- .project                     Eclipse Java project metadata
|-- books.csv                    Book data created at startup
|-- members.csv                  Member data created at startup
|-- issue_records.csv            Issue data created at startup
|-- bin/                         Compiled classes
|-- lib/                         Optional libraries; none are required
|-- src/
|   `-- com/library/
|       |-- Main.java            Application entry point
|       |-- config/
|       |   |-- EnvConfig.java   Reads `.env`
|       |   `-- FileCreation.java Creates CSV files and headers
|       |-- model/
|       |   |-- Book.java        Book entity
|       |   |-- Member.java      Member entity
|       |   `-- IssueRecord.java Issue entity
|       |-- service/
|       |   |-- BooksService.java        Book CSV persistence
|       |   |-- MembersService.java       Member CSV persistence
|       |   `-- IssueRecordsService.java  Issue CSV persistence
|       |-- util/
|       |   |-- CsvParser.java   Quoted CSV parser
|       |   `-- InputHelper.java Small Swing input helpers
|       `-- frames/
|           |-- LoginFrame.java       Login window
|           |-- DashboardFrame.java   Main navigation/statistics window
|           |-- BookFrame.java        Book CRUD window
|           |-- MemberFrame.java      Member CRUD window
|           |-- IssueFrame.java       Issue/return window
|           `-- BulkImportFrame.java  Member CSV import window
`-- projectdetails.md             Earlier project notes
```

## Architecture

```text
Main
  -> FileCreation
      -> EnvConfig -> .env
  -> LoginFrame
      -> DashboardFrame
          -> BookFrame -> BooksService -> books.csv
          -> MemberFrame -> MembersService -> members.csv
          -> IssueFrame -> BooksService + MembersService + IssueRecordsService
          -> BulkImportFrame -> CsvParser + MembersService
```

The application uses a simple layered design:

- **Frames** contain Swing UI and user actions.
- **Services** contain CSV reading, writing, searching, and counting.
- **Models** represent rows as typed Java objects.
- **Config** reads runtime settings and creates missing data files.
- **Utilities** provide CSV parsing and small input helpers.

All file reads and writes use `java.io` classes with try-with-resources. CSV writers quote values containing commas or double quotes. `CsvParser` supports quoted commas and escaped quotes.

## Configuration

Create `.env` in the project root. The current file should contain:

```text
DB_USERNAME=admin
DB_PASSWORD=admin
DB_BOOKS_FILENAME=books.csv
DB_MEMBERS_FILENAME=members.csv
DB_ISSUE_RECORDS_FILENAME=issue_records.csv
```

The names retain the existing `DB_` prefix for compatibility, but this version uses CSV files and does not connect to MySQL.

## CSV Formats

`books.csv`:

```text
id,title,author,isbn,quantity
B001,The Hobbit,J.R.R. Tolkien,9780261102217,3
```

`members.csv`:

```text
id,name,email,phone
M001,John Smith,jsmith@example.com,12345
```

`issue_records.csv`:

```text
id,book_id,member_id,issue_date,due_date,return_date
I001,B001,M001,2026-10-14,2026-10-28,Not_Returned
```

Do not remove the header row. `FileCreation` creates it automatically when a file does not exist.

## Important Classes and Methods

### `com.library.Main`

`main` creates the three CSV files and starts the login frame through `SwingUtilities.invokeLater`, keeping Swing UI creation on the event-dispatch thread.

### `com.library.config.EnvConfig`

Loads `.env` once using `FileInputStream` and exposes the five configuration values through getter methods.

### `com.library.config.FileCreation`

`createBooksFile`, `createMembersFile`, and `createIssueRecordsFile` create missing files. The private `create` method creates parent directories when needed and writes headers only once.

### Models

`Book`, `Member`, and `IssueRecord` store one CSV row each. Getters, setters, and `toRow()` provide the service/UI boundary.

### Services

- `BooksService.readAll()` loads all books.
- `BooksService.findById()` finds one book.
- `BooksService.writeAll()` rewrites the complete book file.
- `MembersService.readAll()`, `findById()`, and `writeAll()` provide equivalent member operations.
- `IssueRecordsService.activeForBook()` finds an unreturned issue.
- `IssueRecordsService.isBookIssued()` prevents duplicate active issues.
- `IssueRecordsService.nextId()` creates IDs such as `I001`.
- `IssueRecordsService.overdueCount()` compares due dates with today.

### Frames

- `LoginFrame` checks credentials and opens the dashboard.
- `DashboardFrame` opens each workflow and refreshes four statistics.
- `BookFrame` validates fields, enforces unique book IDs, and provides CRUD/search operations.
- `MemberFrame` enforces required ID/name fields and unique IDs on add.
- `IssueFrame` verifies book/member existence, quantity, and active issue state before changing both books and issue records.
- `BulkImportFrame` reads an external CSV with `BufferedReader`, skips an optional header, reports every row, and writes members once after processing.

## Compile and Run in PowerShell

Run from the project root:

```powershell
Remove-Item -Recurse -Force .\bin\* -ErrorAction SilentlyContinue
javac -d bin (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
java -cp "bin;lib/*" com.library.Main
```

Login with the values in `.env`:

```text
Username: admin
Password: admin
```

On Linux/macOS, use `:` instead of `;` in the Java classpath:

```bash
java -cp "bin:lib/*" com.library.Main
```

## VS Code

1. Open the `Library-Management-System` folder.
2. Install a JDK extension if VS Code does not detect Java.
3. Confirm `.env` exists in the project root.
4. Run the PowerShell compile command above.
5. Run `com.library.Main` from the Java Run action or terminal.

## Eclipse

1. Import the folder as an existing Eclipse project.
2. Eclipse uses `.classpath` with `src` as the source folder and `bin` as the output folder.
3. Ensure the project has a JDK/JRE configured.
4. Run `com.library.Main` as a Java Application.

## How to Test Bulk Import

Create a file such as `sample-members.csv`:

```csv
id,name,email,phone
M001,John Smith,jsmith@example.com,12345
M002,"Smith, Jane",jane@example.com,67890
M001,Duplicate Person,duplicate@example.com,00000
,Missing ID,missing@example.com,11111
```

Open **Bulk Import**, choose the file, and click **Import**. The log reports `[OK]`, `[DUP]`, and `[INVALID]` rows. Existing IDs are skipped, invalid rows require both ID and name, and valid records are written in one batch.

## Common Errors and Fixes

- **Missing `.env`**: create it in the same directory from which the program is launched.
- **Missing filename configuration**: add all three `DB_*_FILENAME` values.
- **Could not find main class**: run `com.library.Main`, not `Main` or `src.Main`.
- **CSV values appear shifted**: keep the header row and use quoted fields for values containing commas.
- **Window does not open**: run with a desktop JDK, not a headless runtime.
- **Eclipse package error**: ensure `src` is configured as a source folder and packages begin with `com.library`.
- **Stale classes**: clear `bin` and compile all files again.

## Line Count

Measured after the current implementation:

| File | Lines |
|---|---:|
| `Main.java` | 15 |
| `config/EnvConfig.java` | 22 |
| `config/FileCreation.java` | 24 |
| `frames/BookFrame.java` | 52 |
| `frames/BulkImportFrame.java` | 30 |
| `frames/DashboardFrame.java` | 20 |
| `frames/IssueFrame.java` | 34 |
| `frames/LoginFrame.java` | 31 |
| `frames/MemberFrame.java` | 46 |
| `model/Book.java` | 24 |
| `model/IssueRecord.java` | 22 |
| `model/Member.java` | 21 |
| `service/BooksService.java` | 32 |
| `service/IssueRecordsService.java` | 27 |
| `service/MembersService.java` | 28 |
| `util/CsvParser.java` | 30 |
| `util/InputHelper.java` | 14 |

## Current Scope

The project is a local single-user desktop application. It has no authentication database, network API, concurrent-write protection, member deletion dependency check, or database migration layer. CSV files are appropriate for the current educational scope; a multi-user deployment would need a transactional database and service/API layer.
