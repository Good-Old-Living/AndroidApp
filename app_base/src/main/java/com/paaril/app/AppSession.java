package com.paaril.app;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSession {

  private static final String ANDROID_SESSION = "US";
  static final String SESSION_ID = "S";
  static final String USER_ID = "E";
  static final String CUSTOMER_ID = "C";

  private SharedPreferences sharedPrefs;
  private SharedPreferences.Editor sharedPrefsEditor;

  public AppSession(Context context) {
    sharedPrefs = context.getSharedPreferences(ANDROID_SESSION,
                                               Context.MODE_PRIVATE);
    sharedPrefsEditor = sharedPrefs.edit();
  }

  public void setSessionId(String sessionId) {

    putString(SESSION_ID,
              sessionId);

  }

  public String getSessionId() {
    return sharedPrefs.getString(SESSION_ID,
                                 null);
  }

  public String getUserId() {
    return sharedPrefs.getString(USER_ID,
                                 null);
  }

  public void setUserId(String userId) {
    putString(USER_ID,
              userId);
  }

  public String getCustomerId() {
    return sharedPrefs.getString(CUSTOMER_ID,
                                 null);
  }

  public void setCustomerId(String customerId) {
    putString(CUSTOMER_ID,
              customerId);
  }

  public String getString(String name) {
    return sharedPrefs.getString(name,
                                 null);
  }

  public void putString(String name,
                        String value) {

    if (value == null) {
      return;
    }

    sharedPrefsEditor.putString(name,
                                value);
    sharedPrefsEditor.commit();
  }

  public void onLogout() {
    String sessionId = getSessionId();
    clear();
    setSessionId(sessionId);
  }

  public String getLoggedInCustomerId() {

    String customerId = getCustomerId();

    if (customerId != null) {
      return customerId;
    }

    String userId = getUserId();

    if (userId != null) {

      List<DomainEntity> customers = AppServiceRepository.getInstance()
                                                         .getWebServer()
                                                         .getDomainEntities("Customer",
                                                                            "userId=" + userId);

      if (!customers.isEmpty()) {
        customerId = customers.get(0).getId();
        setCustomerId(customerId);

        return customerId;
      }
    }

    return null;
  }

  public boolean hasUserLoggedIn() {
    return sharedPrefs.contains(USER_ID);
  }

  public String getHttpCookieValue() {

    StringBuilder strBuilder = new StringBuilder(40);

    String sessionId = getSessionId();
    String userId = getUserId();

    if (sessionId != null) {
      strBuilder.append(SESSION_ID).append("=").append(getSessionId()).append(";");
    }

    if (userId != null) {
      strBuilder.append(USER_ID).append("=").append(getUserId());
    }

    return strBuilder.toString();
  }

  public void clear() {
    sharedPrefsEditor.clear();
    sharedPrefsEditor.commit();
  }

  public DomainEntity getLogMessage(String type,
                                    String message) {

    DomainEntity logEntity = new DomainEntity();
    logEntity.putValue("sessionId",
                       getSessionId());
    logEntity.putValue("customerId",
                       getLoggedInCustomerId());
    logEntity.putValue("type",
                       type);
    logEntity.putValue("message",
                       "android:"+message);
    

    return logEntity;
  }
}
