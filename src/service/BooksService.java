package src.service;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import src.config.EnvConfig;

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

  public static boolean writeBooks(String bookId, String title, String author, String isbn, String quantity) throws Exception {
    FileWriter fileWriter = new FileWriter(EnvConfig.getBooksFilename(), true);

    fileWriter.append(bookId + ",");
    fileWriter.append(title + ",");
    fileWriter.append(author + ",");
    fileWriter.append(isbn + ",");
    fileWriter.append(quantity + ",");
    fileWriter.append("\n");

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
    fileWriter.write("\n");

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

/*
TODO: Exceptions 
1. Book ID already exists.
2. Quantity cannot be negative.
*/