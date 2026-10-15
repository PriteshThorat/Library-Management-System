package src.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Properties;
import java.util.Scanner;

public class ConstantsConfig {
  static private String getHelper (String constantName, String idInitial) throws Exception {
    Properties constant = new Properties();

    FileInputStream fileInputStream = new FileInputStream(EnvConfig.getConstantsFilename());

    constant.load(fileInputStream);
    fileInputStream.close();

    String constantValue = constant.getProperty(constantName);

    return (idInitial + constantValue);
  }

  static private boolean updateHelper (String constantName) throws Exception {
    Properties constant = new Properties();

    FileInputStream fileInputStream = new FileInputStream(EnvConfig.getConstantsFilename());

    constant.load(fileInputStream);
    fileInputStream.close();

    String constantValue = constant.getProperty(constantName);
    int nextConstantValue = Integer.parseInt(constantValue) + 1;

    FileOutputStream fileOutputStream = new FileOutputStream(EnvConfig.getConstantsFilename());

    constant.setProperty(constantName, Integer.toString(nextConstantValue));
    constant.store(fileOutputStream, null);

    return true;
  }

  static public String getBookId () throws Exception {
    return getHelper("BOOK_ID", "B");
  }

  static public String getMemberId () throws Exception {
    return getHelper("MEMBER_ID", "M");
  }

  static public String getIssueId () throws Exception {
    return getHelper("ISSUE_ID", "I");
  }

  static public boolean updateBookId () throws Exception {
    return updateHelper("BOOK_ID");
  }

  static public boolean updateMemberId () throws Exception {
    return updateHelper("MEMBER_ID");
  }

  static public boolean updateIssueId () throws Exception {
    return updateHelper("ISSUE_ID");
  }
}