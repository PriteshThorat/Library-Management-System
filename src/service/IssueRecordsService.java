package src.service;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import src.config.EnvConfig;

class IssueIDAlreadyExistsException extends Exception {
  IssueIDAlreadyExistsException () {
    super("Issue ID already exists.");
  }
}

public class IssueRecordsService {
  public static int getAllIssueRecordsCount () throws Exception {
    File file = new File(EnvConfig.getIssueRecordsFilename());

    Scanner issueRecordsData = new Scanner(file);
    
    int issueRecordsCount = 0;
    Boolean skip = true;

    while (issueRecordsData.hasNextLine()) {
      if (!skip) {
        issueRecordsData.nextLine();

        issueRecordsCount++;
      } else {
        issueRecordsData.nextLine();
        
        skip = false;
      }
    }

    issueRecordsData.close();

    return issueRecordsCount;
  }

  public static int getAllOverdueRecordsCount () throws Exception {
    File file = new File(EnvConfig.getIssueRecordsFilename());

    Scanner overdueRecordsData = new Scanner(file);
    
    int overdueRecordsCount = 0;
    boolean skip = true;

    while (overdueRecordsData.hasNextLine()) {
      if (!skip) {
        String line = overdueRecordsData.nextLine();
        String structuredData[] = line.split(",");

        if (structuredData[5].equals("Not_Returned")) {
          LocalDate dueDate = LocalDate.parse(structuredData[4]);
          LocalDate today = LocalDate.now();

          if (today.isAfter(dueDate)) {
            overdueRecordsCount++;
          }
        }
      } else {
        overdueRecordsData.nextLine();
        
        skip = false;
      }
    }

    overdueRecordsData.close();

    return overdueRecordsCount;
  }

  public static boolean issueBooks(List<String[]> issues) throws Exception {
    FileWriter fileWriter = new FileWriter(EnvConfig.getIssueRecordsFilename(), true);

    for (String[] issue : issues) {
      fileWriter.append(String.join(",", issue));
    }

    fileWriter.append("\n");

    fileWriter.close();

    return true;
  }

  public static boolean updateBookQuantity(String bookId) throws Exception {
    File file = new File(EnvConfig.getBooksFilename());

    Scanner bookData = new Scanner(file);
    String updatedBookData = "";
    String structuredData[];

    while (bookData.hasNextLine()) {
      String line = bookData.nextLine();
      structuredData = line.split(",");

      if (structuredData[0].contains(bookId)) {
        updatedBookData += structuredData[0];
        updatedBookData += ",";
        updatedBookData += structuredData[1];
        updatedBookData += ",";
        updatedBookData += structuredData[2];
        updatedBookData += ",";
        updatedBookData += structuredData[3];
        updatedBookData += ",";

        if (structuredData[4].equals("1")) {
          bookData.close();

          return false;
        }

        int newQuantity = Integer.parseInt(structuredData[4]) - 1;

        updatedBookData += Integer.toString(newQuantity);
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

  public static boolean updateBookQuantityAfterReturn (String bookId) throws Exception {
    File file = new File(EnvConfig.getBooksFilename());

    Scanner bookData = new Scanner(file);
    String updatedBookData = "";
    String structuredData[];

    while (bookData.hasNextLine()) {
      String line = bookData.nextLine();
      structuredData = line.split(",");

      if (structuredData[0].contains(bookId)) {
        updatedBookData += structuredData[0];
        updatedBookData += ",";
        updatedBookData += structuredData[1];
        updatedBookData += ",";
        updatedBookData += structuredData[2];
        updatedBookData += ",";
        updatedBookData += structuredData[3];
        updatedBookData += ",";

        int newQuantity = Integer.parseInt(structuredData[4]) + 1;

        updatedBookData += Integer.toString(newQuantity);
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

  public static boolean returnedIssueBook (String issuedId) throws Exception {
    File file = new File(EnvConfig.getIssueRecordsFilename());

    Scanner issueBookDataData = new Scanner(file);
    String updatedIssueBookData = "";
    String structuredData[];

    while (issueBookDataData.hasNextLine()) {
      String line = issueBookDataData.nextLine();
      structuredData = line.split(",");

      if (structuredData[0].equals(issuedId)) {
        updatedIssueBookData += structuredData[0];
        updatedIssueBookData += ",";
        updatedIssueBookData += structuredData[1];
        updatedIssueBookData += ",";
        updatedIssueBookData += structuredData[2];
        updatedIssueBookData += ",";
        updatedIssueBookData += structuredData[3];
        updatedIssueBookData += ",";
        updatedIssueBookData += structuredData[4];
        updatedIssueBookData += ",";
        updatedIssueBookData += LocalDate.now().toString();
        updatedIssueBookData += "\n";
      } else {
        updatedIssueBookData += line;
        updatedIssueBookData += '\n';
      }
    }
    
    FileWriter fileWriter = new FileWriter(EnvConfig.getIssueRecordsFilename());
    
    fileWriter.write(updatedIssueBookData);

    issueBookDataData.close();
    fileWriter.close();

    return true;
  }

  public static String getStatus (String[] issue) throws Exception {
    File file = new File(EnvConfig.getIssueRecordsFilename());
    FileWriter fileWriter = new FileWriter(EnvConfig.getIssueRecordsFilename(), true);

    Scanner issuedBookData = new Scanner(file);
    String structuredData[];

    while (issuedBookData.hasNextLine()) {
      String line = issuedBookData.nextLine();
      structuredData = line.split(","); 

      if (structuredData[0].contains(issue[0])) {
        if (!structuredData[5].equals("Not_Returned")) {
          fileWriter.close();
          issuedBookData.close();

          return "Returned";
        }

        LocalDate dueDate = LocalDate.parse(issue[4]);

        if (LocalDate.now().isAfter(dueDate)) {
          fileWriter.close();
          issuedBookData.close();

          return "Overdue";
        }

        fileWriter.close();
        issuedBookData.close();

        return "Issued";
      }
    }

    fileWriter.close();
    issuedBookData.close();

    return "";
  }

  public static List<String[]> readIssuedBooks() throws Exception {
    List<String[]> issuedBooks = new ArrayList<>();
    File file = new File(EnvConfig.getIssueRecordsFilename());

    Scanner issuedBooksData = new Scanner(file);

    String structuredData[];
    Boolean skip = true;

    while (issuedBooksData.hasNextLine()) {
      if (!skip) {
        structuredData = issuedBooksData.nextLine().split(",");
          
        issuedBooks.add(structuredData);
      } else {
        issuedBooksData.nextLine();
        
        skip = false;
      }
    }

    issuedBooksData.close();

    return issuedBooks;
  }

  public static String[] searchIssuedBooks (String keyword) {
    try {
      File file = new File(EnvConfig.getIssueRecordsFilename());
      FileWriter fileWriter = new FileWriter(EnvConfig.getIssueRecordsFilename(), true);

      Scanner issuedBookData = new Scanner(file);
      String issuedBook[] = {""};

      while (issuedBookData.hasNextLine()) {
        String line = issuedBookData.nextLine();

        if (line.toLowerCase().contains(keyword)) {
          issuedBook = line.split(",");
            
          issuedBookData.close();
          fileWriter.close();
          
          return issuedBook;
        }
      }

      fileWriter.close();
      issuedBookData.close();

      return issuedBook;
    } catch (Exception e) {
      String issuedBook[] = {""};

      System.out.println(e);

      return issuedBook;
    }
  }
}