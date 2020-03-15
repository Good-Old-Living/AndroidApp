package com.paaril.app.ui.activity;

import com.paaril.app.AppServiceRepository;
import com.paaril.app.UncaughtAppExceptionHandler;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AppActivity extends AppCompatActivity {

   
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtAppExceptionHandler(this));
    onCreate();
  }

  protected void onCreate() {
  }

  protected String getLoggedInCustomerId() {
    return AppServiceRepository.getInstance()
                               .getAppSession()
                               .getLoggedInCustomerId();

  }

}
