# Library Management System: Complete Beginner Guide

## 1. What This Project Is

This is a desktop Library Management System written in Java. It uses Java Swing for the graphical interface and CSV files for local data storage. It does not require a database or external JAR file for the main application.

The application provides:

- Username and password login.
- Dashboard statistics.
- Book add, update, delete, search, and list operations.
- Member add, update, delete, search, and list operations.
- Issue and return book operations.
- Quantity updates when a book is issued or returned.
- Overdue-book calculation.
- Bulk member import from CSV, TSV, or Excel-exported text files.
- Automatic creation of required CSV files.

The main source package is `com.library`. The older `src.*` files are not the active application entry point.

## 2. How the Program Starts

```text
com.library.Main
    |
    +-- EnvConfig reads .env
    |
    +-- FileCreation creates missing CSV files
    |
    +-- LoginFrame opens the login window
             |
             +-- DashboardFrame
                    |
                    +-- BookFrame -> BooksService -> books.csv
                    +-- MemberFrame -> MembersService -> members.csv
                    +-- IssueFrame -> all services -> issue_records.csv
                    +-- BulkImportFrame -> CsvParser + MembersService
```

The normal execution sequence is:

1. The JVM loads `com.library.Main`.
2. `Main.main()` creates the data files if they do not exist.
3. Swing starts `LoginFrame` on the Event Dispatch Thread.
4. `LoginFrame` reads credentials from `.env`.
5. A successful login disposes the login window and opens `DashboardFrame`.
6. The dashboard reads counts from the CSV services.
7. Buttons open the required workflow frame.
8. A service reads CSV rows into model objects.
9. The user changes data through a Swing form.
10. The service writes the updated model list back to the CSV file.

## 3. Folder Structure

```text
Library-Management-System/
|
|-- .env                         Runtime configuration
|-- env.txt                      Example credentials reference
|-- books.csv                    Book data
|-- members.csv                  Member data
|-- issue_records.csv            Issue and return data
|-- .classpath                   Eclipse source/output configuration
|-- .project                     Eclipse project metadata
|-- README.md                    Short project guide
|-- projectdetails.md            This detailed guide
|-- src/com/library/
|   |-- Main.java
|   |-- config/
|   |   |-- EnvConfig.java
|   |   `-- FileCreation.java
|   |-- model/
|   |   |-- Book.java
|   |   |-- Member.java
|   |   `-- IssueRecord.java
|   |-- service/
|   |   |-- BooksService.java
|   |   |-- MembersService.java
|   |   `-- IssueRecordsService.java
|   |-- util/
|   |   |-- CsvParser.java
|   |   `-- InputHelper.java
|   `-- frames/
|       |-- LoginFrame.java
|       |-- DashboardFrame.java
|       |-- BookFrame.java
|       |-- MemberFrame.java
|       |-- IssueFrame.java
|       `-- BulkImportFrame.java
|-- Test/Swing.java              Separate experiment
|-- bin/                         Compiled class output
`-- lib/                         Optional external libraries
```

## 4. Java Concepts Used

### Packages

A package groups related classes. For example, `com.library.model` contains data classes and `com.library.service` contains file operations. The folder path must match the package name.

### Classes and objects

A class is a blueprint. An object is an instance created from that blueprint. `new Book(...)` creates one book object in memory.

### Encapsulation

Model fields are private. Other classes use public getters and setters instead of directly changing fields. This protects the internal state of an object.

### `static`

A static method belongs to the class, not one object. Services use static methods because the application does not need one separate service object for each window.

### `final`

A final field reference cannot point to a different object after initialization. It does not always make the object itself immutable. For example, a final `JTextField` can still have its text changed.

### Exceptions

File operations can fail, so service methods declare `throws IOException`. UI event methods catch the exception and show an error dialog.

### Try-with-resources

`try (BufferedReader reader = ...)` automatically closes the reader when the block ends, even when an exception occurs.

### Swing event handling

A button receives an `ActionListener`. The listener runs when the user clicks the button. Swing components should be created and changed on the Event Dispatch Thread.

### CSV persistence

CSV is plain text. The first line is a header, and every later line is one record. The program reads CSV into objects, changes objects in memory, and writes the complete list back.

### Memory behavior

- Local variables such as `row`, `book`, and `reader` live in a method stack frame while a method runs.
- Objects such as `Book`, `Member`, `JFrame`, lists, and tables live on the heap.
- A reference variable stores a reference to an object, not the complete object itself.
- `readAll()` creates a new list and new model objects each time it is called.
- After a frame is disposed and no reference points to it, the garbage collector may reclaim it.
- Static fields such as configuration properties remain reachable for the life of the class loader.
- CSV files are persistent storage outside JVM memory. Closing the program does not remove them.
- `DefaultTableModel` stores a separate UI copy of rows; changing a table row does not automatically write the CSV file.

## 5. Configuration and CSV Formats

`.env` must be in the directory from which the application is started:

```properties
DB_USERNAME=admin
DB_PASSWORD=admin
DB_BOOKS_FILENAME=books.csv
DB_MEMBERS_FILENAME=members.csv
DB_ISSUE_RECORDS_FILENAME=issue_records.csv
```

`books.csv`:

```csv
id,title,author,isbn,quantity
B001,The Hobbit,J.R.R. Tolkien,9780261102217,3
```

`members.csv`:

```csv
id,name,email,phone
M001,John Smith,jsmith@example.com,1234567890
```

`issue_records.csv`:

```csv
id,book_id,member_id,issue_date,due_date,return_date
I001,B001,M001,2026-10-14,2026-10-28,Not_Returned
```

The header row is required. Dates use ISO format `yyyy-MM-dd`. The special value `Not_Returned` means the book is still issued.

---

# 6. File-by-File Explanation

## 6.1 `src/com/library/Main.java`

### Purpose

`Main` is the application entry point. Java starts here because the class contains `public static void main(String[] args)`.

### Package and imports

- `package com.library;` places the class in the root application package.
- `FileCreation` is imported to prepare CSV files.
- `LoginFrame` is imported to open the first window.
- `IOException` handles file errors.
- `JOptionPane` displays startup errors.
- `SwingUtilities` starts the Swing UI safely.

### Class, method, and variables

- `Main` is `final` because it is not intended to be subclassed.
- The private constructor prevents accidental creation of a `Main` object.
- `args` can contain command-line arguments, although this application does not use them.
- The `try` block initializes books, members, and issue records.
- `SwingUtilities.invokeLater` queues login-window creation on Swing's Event Dispatch Thread.
- `error` stores an `IOException` when startup fails.

### How it works in memory

`main` has a stack frame while running. `FileCreation` creates file objects temporarily. The login frame becomes a heap object and remains reachable through the visible Swing application.

### Ten questions

1. Why must `Main` contain `public static void main`? **It is the JVM entry method.**
2. Why is `Main` final? **No class should inherit from this startup utility.**
3. Why is the constructor private? **To prevent unnecessary `Main` objects.**
4. Why does `main` call `FileCreation` first? **The services need files and headers before reading.**
5. Why is `IOException` caught? **File creation can fail because of path or permission problems.**
6. Why is `SwingUtilities.invokeLater` used? **Swing UI work belongs on the Event Dispatch Thread.**
7. What does `args` contain? **Command-line arguments supplied when starting Java.**
8. What happens after file creation? **The login frame is scheduled.**
9. Does `Main` store books in memory? **No, services load them when needed.**
10. What is the result of a startup error? **The error is printed and shown in a dialog.**

## 6.2 `src/com/library/config/EnvConfig.java`

### Purpose

`EnvConfig` loads `.env` values and provides named getter methods. It keeps configuration access in one class instead of scattering file-reading code through the UI.

### Main fields and methods

- `VALUES` is a static `Properties` object containing key/value pairs.
- The static initializer runs once when the class is first used.
- `FileInputStream` reads `.env` bytes from disk.
- `VALUES.load(input)` parses lines such as `DB_USERNAME=admin`.
- `value(key)` returns a trimmed property or an empty string.
- The five public getters return username, password, and three filenames.

### Concepts and memory

`Properties` is a map-like object on the heap. The static field keeps it reachable. The file stream is a temporary resource and is closed by try-with-resources. Returning an empty string avoids returning `null` for missing configuration, but later code must still validate required values.

### Ten questions

1. Why use a separate configuration class? **It centralizes runtime settings.**
2. What is `Properties`? **A key/value collection designed for configuration text.**
3. When does the static block run? **The first time `EnvConfig` is loaded.**
4. Why use `FileInputStream`? **It reads the `.env` file from disk.**
5. Why close the stream? **To release the operating-system file handle.**
6. What does `trim()` do? **It removes surrounding whitespace.**
7. What happens for a missing key? **`value` returns an empty string.**
8. Why are getters public? **Other packages need configuration without accessing `VALUES`.**
9. Is the password encrypted? **No; it is plain text in `.env`.**
10. What happens when `.env` is missing? **The error is printed and values become empty.**

## 6.3 `src/com/library/config/FileCreation.java`

### Purpose

This class creates the three CSV files during startup, but only when they do not already exist.

### Main fields and methods

- The private constructor prevents utility-class objects.
- `createBooksFile`, `createMembersFile`, and `createIssueRecordsFile` provide readable public entry points.
- Each method passes a configured filename and correct header to `create`.
- `create` checks for an empty filename.
- `File` represents the path.
- `getParentFile` finds a parent directory.
- `mkdirs` creates missing directories.
- `BufferedWriter` writes the header and newline.

### Memory and persistence

The `File` and writer are heap objects referenced only during the method. The actual records remain on disk. The `exists` check prevents overwriting existing user data.

### Ten questions

1. Why separate file creation from `Main`? **It keeps startup orchestration simple.**
2. Why write headers? **Services need to distinguish the first line from data rows.**
3. Why check `exists` first? **To protect existing data.**
4. What does `mkdirs` do? **It creates missing parent directories.**
5. Why reject an empty filename? **Writing to an unknown path would fail or be unsafe.**
6. Why use `BufferedWriter`? **It writes text efficiently.**
7. What does `newLine` provide? **A platform-appropriate line ending.**
8. Can this class delete data? **No, it only creates missing files.**
9. Where do filenames come from? **`EnvConfig`.**
10. What happens on the second startup? **Existing files are left unchanged.**

## 6.4 `src/com/library/model/Book.java`

### Purpose

`Book` represents one row in `books.csv`.

### Fields and methods

- `id`, `title`, `author`, and `isbn` are `String` values.
- `quantity` is an `int` because arithmetic is performed when issuing and returning.
- The constructor initializes all fields.
- Getters expose values safely to services and frames.
- Setters change values during update, issue, and return operations.
- `toRow` converts the object into five CSV values: ID, title, author, ISBN, quantity.

### Concepts and memory

Each `Book` is a heap object. The fields hold references to String objects or a primitive integer. A `List<Book>` holds references to many book objects. `toRow` creates a new String array for the table or writer.

### Ten questions

1. Why is `quantity` an int? **It must support numeric comparison and arithmetic.**
2. Why are fields private? **To enforce encapsulation.**
3. What does the constructor do? **It gives a new book its initial state.**
4. Why use getters? **Other classes can read fields through a controlled API.**
5. Why use setters? **Update workflows need to change existing objects.**
6. What does `toRow` return? **The five CSV/table values.**
7. Does `toRow` save to disk? **No, the service performs file writing.**
8. Where does a Book live? **On the heap while referenced.**
9. What happens if quantity is negative? **The UI validation should reject it.**
10. Which class creates books from CSV? **`BooksService.readAll`.**

## 6.5 `src/com/library/model/Member.java`

### Purpose

`Member` represents one row in `members.csv`.

### Fields and methods

- `id` identifies the member.
- `name`, `email`, and `phone` store contact information.
- The constructor initializes all four values.
- Getters are used by tables, import logging, and duplicate checks.
- Setters support member updates.
- `toRow` creates the four CSV values.

### Concepts and memory

Member objects are stored in a `List<Member>` while a service operation runs. Swing tables receive copied row arrays. CSV storage remains independent of the objects after `writeAll` finishes.

### Ten questions

1. What does a Member represent? **One member CSV record.**
2. Why is ID required? **It identifies a record and detects duplicates.**
3. Why are contact fields Strings? **They may contain formatting and leading zeros.**
4. Why not store phone as int? **Numeric conversion could remove leading zeros.**
5. What does `toRow` provide? **A CSV/table representation.**
6. Which service loads members? **`MembersService`.**
7. Which frame edits members? **`MemberFrame`.**
8. Which frame imports members? **`BulkImportFrame`.**
9. Are Member objects automatically persistent? **No, `writeAll` must be called.**
10. What Java concept does private state demonstrate? **Encapsulation.**

## 6.6 `src/com/library/model/IssueRecord.java`

### Purpose

`IssueRecord` represents one issue transaction, including its return state.

### Fields and methods

- `id` identifies the issue transaction.
- `bookId` links the record to a book.
- `memberId` links it to a member.
- `issueDate` stores the issue date as text in ISO format.
- `dueDate` stores the 14-day deadline.
- `returnDate` stores either a return date or `Not_Returned`.
- `setReturnDate` changes an active issue to returned.
- `toRow` converts the record to six CSV values.

### Memory and concepts

The record is a domain object, not a database row object. It models the same information in memory. `IssueFrame` changes the object, then `IssueRecordsService.writeAll` persists the list.

### Ten questions

1. Why does an issue record have both book and member IDs? **It represents the relationship between them.**
2. What does `Not_Returned` mean? **The loan is still active.**
3. Why use ISO dates? **`LocalDate.parse` can read them reliably.**
4. What method marks a return? **`setReturnDate`.**
5. Does an issue record change book quantity itself? **No, `IssueFrame` coordinates both changes.**
6. Why have an issue ID? **To update the exact transaction.**
7. How many CSV fields exist? **Six.**
8. Who calculates overdue status? **`IssueRecordsService.overdueCount`.**
9. Where are objects stored during reading? **In a list on the heap.**
10. What design idea does this class show? **A model/domain object represents one business record.**

## 6.7 `src/com/library/service/BooksService.java`

### Purpose

`BooksService` is the persistence layer for books. It translates between CSV rows and `Book` objects.

### Fields and methods

- `HEADER` is the canonical CSV header.
- `readAll` opens the configured file, skips the first line, parses each row, and creates `Book` objects.
- `findById` calls `readAll` and performs a case-insensitive ID search.
- `writeAll` replaces the file contents with the header and current book list.
- `count` returns the number of book objects.
- `row` joins fields into escaped CSV text.

### Concepts and memory

The entire file is loaded into an `ArrayList` during a read. `BufferedReader` and `BufferedWriter` are temporary heap objects and are closed automatically. `writeAll` uses a rewrite strategy: the list in memory becomes the complete file state.

### Ten questions

1. Why is the service separate from the frame? **It separates file logic from UI logic.**
2. What does `readAll` return? **A list of Book objects.**
3. Why skip the first line? **It is the CSV header.**
4. Why use `CsvParser`? **It handles CSV values more safely than simple split.**
5. What does `findById` compare? **Book IDs without case sensitivity.**
6. What does `writeAll` do to old file data? **It replaces it with the supplied list.**
7. Why escape commas and quotes? **So a field containing them does not break columns.**
8. What is the risk of rewrite persistence? **Two simultaneous writers could overwrite each other.**
9. What does `count` count? **Loaded book records, not the header.**
10. Which frame calls this service? **`BookFrame`, `IssueFrame`, and `DashboardFrame`.**

## 6.8 `src/com/library/service/MembersService.java`

### Purpose

`MembersService` performs member CSV persistence, lookup, and counting.

### Methods and behavior

- `readAll` creates Member objects from every data row.
- `findById` searches members case-insensitively.
- `writeAll` writes the header and all member rows.
- `count` returns the number of members.
- `row` quotes values containing commas or double quotes.

`MemberFrame` uses this service for CRUD. `BulkImportFrame` reads the current list, adds valid imported members, and writes once at the end.

### Ten questions

1. What is the service responsibility? **Member file persistence and lookup.**
2. Why does import call `readAll` first? **To preserve existing members.**
3. How are duplicate IDs detected? **`findById` or an ID set.**
4. Why write only once after bulk import? **It is faster and reduces partial writes.**
5. What does `count` return? **The number of member objects.**
6. Why is the header constant? **All writes should use the same column names.**
7. What happens if the file is missing? **`readAll` returns an empty list.**
8. What exception can methods throw? **`IOException`.**
9. What is held in memory? **The current member list and each Member object.**
10. Which UI uses `writeAll` for updates? **`MemberFrame` and `BulkImportFrame`.**

## 6.9 `src/com/library/service/IssueRecordsService.java`

### Purpose

This service stores and calculates issue/return data.

### Methods and behavior

- `readAll` loads issue records and skips the header.
- `activeForBook` finds an unreturned issue for a book.
- `isBookIssued` returns whether a book currently has an active issue.
- `writeAll` rewrites the issue CSV.
- `count` returns total issue transactions.
- `overdueCount` checks `Not_Returned` records and compares due dates with today.
- `nextId` creates IDs such as `I001`.
- `row` creates CSV text for an issue record.

### Memory and date logic

`LocalDate.now()` creates/returns the current date value used during calculation. `LocalDate.parse` converts ISO text into a date object. A record is overdue only when it is not returned and today is after its due date.

### Ten questions

1. What is an active issue? **A record whose return date is `Not_Returned`.**
2. How does `activeForBook` find it? **It scans all records by book ID and status.**
3. Why is `isBookIssued` useful? **It prevents duplicate active loans.**
4. What does `nextId` create? **A formatted sequential issue ID.**
5. How is overdue calculated? **Today is compared with the due date.**
6. Why use `LocalDate` instead of String comparison? **Date objects provide correct date semantics.**
7. What happens if a date is malformed? **`LocalDate.parse` can throw a date format exception.**
8. Does `count` count active issues only? **No, it counts all records.**
9. What does `writeAll` preserve? **The complete current issue list.**
10. Which frame calls this service? **`DashboardFrame` and `IssueFrame`.**

## 6.10 `src/com/library/util/CsvParser.java`

### Purpose

`CsvParser` converts one CSV line into a String array. It supports quoted values and commas inside quotes.

### Algorithm

- `values` stores completed columns.
- `value` builds the current column.
- `inQuotes` tells the parser whether commas are data or separators.
- `quoted` remembers whether the current value was quoted.
- A quote toggles quoted mode.
- A comma outside quotes finishes a column.
- A doubled quote represents one quote inside a quoted value.
- The final value is added after the loop.

### Memory

The parser creates an ArrayList, a StringBuilder, and a final String array. These are temporary heap objects and become eligible for garbage collection after the caller stops using them.

### Ten questions

1. Why not always use `split(",")`? **It breaks fields containing quoted commas.**
2. What does `inQuotes` mean? **The parser is currently inside a quoted field.**
3. Why use StringBuilder? **It efficiently builds text one character at a time.**
4. What does a doubled quote mean? **A literal quote in CSV data.**
5. What does an outside comma do? **It ends the current column.**
6. What happens for null input? **An empty array is returned.**
7. What does `trim` do for unquoted values? **It removes surrounding spaces.**
8. Is the parser a service? **No, it is a reusable utility.**
9. Who uses it? **All CSV services and bulk import.**
10. What is its output type? **`String[]`.**

## 6.11 `src/com/library/util/InputHelper.java`

### Purpose

`InputHelper` contains small reusable input functions for Swing text fields.

### Methods

- `readInt` trims text and attempts integer parsing.
- It returns `-1` when parsing fails.
- `isEmpty` handles null and blank strings.
- The private constructor prevents creating utility objects.

### Memory and concepts

The method receives a reference to a JTextField and reads its current text. It does not copy or store the field. The returned integer is a primitive value stored in the caller's stack frame.

### Ten questions

1. Why use a helper class? **To avoid repeating common input checks.**
2. What does `readInt` return for invalid text? **`-1`.**
3. Why trim before parsing? **Spaces should not cause avoidable parse failures.**
4. What does `isEmpty(null)` return? **True.**
5. Is `-1` always a safe error value? **Only when negative values are invalid for that use.**
6. Does the helper show dialogs? **No.**
7. Is it stateful? **No, its methods use supplied values.**
8. Why is the constructor private? **It is a utility-only class.**
9. Which Swing type does it accept? **`JTextField`.**
10. What Java concept does it demonstrate? **Reusable static utility methods.**

## 6.12 `src/com/library/frames/LoginFrame.java`

### Purpose

`LoginFrame` is the first visible window. It collects credentials and opens the dashboard after successful validation.

### Main variables and methods

- Color constants define the button, field, text, and placeholder colors.
- `username` is a JTextField.
- `password` is a JPasswordField, which stores typed characters separately from a normal text field.
- The constructor builds the centered form and login button.
- `label` creates consistently styled labels.
- `style` applies font, color, border, and fixed size to a field.
- `addHint` implements placeholder behavior with focus listeners.
- `at` copies GridBagConstraints and changes its row/column.
- `signIn` compares entered text with EnvConfig values.
- `BookIcon` draws the small stacked-book logo.

### UI and memory behavior

Swing components are heap objects. The frame owns references to the fields. Focus events mutate field text and password echo settings. On a valid login, `dispose` releases the window resources and a new DashboardFrame is created.

### Ten questions

1. Why extend JFrame? **The class itself is a Swing window.**
2. Why use JPasswordField? **It supports password-specific display behavior.**
3. What does the hint listener do? **It clears placeholder text on focus and restores it when empty.**
4. Why use `new String(password.getPassword())`? **It converts the password characters for comparison.**
5. Where are valid credentials stored? **In `.env` through EnvConfig.**
6. What happens on invalid login? **An error dialog is shown.**
7. What happens on valid login? **This frame closes and the dashboard opens.**
8. What does the icon implement? **Swing's Icon interface.**
9. Why use `paintIcon`? **It draws custom graphics.**
10. Why use GridBagLayout? **It supports centered, row/column-based form placement.**

## 6.13 `src/com/library/frames/DashboardFrame.java`

### Purpose

`DashboardFrame` is the main navigation and statistics window.

### Main variables and methods

- Color constants define navigation and content colors.
- Four JLabel fields display books, members, issued, and overdue counts.
- `cards` is a 2-by-2 panel for statistic cards.
- `active` tracks the selected navigation button.
- The constructor creates the window, sidebar, content, and statistics.
- `sidebar` creates navigation buttons.
- `content` creates the welcome label and four statistic cards.
- `card` creates one white statistics panel.
- `valueLabel` creates a large number label.
- `button` creates a navigation button.
- `select` changes the active navigation state.
- `refresh` reads counts from services and updates labels.
- `NavButton` paints selected and rollover backgrounds.

### Ten questions

1. What does the dashboard display? **Four live statistics and navigation.**
2. Where do statistics come from? **The three service classes.**
3. Why are labels stored as fields? **Refresh must update them later.**
4. What does `active` store? **The currently selected NavButton.**
5. What happens when Books is clicked? **A BookFrame opens.**
6. What happens when Logout is clicked? **The dashboard closes and login opens.**
7. What does Refresh do? **It rereads CSV-derived counts.**
8. Why use GridLayout for cards? **It creates equal rows and columns.**
9. What does NavButton override? **`paintComponent` for custom selection visuals.**
10. Why call revalidate and repaint? **To refresh Swing layout and drawing after values change.**

## 6.14 `src/com/library/frames/BookFrame.java`

### Purpose

`BookFrame` manages the book form and table.

### Main variables and methods

- Text fields hold ID, title, author, ISBN, quantity, and search text.
- `DefaultTableModel` stores visible table rows and prevents direct editing.
- `JTable` displays books.
- The constructor creates the frame and loads data.
- `build` creates forms, buttons, search controls, and listeners.
- `addField` adds a label and field to a form.
- `addBook` validates and appends a new Book.
- `updateBook` changes the selected Book.
- `deleteBook` confirms and removes a row.
- `filter` searches all book fields case-insensitively.
- `load` refreshes the table from the service.
- `fill` copies the selected table row into fields.
- `validQuantity` rejects invalid or negative values.
- `clear` resets controls.
- `run` catches IOException from button actions.

### Ten questions

1. Why use DefaultTableModel? **It provides mutable rows for JTable.**
2. Why make cells non-editable? **Updates should go through the form and validation.**
3. How is a duplicate ID prevented? **BooksService.findById checks before insertion.**
4. What does update use as its target? **The selected table row.**
5. Why validate quantity? **Quantity is a numeric business rule.**
6. What does filter search? **The joined ID, title, author, ISBN, and quantity text.**
7. Why call load after a change? **The visible table must match the CSV.**
8. Why call clear after a change? **It resets the form and selection.**
9. What does `run` solve? **It centralizes checked IOException handling.**
10. Where is persistent book data kept? **The configured books CSV file.**

## 6.15 `src/com/library/frames/MemberFrame.java`

### Purpose

`MemberFrame` provides member CRUD and search.

### Main variables and methods

- Text fields hold member ID, name, email, phone, and search text.
- The table model displays four columns and disables direct editing.
- `build` constructs the form, buttons, filters, table, and event listeners.
- `addMember` checks required values and duplicate ID before saving.
- `updateMember` replaces values in the selected member object.
- `deleteMember` confirms removal.
- `filter` performs case-insensitive search.
- `load` reads current members into the table.
- `member` creates a Member from form values.
- `required` checks ID and name.
- `fill` copies a table row into the form.
- `clear` empties fields and selection.

### Ten questions

1. Which fields are required? **Member ID and name.**
2. How is duplicate ID checked? **MembersService.findById.**
3. Why create a Member helper method? **It keeps object construction in one place.**
4. What does the table store? **Copies of member row values.**
5. Does table selection automatically save? **No, it only fills fields.**
6. What does update modify? **The selected Member object's fields.**
7. Why confirm delete? **Deletion is destructive.**
8. What does search compare? **All values joined into one lowercase string.**
9. Which service persists changes? **MembersService.**
10. What exception is handled? **IOException from CSV operations.**

## 6.16 `src/com/library/frames/IssueFrame.java`

### Purpose

`IssueFrame` coordinates book issue and return transactions.

### Main variables and methods

- `bookId` and `memberId` are input fields.
- `model` stores issue table rows.
- The constructor builds the form, issue/return buttons, and table.
- `issue` verifies the book and member, checks quantity and active issue state, decreases quantity, and creates a 14-day record.
- `returnBook` finds an active issue, increases book quantity, and writes the return date.
- `findBook` searches a list by ID.
- `load` refreshes issue records in the table.
- `run`, `error`, and `fail` handle event execution and errors.

### Transaction behavior

Issue and book changes are held in lists in memory first. Both files are written after the objects are changed. This keeps the normal operation coordinated, although a database transaction would be stronger if a write failed halfway through.

### Ten questions

1. What must exist before issuing? **The book and member.**
2. What quantity is required? **At least one available copy.**
3. Can a book have two active issues? **No, activeForBook prevents it.**
4. How long is the issue period? **Fourteen days.**
5. What happens to quantity during issue? **It decreases by one.**
6. What happens during return? **It increases by one.**
7. How is a returned record identified? **By issue ID.**
8. What value marks an active issue? **`Not_Returned`.**
9. Why use `findBook`? **It keeps list searching out of the main methods.**
10. What happens if a file write fails? **The exception is caught and shown.**

## 6.17 `src/com/library/frames/BulkImportFrame.java`

### Purpose

`BulkImportFrame` imports many members from a CSV/TSV file in one operation. Excel files should be exported as CSV UTF-8 or tab-delimited text first.

### Main variables and methods

- `log` displays one result per imported row.
- `importButton` starts import and is disabled until a file is selected.
- `summary` displays the selected file and final counts.
- `file` stores the selected input file.
- The constructor builds the chooser, buttons, log, and listeners.
- `choose` opens JFileChooser and accepts csv, tsv, and txt extensions.
- `importMembers` reads lines, skips headers/blank rows, validates columns, detects duplicates, creates Members, and writes once.
- `isHeader` identifies an `id,name` header.
- `fail` displays import errors.
- `HashSet ids` tracks existing and newly imported IDs.

### Memory and file behavior

The complete existing member list is loaded into memory. New valid members are added to that list. The selected file is read line by line, so the whole input file is not loaded at once. At the end, the combined member list is written to `members.csv`.

### Ten questions

1. What does the bulk screen import? **Members.**
2. Which file formats are accepted? **CSV, TSV, and TXT.**
3. Can it read native XLSX directly? **No; export XLSX to CSV or TSV first.**
4. Why use a HashSet? **Fast duplicate-ID detection.**
5. Are existing IDs preserved? **Yes.**
6. Are duplicate rows inside the import detected? **Yes, the set is updated after each valid row.**
7. What makes a row invalid? **Fewer than four values or missing ID/name.**
8. Why write once at the end? **It is more efficient than writing for every row.**
9. What does the log show? **OK, duplicate, or invalid results.**
10. What parser reads each line? **CsvParser.**

## 7. `Test/Swing.java`

This is a standalone Swing learning example and is not used by `com.library.Main`.

### How it works

- The `Test` package separates the experiment from the application package.
- `Swing` contains its own `main` method, so it can be run separately.
- A `JFrame` is the window.
- A `JLabel` displays text.
- A `JButton` provides a clickable action.
- `addActionListener` changes the label when the button is clicked.
- A `JPanel` groups the label and button.
- `setSize` gives the window a fixed size.
- `setDefaultCloseOperation` closes the program when the window closes.
- `setLocationRelativeTo(null)` centers the window.

### Ten questions

1. Is this file part of the library application? **No, it is a separate Swing example.**
2. What is the entry point? **Its own `main` method.**
3. What is JFrame? **A top-level Swing window.**
4. What does JLabel do? **It displays text.**
5. What does JButton do? **It provides a clickable control.**
6. What is an ActionListener? **Code that runs after an action such as a click.**
7. What changes after clicking? **The label text changes to `Button clicked!`.**
8. Why use JPanel? **It groups and lays out components.**
9. Why set a close operation? **To define what closing the window does.**
10. Why is this example useful? **It demonstrates the basic Swing event model.**

## 8. Other Project Files

### `.env`

Runtime configuration. It should not contain real credentials in a shared repository. The application reads it relative to the current working directory.

### `env.txt`

A reference file containing example credentials. It is not the runtime configuration because `EnvConfig` reads `.env`.

### `books.csv`, `members.csv`, `issue_records.csv`

These are persistent plain-text data files. Keep their header rows. They can be inspected with a text editor or spreadsheet program.

### `.classpath`

Eclipse metadata. It tells Eclipse that `src` is the source folder and `bin` is the compiled output folder.

### `.project`

Eclipse project metadata. It identifies the project and Java builder/nature.

### `README.md`

The shorter setup and usage guide. This document is the detailed learning reference.

### `bin/`

Generated `.class` files. It can be cleared and rebuilt safely.

### `lib/`

Optional external JAR directory. The current main application does not need an external library.

### `Test/Swing.java`

A separate experiment/test file. It is not needed by `com.library.Main`.

## 9. How to Compile and Run

### Command Prompt

```cmd
cd /d "C:\Users\Shubham\Desktop\OOPs\project\Library-Management-System"
if not exist bin mkdir bin
dir /s /b src\*.java > sources.txt
javac -d bin @sources.txt
del sources.txt
java -cp "bin;lib/*" com.library.Main
```

### PowerShell

```powershell
Set-Location "C:\Users\Shubham\Desktop\OOPs\project\Library-Management-System"
javac -d bin (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })
java -cp "bin;lib/*" com.library.Main
```

### Eclipse

1. Open Eclipse.
2. Select **File > Import > Existing Projects into Workspace**.
3. Select the `Library-Management-System` folder.
4. Check that `src` is a source folder.
5. Check that a JDK is configured under **Project > Properties > Java Build Path**.
6. Right-click `src/com/library/Main.java`.
7. Choose **Run As > Java Application**.
8. Use username `admin` and password `admin` from `.env`.

Run `com.library.Main`, not `src.Main` or plain `Main`.

## 10. Debugging Checklist

- If the main class is not found, compile all files and run `com.library.Main`.
- If credentials are empty, confirm `.env` exists in the working directory.
- If a CSV is empty, check its header and file permissions.
- If the window does not appear, use a desktop JDK instead of a headless runtime.
- If Eclipse reports a package error, make `src` the source folder.
- If old behavior appears, clear `bin` and recompile.
- If Excel data imports incorrectly, save it as CSV UTF-8 or TSV.
- If dates fail, use `yyyy-MM-dd`.
- If table data looks old, click Refresh or reopen the window.
- If two writes conflict, remember that CSV storage is intended for a single-user desktop application.

## 11. Design Limitations and Future Improvements

- Credentials are plain text in `.env`; production authentication should use secure password hashing.
- CSV rewrite operations are not database transactions.
- Multiple users writing simultaneously are not supported.
- Issue ID generation based on list size can reuse an ID after deletions.
- Input validation could be expanded for email, phone, ISBN, and duplicate IDs during updates.
- Native `.xlsx` import would require a library such as Apache POI.
- A database would be preferable for larger or multi-user deployments.
- Services could use interfaces and dependency injection for easier automated testing.
- Swing work could use `SwingWorker` for large files to avoid blocking the UI.
- Unit and integration tests should cover services, CSV parsing, and issue/return rules.

## 12. Beginner Summary

The application follows this simple pattern:

```text
Frame gets user input
        |
        v
Service validates/loads/saves data
        |
        v
Model object represents one record
        |
        v
CSV file keeps data after the program closes
```

A frame should focus on user interaction. A service should focus on persistence and business operations. A model should represent data. A utility should solve a reusable small problem. Keeping these responsibilities separate makes the application easier to understand, debug, and extend.
