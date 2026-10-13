package src.service;

import java.io.File;
import java.time.LocalDate;
import java.util.Scanner;

import src.config.EnvConfig;

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
        String structuredData[] = line.split(",", -1);

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
}