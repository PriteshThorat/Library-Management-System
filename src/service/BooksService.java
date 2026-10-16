package src.service;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import src.config.ConstantsConfig;
import src.config.EnvConfig;

class BookIDAlreadyExistsException extends Exception {
  BookIDAlreadyExistsException () {
    super("Book ID already exists.");
  }
}

class QuantityCannotBeNegativeException extends Exception {
  QuantityCannotBeNegativeException () {
    super("Quantity cannot be negative.");
  }
}

class QuantityMustBeAValidIntegerException extends Exception {
  QuantityMustBeAValidIntegerException () {
    super("Quantity must be a valid integer");
  }
}

public class BooksService {
  public static int getAllBooksCount () throws Exception {
    File file = new File(EnvConfig.getBooksFilename());

    Scanner booksData = new Scanner(file);
    
    int booksCount = 0;
    Boolean skip = true;

    while (booksData.hasNextLine()) {
      if (!skip) {
        booksData.nextLine();

        booksCount++;
      } else {
        booksData.nextLine();
        
        skip = false;
      }
    }

    booksData.close();

    return booksCount;
  }

  public static List<String[]> readBooks() throws Exception{
    List<String[]> books = new ArrayList<>();
    File file = new File(EnvConfig.getBooksFilename());

    Scanner booksData = new Scanner(file);

    String structuredData[];
    Boolean skip = true;

    while (booksData.hasNextLine()) {
      if (!skip) {
        structuredData = booksData.nextLine().split(",");
          
        books.add(structuredData);
      } else {
        booksData.nextLine();
        
        skip = false;
      }
    }

    booksData.close();

    return books;
  }

  public static boolean writeBooks(String title, String author, String isbn, String quantity) throws Exception {
    FileWriter fileWriter = new FileWriter(EnvConfig.getBooksFilename(), true);

    try {
      int quantityInInt = Integer.parseInt(quantity);

      if (quantityInInt < 0) {
        throw new QuantityCannotBeNegativeException();
      }
    } catch (NumberFormatException e) {
      throw new QuantityMustBeAValidIntegerException();
    }

    fileWriter.append(ConstantsConfig.getBookId() + ",");

    ConstantsConfig.updateBookId();

    fileWriter.append(title + ",");
    fileWriter.append(author + ",");
    fileWriter.append(isbn + ",");
    fileWriter.append(quantity + "\n");

    fileWriter.close();

    return true;
  }

  public static boolean deleteBook(String bookId) throws Exception {
    File file = new File(EnvConfig.getBooksFilename());

    Scanner BookData = new Scanner(file);
    String updatedBookData = "";

    while (BookData.hasNextLine()) {
      String line = BookData.nextLine();

      if (!line.contains(bookId)) {
        updatedBookData += line;
        updatedBookData += "\n";
      }
    }
    
    FileWriter fileWriter = new FileWriter(EnvConfig.getBooksFilename());
    
    fileWriter.write(updatedBookData);

    BookData.close();
    fileWriter.close();

    return true;
  }

  public static boolean updateBook(String bookId, String title, String author, String isbn, String quantity) throws Exception {
    File file = new File(EnvConfig.getBooksFilename());

    Scanner bookData = new Scanner(file);
    String updatedBookData = "";

    while (bookData.hasNextLine()) {
      String line = bookData.nextLine();

      if (line.contains(bookId)) {
        updatedBookData += bookId;
        updatedBookData += ",";
        updatedBookData += title;
        updatedBookData += ",";
        updatedBookData += author;
        updatedBookData += ",";
        updatedBookData += isbn;
        updatedBookData += ",";
        updatedBookData += quantity;
        updatedBookData += "\n";
      } else {
        updatedBookData += line;
        updatedBookData += '\n';
      }
    }
    
    FileWriter fileWriter = new FileWriter(EnvConfig.getBooksFilename());
    
    fileWriter.write(updatedBookData);

    bookData.close();
    fileWriter.close();

    return true;
  }

  public static String[] searchBooks (String keyword) {
    try {
      File file = new File(EnvConfig.getBooksFilename());
      FileWriter fileWriter = new FileWriter(EnvConfig.getBooksFilename(), true);

      Scanner bookData = new Scanner(file);
      String book[] = {""};

      while (bookData.hasNextLine()) {
        String line = bookData.nextLine();

        if (line.toLowerCase().contains(keyword)) {
          book = line.split(",");
            
          bookData.close();
          fileWriter.close();
          
          return book;
        }
      }

      fileWriter.close();
      bookData.close();

      return book;
    } catch (Exception e) {
      String book[] = {""};

      System.out.println(e);

      return book;
    }
  }
}