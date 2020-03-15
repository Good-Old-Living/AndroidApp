package com.paaril.app;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import com.paaril.app.util.ObjectHelper;

public class AccountManager {

  private static final String ACCOUNT_URL = "/a/";

  private static final String SEND_OTP_URL = ACCOUNT_URL + "sendAndOTP";
  private static final String OTP_LOGIN_URL = ACCOUNT_URL + "loginOTP";
  private static final String LOGOUT_URL = ACCOUNT_URL + "logout";

  private AppSession appSession;
  private WebServer webServer;

  AccountManager(AppSession appSession, WebServer webServer) {
    this.appSession = appSession;
    this.webServer = webServer;
  }

  public String otpLogin(String mobileNo,
                         String otp) {

    ObjectHelper.checkNullorEmpty(mobileNo,
                                  "Please provide your mobile number");
    ObjectHelper.checkNullorEmpty(otp,
                                  "OTP is required");

    Map<String, String> paramMap = new HashMap<>(2);
    paramMap.put("mobile",
                 mobileNo);
    paramMap.put("otp",
                 otp);

    webServer.post(OTP_LOGIN_URL,
                   paramMap);

    return null;

  }

  public String sendOTP(String mobileNo) {

    ObjectHelper.checkNullorEmpty(mobileNo,
                                  "Please provide your mobile number");

    Map<String, String> paramMap = new HashMap<>(1);
    paramMap.put("mobile",
                 mobileNo);
    paramMap.put("app",
                 "a");

    webServer.post(SEND_OTP_URL,
                   paramMap);

    return null;

  }

  public void logout(AppCompatActivity activity) {
    webServer.get(LOGOUT_URL);
  }

}
