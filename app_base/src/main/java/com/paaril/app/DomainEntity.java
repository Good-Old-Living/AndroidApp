package com.paaril.app;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DomainEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  private JSONObject jsonObject;
  private Object userObject;

  public DomainEntity() {
    jsonObject = new JSONObject();
  }

  public DomainEntity(String jsonString) {

    init(jsonString);

  }

  private void init(String jsonString) {

    try {
      jsonObject = new JSONObject(jsonString);

    } catch (JSONException e) {
      throw new AppException("Unable to process the JSON data. "+jsonString, e);
    }
  }

  public Object getUserObject() {
    return userObject;
  }

  public void setUserObject(Object userObject) {
    this.userObject = userObject;
  }

  @Override
  public int hashCode() {

    String id = getId();
    return (id == null) ? 0 : id.hashCode();

  }

  @Override
  public boolean equals(Object obj) {

    DomainEntity other = (DomainEntity) obj;
    if (jsonObject == null) {
      if (other.jsonObject != null) {
        return false;
      }
    } else if (!jsonObject.equals(other.jsonObject)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return jsonObject.toString();
  }

  public String getId() {
    return getValue("id");
  }

  public void putId(String id) {
    putValue("id",
             id);
  }

  public boolean isSame(DomainEntity entity) {
    return entity.getId().equals(getId());
  }

  public DomainEntity(JSONObject jsonObject) {

    this.jsonObject = jsonObject;

  }

  private Object getJSONObject(String attrName,
                               boolean isArray) throws JSONException {

    JSONObject jsonObject = this.jsonObject;
    if (attrName.contains(".")) {
      String[] attrs = attrName.split("[.]");
      for (int i = 0; i < attrs.length - 1; i++) {

        if (jsonObject.has(attrs[i])) {
          jsonObject = (JSONObject) jsonObject.get(attrs[i]);
        } else {
          return null;
        }
      }

      attrName = attrs[attrs.length - 1];

    }

    if (!jsonObject.has(attrName)) {
      return null;
    }

    if (isArray) {

      return (JSONArray) jsonObject.getJSONArray(attrName);
    }

    return (JSONObject) jsonObject.get(attrName);

  }

  public DomainEntity getDomainEntity(String attrName) {
    try {
      JSONObject jsonObject = (JSONObject) getJSONObject(attrName,
                                                         false);

      if (jsonObject != null) {

        return new DomainEntity(jsonObject);
      }
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }

    return null;
  }

  public List<DomainEntity> getDomainEntities(String attrName) {
    List<DomainEntity> childEntities = new ArrayList<>();
    try {
      JSONArray jsonArray = (JSONArray) getJSONObject(attrName,
                                                      true);
      if (jsonArray != null) {

        int size = jsonArray.length();
        for (int i = 0; i < size; i++) {
          DomainEntity entity = new DomainEntity(jsonArray.getJSONObject(i));
          childEntities.add(entity);
        }

      }
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }

    return childEntities;
  }

  public String getValue(String attrName) {

    JSONObject jsonObject = this.jsonObject;
    try {

      if (attrName.contains(".")) {
        String[] attrs = attrName.split("[.]");
        for (int i = 0; i < attrs.length - 1; i++) {

          jsonObject = (JSONObject) jsonObject.get(attrs[i]);
        }

        attrName = attrs[attrs.length - 1];

      }

      if (!jsonObject.has(attrName)) {
        return null;
      }

      return (String) jsonObject.get(attrName);
    } catch (JSONException e) {
      return null;
      // throw new RuntimeException(e);
    }
  }

  public void putValue(String attrName,
                       Object attrValue) {

    JSONObject jsonObject = this.jsonObject;

    try {

      if (attrName.contains(".")) {
        String[] attrs = attrName.split("[.]");
        for (int i = 0; i < attrs.length - 1; i++) {

          JSONObject childJsonObject = null;

          if (jsonObject.has(attrs[i])) {
            childJsonObject = (JSONObject) jsonObject.get(attrs[i]);
          } else {

            childJsonObject = new JSONObject();
            jsonObject.put(attrs[i],
                           childJsonObject);

          }

          jsonObject = childJsonObject;
        }

        attrName = attrs[attrs.length - 1];

      }

      if (attrValue instanceof DomainEntity) {
        attrValue = ((DomainEntity) attrValue).jsonObject;
      }

      jsonObject.put(attrName,
                     attrValue);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  public List<DomainEntity> getList(String attrName) {
    try {
      JSONArray jsonArray = (JSONArray) jsonObject.get(attrName);
      if (jsonArray != null) {

        List<DomainEntity> entityList = new ArrayList<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
          entityList.add(new DomainEntity((JSONObject) jsonArray.get(i)));
        }

        return entityList;
      } else {
        return Collections.emptyList();
      }

    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException,
                                                         IOException {

    init((String) inputStream.readObject());

  }

  private void writeObject(ObjectOutputStream outputStream) throws IOException {
    outputStream.writeObject(jsonObject.toString());
  }
}
