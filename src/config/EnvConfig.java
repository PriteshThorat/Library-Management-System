package src.config;

import java.io.FileInputStream;
import java.util.Properties;

public class EnvConfig {
  static private String helper (String envName) throws Exception {
    Properties env = new Properties();

    FileInputStream file = new FileInputStream(".env");
    env.load(file);
    file.close();

    return env.getProperty(envName);
  }

  public static String getUsername () throws Exception {
    return helper("DB_USERNAME");
  }

  public static String getPassword () throws Exception {
    return helper("DB_PASSWORD");
  }

  public  static String getBooksFilename () throws Exception {
    return helper("DB_BOOKS_FILENAME");
  }

  public  static String getMembersFilename () throws Exception {
    return helper("DB_MEMBERS_FILENAME");
  }

  public  static String getIssueRecordsFilename () throws Exception {
    return helper("DB_ISSUE_RECORDS_FILENAME");
  }

  public static String getConstantsFilename () throws Exception {
    return helper("CONSTANTS_FILE_NAME");
  }
}