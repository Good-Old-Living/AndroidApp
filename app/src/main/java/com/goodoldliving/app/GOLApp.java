package com.goodoldliving.app;

import android.app.Application;

import com.paaril.app.AppServiceRepository;

public class GOLApp extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    GOLConfig appConfig = new GOLConfig();
    new AppServiceRepository(this,
            appConfig);

  }

}