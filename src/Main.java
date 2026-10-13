package src;

import src.config.FileCreation;
import src.frames.LoginFrame;

public class Main {
  public static void main(String[] args) throws Exception {
    try {
      FileCreation.createBooksFile();
      FileCreation.createMembersFile();
      FileCreation.createIssueRecordsFile();
      
      new LoginFrame();
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}