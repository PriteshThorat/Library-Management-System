package src.service;

import java.io.File;
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
}