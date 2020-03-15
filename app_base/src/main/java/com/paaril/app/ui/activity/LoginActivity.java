package com.paaril.app.ui.activity;

import com.paaril.app.base.R;

import com.paaril.app.AppExceptionHandler;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.sms.AppSMSRetriever;
import com.paaril.app.ui.UIHelper;
import com.paaril.app.util.ObjectHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LoginActivity extends AppActivity {

  private LoginSMSReceiver smsReceiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    AppSMSRetriever.start(this);

    final TextView mobileText = findViewById(R.id.text_mobile);

    smsReceiver = new LoginSMSReceiver(this);

    Button sendOTPButton = findViewById(R.id.button_send_otp);

    View.OnClickListener sendOTPListener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String mobileNo = mobileText.getText().toString();
        try {
          
          ObjectHelper.checkNullorEmpty(mobileNo,
              "Please provide your mobile number'");

          AppServiceRepository.getInstance().getAccountManager().sendOTP(mobileNo);
          UIHelper.toast(view.getContext(),
                         "An OTP has been sent");
          enableOTPLayout();
        } catch (Exception e) {
          AppExceptionHandler.handle(LoginActivity.this,
                                     e);
        }

      }
    };

    sendOTPButton.setOnClickListener(sendOTPListener);
    findViewById(R.id.button_resend_otp).setOnClickListener(sendOTPListener);
  }

  private void enableOTPLayout() {

    final View optLayout = findViewById(R.id.layout_otp);
    optLayout.setVisibility(View.VISIBLE);
    findViewById(R.id.button_send_otp).setVisibility(View.GONE);
    findViewById(R.id.text_otp).requestFocus();
  }

  @Override
  public void onResume() {
    LocalBroadcastManager.getInstance(this).registerReceiver(smsReceiver,
                                                             new IntentFilter("otp"));
    super.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    LocalBroadcastManager.getInstance(this).unregisterReceiver(smsReceiver);
  }

  class LoginSMSReceiver extends BroadcastReceiver {

    private AppActivity appActivity;

    private TextView mobileText;
    private TextView otpText;
    private Button loginButton;

    public LoginSMSReceiver(AppActivity appActivity) {
      this.appActivity = appActivity;

      mobileText = findViewById(R.id.text_mobile);
      otpText = findViewById(R.id.text_otp);
      loginButton = findViewById(R.id.button_otp_login);

      loginButton.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View view) {

          login();

        }

      });

    }

    private void login() {

      try {
        String mobile = mobileText.getText().toString();
        String otp = otpText.getText().toString();

        ObjectHelper.checkNullorEmpty(mobile,
                                      "Please enter your mobile number");
        ObjectHelper.checkNullorEmpty(otp,
                                      "Please enter the OTP");

        AppServiceRepository.getInstance().getAccountManager().otpLogin(mobile,
                                                                        otp);

        UIHelper.startNextActivity(appActivity,
                                   true);

      } catch (Exception e) {
        AppExceptionHandler.handle(otpText.getContext(),
                                   e);
      }

    }

    @Override
    public void onReceive(Context context,
                          Intent intent) {

      if ("otp".equalsIgnoreCase(intent.getAction())) {
        final String message = intent.getStringExtra("message");

        int startIndex = message.lastIndexOf("is ") + 3;
        String otp = message.substring(startIndex,
                                       startIndex + 4);
        otpText.setText(otp);
        login();
      }

    }

  }
}