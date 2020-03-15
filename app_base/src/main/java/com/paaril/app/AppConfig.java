package com.paaril.app;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AppConfig {

  private final Map<String, String> propertyMap = new ConcurrentHashMap<>();

  public abstract String getDomainURL();

  public abstract String getAppName();

  public abstract String getSecurityAppName();

  public abstract Class<?> getLoginActivityClass();


  public boolean isPropertyTrue(String name) {
    String value = propertyMap.get(name);
    return value == null || value.equals("true");
  }

}
