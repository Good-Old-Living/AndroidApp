package com.paaril.app.util;

import com.paaril.app.AppException;

public class ObjectHelper {

  
  public static final void checkNullorEmpty(String value, String message) {
    if (isNullorEmpty(value)) {
      throw new AppException(message);
    }
  }
  
  public static final boolean isNullorEmpty(String value) {
    return value == null || value.trim().equals("");
  }
}
