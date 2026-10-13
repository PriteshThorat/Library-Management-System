package src.service;

import java.io.File;
import java.util.Scanner;

import src.config.EnvConfig;

public class MembersService {
  public static int getAllMembersCount () throws Exception {
    File file = new File(EnvConfig.getMembersFilename());

    Scanner membersData = new Scanner(file);
    
    int membersCount = 0;
    Boolean skip = true;

    while (membersData.hasNextLine()) {
      if (!skip) {
        membersData.nextLine();

        membersCount++;
      } else {
        membersData.nextLine();
        
        skip = false;
      }
    }

    membersData.close();

    return membersCount;
  }
}