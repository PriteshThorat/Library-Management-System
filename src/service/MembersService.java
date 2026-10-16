package src.service;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

import src.config.ConstantsConfig;
import src.config.EnvConfig;

class PleaseFillInAllFieldsException extends Exception {
  PleaseFillInAllFieldsException () {
    super("Please fill in all fields.");
  }
}

class PleaseEnterAValidEmailAddressException extends Exception {
  PleaseEnterAValidEmailAddressException () {
    super("Please enter a valid email address.");
  }
}

class PhoneNumberMustContain10DigitsException extends Exception {
  PhoneNumberMustContain10DigitsException () {
    super("Phone number must contain 10 digits.");
  }
}

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

  public static boolean writeMembers (String name, String email, String phone) throws Exception {
    if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
      throw new PleaseFillInAllFieldsException();
    }

    if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
      throw new PleaseEnterAValidEmailAddressException();
    }

    if (!phone.matches("\\d{10}")) {
      throw new PhoneNumberMustContain10DigitsException();
    }

    FileWriter fileWriter = new FileWriter(EnvConfig.getMembersFilename(), true);

    fileWriter.append(ConstantsConfig.getMemberId() + ",");

    ConstantsConfig.updateMemberId();

    fileWriter.append(name + ",");
    fileWriter.append(email + ",");
    fileWriter.append(phone + "\n");

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
    if (memberId.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
      throw new PleaseFillInAllFieldsException();
    }

    if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
      throw new PleaseEnterAValidEmailAddressException();
    }

    if (!phone.matches("\\d{10}")) {
      throw new PhoneNumberMustContain10DigitsException();
    }

    File file = new File(EnvConfig.getMembersFilename());

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
    
    FileWriter fileWriter = new FileWriter(EnvConfig.getMembersFilename());
    
    fileWriter.write(updatedMemberData);

    memberData.close();
    fileWriter.close();

    return true;
  }

  public static boolean deleteMember (String memberId) throws Exception {
    File file = new File(EnvConfig.getMembersFilename());

    Scanner memberData = new Scanner(file);
    String updatedMemberData = "";

    while (memberData.hasNextLine()) {
      String line = memberData.nextLine();

      if (!line.contains(memberId)) {
        updatedMemberData += line;
        updatedMemberData += "\n";
      }
    }
    
    FileWriter fileWriter = new FileWriter(EnvConfig.getMembersFilename());
    
    fileWriter.write(updatedMemberData);

    memberData.close();
    fileWriter.close();

    return true;
  }

  public static String[] searchMember (String keyword) {
    try {
      File file = new File(EnvConfig.getMembersFilename());
      FileWriter fileWriter = new FileWriter(EnvConfig.getMembersFilename(), true);

      Scanner memberData = new Scanner(file);
      String member[] = {""};

      while (memberData.hasNextLine()) {
        String line = memberData.nextLine();

        if (line.toLowerCase().contains(keyword)) {
          member = line.split(",");
            
          memberData.close();
          fileWriter.close();
          
          return member;
        }
      }

      fileWriter.close();
      memberData.close();

      return member;
    } catch (Exception e) {
      String member[] = {""};

      System.out.println(e);

      return member;
    }
  }
}