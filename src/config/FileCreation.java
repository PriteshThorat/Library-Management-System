package src.config;

import java.io.File;
import java.io.FileWriter;

public class FileCreation {
  public static void createBooksFile () throws Exception {
    File file = new File(EnvConfig.getBooksFilename());

    if (!file.exists()) {
      file.createNewFile();

      FileWriter fileWriter = new FileWriter(EnvConfig.getBooksFilename());

      fileWriter.write("Book ID,Title,Author,isbn,quantity");

      fileWriter.close();
    }
  }

  public static void createMembersFile () throws Exception {
    File file = new File(EnvConfig.getMembersFilename());

    if (!file.exists()) {
      file.createNewFile();

      FileWriter fileWriter = new FileWriter(EnvConfig.getMembersFilename());

      fileWriter.write("member_id,name,email,phone");

      fileWriter.close();
    }
  }

  public static void createIssueRecordsFile () throws Exception {
    File file = new File(EnvConfig.getIssueRecordsFilename());

    if (!file.exists()) {
      file.createNewFile();

      FileWriter fileWriter = new FileWriter(EnvConfig.getIssueRecordsFilename());

      fileWriter.write("issue_id,book_id,member_id,issue_date,due_date,return_date,");

      fileWriter.close();
    }
  }
}