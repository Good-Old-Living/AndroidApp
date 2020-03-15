package com.paaril.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

public class IOHelper {

  public static void close(InputStream inputStream) {

    if (inputStream != null) {
      try {
        inputStream.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
  
  public static void close(Reader reader) {

    if (reader != null) {
      try {
        reader.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
  
  public static void close(Writer writer) {

    if (writer != null) {
      try {
        writer.close();
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
}
