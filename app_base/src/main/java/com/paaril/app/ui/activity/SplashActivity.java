package com.paaril.app.ui.activity;

import com.paaril.app.base.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class SplashActivity extends Activity {

  private static int SPLASH_TIME_OUT = 500;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    ImageView logo = (ImageView) findViewById(R.id.logo_img);
    new Handler().postDelayed(new Runnable() {

      @Override
      public void run() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
      }
    }, SPLASH_TIME_OUT);
  }
}
