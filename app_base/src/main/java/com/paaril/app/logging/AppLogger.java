package com.paaril.app.logging;

import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;

public class AppLogger {

  private static AppLogger INSTANCE = new AppLogger();
  
  public static AppLogger getLogger() {
    return INSTANCE;
  }
  
  
  public void logMessage(String type,
                         String message) {

    try {

      DomainEntity logEntity = AppServiceRepository.getInstance()
                                                   .getAppSession()
                                                   .getLogMessage(type,
                                                                  message);

      AppServiceRepository.getInstance()
                          .getWebServer()
                          .postEntity("LogMessage",
                                      logEntity);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
