package src.service;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
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

  public static boolean writeMembers(List<String[]> members) throws Exception {
    FileWriter fileWriter = new FileWriter(EnvConfig.getMembersFilename(), true);

    for (String[] member : members) {
      fileWriter.append(String.join(",", member));
    }

    fileWriter.append("\n");

    fileWriter.close();

    return true;
  }

  public static List<String[]> readMembers() throws Exception {
    List<String[]> members = new ArrayList<>();
    File file = new File(EnvConfig.getMembersFilename());

    Scanner membersData = new Scanner(file);

    String structuredData[];
    Boolean skip = true;

    while (membersData.hasNextLine()) {
      if (!skip) {
        structuredData = membersData.nextLine().split(",");
          
        members.add(structuredData);
      } else {
        membersData.nextLine();
        
        skip = false;
      }
    }

    membersData.close();

    return members;
  }

  public static boolean updateMember (String memberId, String name, String email, String phone) throws Exception {
    File file = new File(EnvConfig.getBooksFilename());

    Scanner memberData = new Scanner(file);
    String updatedMemberData = "";

    while (memberData.hasNextLine()) {
      String line = memberData.nextLine();

      if (line.contains(memberId)) {
        updatedMemberData += memberId;
        updatedMemberData += ",";
        updatedMemberData += name;
        updatedMemberData += ",";
        updatedMemberData += email;
        updatedMemberData += ",";
        updatedMemberData += phone;
        updatedMemberData += "\n";
      } else {
        updatedMemberData += line;
        updatedMemberData += '\n';
      }
    }
    
    FileWriter fileWriter = new FileWriter(EnvConfig.getBooksFilename());
    
    fileWriter.write(updatedMemberData);
    fileWriter.write("\n");

    memberData.close();
    fileWriter.close();

    return true;
  }
}