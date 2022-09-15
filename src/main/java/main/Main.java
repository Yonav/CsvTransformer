package main;

import configurations.IngConfiguration;

import java.io.IOException;

public class Main {

  // located in resource folder
  private static final String inputFromResource = "private/input/test.csv";

  // located anywhere on computer, must be absolute
  private static final String output = "";
  // private static final String output = "C:\\Users\\<your-user-name>\\Documents\\csvOutput\\test.csv";

  public static void main(String[] args) throws IOException {
    transformCSV();
  }

  public static void transformCSV() throws IOException {

    IngConfiguration ingConfiguration = new IngConfiguration(false);
    ingConfiguration.readTransformWrite(inputFromResource, output);
  }
}
