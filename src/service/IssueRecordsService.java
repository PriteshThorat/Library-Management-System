package src.service;

import java.io.File;
import java.util.Scanner;

import src.config.EnvConfig;

public class IssueRecordsService {
  public static int getAllIssueRecordsCount () throws Exception {
    File file = new File(EnvConfig.getMembersFilename());

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
}