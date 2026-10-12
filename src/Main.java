package src;

import src.frames.LoginFrame;

public class Main {
  public static void main(String[] args) throws Exception {
    try {
      new LoginFrame();
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}