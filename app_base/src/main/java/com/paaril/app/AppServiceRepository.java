package com.paaril.app;

import android.content.Context;

public class AppServiceRepository {

  private AppSession appSession;
  private AccountManager accountManager;
  private WebServer webServer;
  private AppConfig appConfig;

  private static AppServiceRepository instance;

  public AppServiceRepository(Context context,
                              AppConfig appConfig) {
    appSession = new AppSession(context);
    webServer = new WebServer(appConfig.getDomainURL(),
                              appConfig.getAppName(),
                              appSession);
    this.appConfig = appConfig;

    WebServer securityWebServer = new WebServer(appConfig.getDomainURL(),
                                                appConfig.getSecurityAppName(),
                                                appSession);

    accountManager = new AccountManager(appSession,
                                    securityWebServer);

    instance = this;
  }

  public static AppServiceRepository getInstance() {
    return instance;
  }
  
  public AppConfig getAppConfig() {
    return appConfig;
  }

  public WebServer getWebServer() {
    return webServer;
  }

  public AccountManager getAccountManager() {
    return accountManager;
  }

  public AppSession getAppSession() {
    return appSession;
  }

}
