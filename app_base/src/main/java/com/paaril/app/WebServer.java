package com.paaril.app;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.paaril.app.util.IOHelper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class WebServer {

  private static final String ENTITY_BASE_URL = "e/";

  private String domainURL;
  private String appBaseURL;

  private static CookieManager cookieManager;
  private AppSession appSession;

  public WebServer(String domainURL,
                   String appName,
                   AppSession appSession) {
    this.domainURL = domainURL;
    this.appBaseURL = domainURL;
    if (appName != null) {
      appBaseURL += appName + "/";
    }

    if (cookieManager == null) {
      cookieManager = new CookieManager();
      CookieHandler.setDefault(cookieManager);
    }

    this.appSession = appSession;

  }

  public Bitmap getImage(String imageURL) {

    imageURL = domainURL + imageURL;

    InputStream inputStream = null;
    try {

      inputStream = new java.net.URL(imageURL).openStream();
      return BitmapFactory.decodeStream(inputStream);

    } catch (IOException e) {
      throw new AppException("Unable to load the image '" + imageURL + "'", e);
    } finally {
      IOHelper.close(inputStream);
    }
  }

  public void deleteDomainEntity(String entityName,
                                 String id) {

    getData(ENTITY_BASE_URL + entityName + "/" + id,
            "DELETE",
            null);
  }

  public DomainEntity getDomainEntity(String entityName) {

    String entityJson = get(ENTITY_BASE_URL + entityName);
    return new DomainEntity(entityJson);
  }

  public DomainEntity getDomainEntity(String entityName,
                                      String id) {

    String entityJson = get(ENTITY_BASE_URL + entityName + "/" + id);
    return new DomainEntity(entityJson);
  }

  public DomainEntity getDomainEntity(String entityName,
                                      String id,
                                      String parameters) {

    String entityJson = get(ENTITY_BASE_URL + entityName + "/" + id + "?" + parameters);
    return new DomainEntity(entityJson);
  }

  public List<DomainEntity> getDomainEntities(String entity) {
    return getDomainEntities(entity,
                             null);
  }

  public List<DomainEntity> getDomainEntities(String entity,
                                              String filter) {

    String url = ENTITY_BASE_URL + entity;

    if (filter != null) {
      // filter = filter.replaceAll("[&]", "%26");
      url += "?" + filter;
    }

    String entityJson = get(url);
    DomainEntity domainEntity = new DomainEntity(entityJson);
    return domainEntity.getList("items");
  }

  public DomainEntity getFirstDomainEntity(String entity,
                                           String filter) {

    List<DomainEntity> entities = getDomainEntities(entity,
                                                    filter);
    if (entities.isEmpty()) {

      return null;
    }

    return entities.get(0);
  }

  public String getEntity(String entityName) {

    return get(ENTITY_BASE_URL + entityName);
  }

  public String get(String url) {
    return getData(url,
                   "GET",
                   null);
  }

  public String post(String url,
                     final Map<String, String> parameters) {

    DataWriter formDataWriter = new DataWriter() {

      @Override
      public void write(HttpURLConnection httpConnection) {
        StringBuilder paramBuilder = new StringBuilder();
        for (Map.Entry<String, String> param : parameters.entrySet()) {

          if (paramBuilder.length() != 0) {
            paramBuilder.append("&");
          }

          paramBuilder.append(param.getKey()).append("=").append(param.getValue());
        }

        try {

          byte[] dataInBytes = paramBuilder.toString().getBytes("UTF-8");

          httpConnection.setRequestProperty("Content-Type",
                                            "application/x-www-form-urlencoded");
          httpConnection.setRequestProperty("Content-Length",
                                            String.valueOf(dataInBytes.length));
          httpConnection.setDoOutput(true);

          httpConnection.getOutputStream().write(dataInBytes);
          httpConnection.getOutputStream().flush();

        } catch (IOException e) {
          throw new AppException("Unable to send data to server. " + e.getMessage(), e);
        }

      }

    };

    return getData(url,
                   "POST",
                   formDataWriter);
  }

  public DomainEntity postEntity(String entityName,
                                 final DomainEntity domainEntity) {

    DataWriter jsonDataWriter = new DataWriter() {

      @Override
      public void write(HttpURLConnection httpConnection) {

        try {
          byte[] dataInBytes = domainEntity.toString().getBytes("UTF-8");

          httpConnection.setRequestProperty("Content-Type",
                                            "application/json");
          httpConnection.setRequestProperty("Content-Length",
                                            String.valueOf(dataInBytes.length));
          httpConnection.setDoOutput(true);

          httpConnection.getOutputStream().write(dataInBytes);
          httpConnection.getOutputStream().flush();

        } catch (IOException e) {
          throw new AppException("Unable to send data to server. " + e.getMessage(), e);
        }
      }
    };

    String entityJson = getData(ENTITY_BASE_URL + entityName,
                                "POST",
                                jsonDataWriter);
    return new DomainEntity(entityJson);
  }

  private String getData(String url,
                         String method,
                         DataWriter dataWriter) {

    url = appBaseURL + url;

    //System.out.println("@@@@@ Request URL " + url);

    HttpURLConnection httpConnection = getConnection(url,
                                                     method,
                                                     dataWriter);

    try {

      return readData(httpConnection.getInputStream());

    } catch (AppException e) {
      throw e;
    } catch (Exception e) {
      Log.e(null,
            "Exception: " + e.getMessage());
      throw new RuntimeException(e);
    }

  }

  private String readData(InputStream inputStream) {

    InputStream bufInputStream = null;
    BufferedReader bufReader = null;

    try {

      bufInputStream = new BufferedInputStream(inputStream);
      bufReader = new BufferedReader(new InputStreamReader(bufInputStream));

      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = bufReader.readLine()) != null) {
        sb.append(line).append('\n');
      }

      //System.out.println(">>>>> RECEIVED FROM WEBSERVER " + sb);

      return sb.toString();

    }

    catch (Exception e) {
      Log.e(null,
            "Exception: " + e.getMessage());
      throw new AppException(e.getMessage(), e);
    } finally {
      IOHelper.close(bufReader);
      IOHelper.close(bufInputStream);
    }

  }

  private HttpURLConnection getConnection(String url,
                                          String method,
                                          DataWriter dataWriter) {

    try {

      HttpURLConnection httpConnection = (HttpURLConnection) new URL(url).openConnection();
      httpConnection.setRequestMethod(method);
      httpConnection.setRequestProperty("Accept",
                                        "application/json");
      httpConnection.setRequestProperty("Cookie",
                                        appSession.getHttpCookieValue());
      httpConnection.setConnectTimeout(10000);

      if (dataWriter != null) {

        dataWriter.write(httpConnection);

      }

      int responseCode = httpConnection.getResponseCode();

      if (responseCode != 200) {
        String result = readData(httpConnection.getErrorStream());
        DomainEntity entity = new DomainEntity(result);
        throw new AppException(entity.getValue("message"));
      }

      extractSessionId();
      return httpConnection;

    } catch (SocketTimeoutException e) {
      throw new AppException("Server is not accessible: " + e.getMessage(), e);
    } catch (AppException e) {
      throw e;
    } catch (Exception e) {
      throw new AppException(e.getMessage(), e);
    }

  }

  private void extractSessionId() {

    CookieStore cookieStore = cookieManager.getCookieStore();
    List<HttpCookie> cookieList = cookieStore.getCookies();

    for (HttpCookie cookie : cookieList) {

      String cookieName = cookie.getName();

      if (cookieName.equals(AppSession.SESSION_ID)) {
        appSession.setSessionId(cookie.getValue());
      }

      else if (cookieName.equals(AppSession.USER_ID)) {
        appSession.setUserId(cookie.getValue());
      }
    }
  }

  public interface DataWriter {

    void write(HttpURLConnection httpConnection);

  }
}
