package com.paaril.app.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.paaril.app.util.IOHelper;

public class LogCatReader {

  public static String readLogs() {

    StringBuilder logBuilder = new StringBuilder();
    BufferedReader bufferedReader = null;
    InputStreamReader inputStreamReader = null;
    try {
      Process process = Runtime.getRuntime().exec("logcat -d");
      inputStreamReader = new InputStreamReader(process.getInputStream());
      bufferedReader = new BufferedReader(inputStreamReader);

      String line;
      while ((line = bufferedReader.readLine()) != null) {
        logBuilder.append(line + System.getProperty("line.separator"));
      }
    } catch (IOException e) {

    } finally {
      IOHelper.close(inputStreamReader);
      IOHelper.close(bufferedReader);
    }

    return logBuilder.toString();
  }
}
