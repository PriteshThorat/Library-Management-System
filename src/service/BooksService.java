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

  public static boolean writeBooks(List<String[]> books) throws Exception {
    FileWriter fileWriter = new FileWriter(EnvConfig.getBooksFilename());

    fileWriter.write("\n");

    for (String[] book : books) {
      fileWriter.write(String.join(",", book));
    }

    fileWriter.close();

    return true;
  }
}