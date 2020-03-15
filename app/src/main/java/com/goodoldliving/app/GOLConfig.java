package com.goodoldliving.app;

import com.paaril.app.AppConfig;

public class GOLConfig extends AppConfig {

  private static final String APP_SERVER_URL = "https://www.goodoldliving.com/";
  //private static final String PAARUL_URL = "http://192.168.1.10:8080/";
  private static final String SECURITY_APP_NAME = "";

  @Override
  public String getAppName() {
    return null;
  }

  @Override
  public String getDomainURL() {
    return APP_SERVER_URL;
  }

  @Override
  public String getSecurityAppName() {
    return SECURITY_APP_NAME;
  }
  
  @Override
  public Class<?> getLoginActivityClass() {
    return null;
  }

}
