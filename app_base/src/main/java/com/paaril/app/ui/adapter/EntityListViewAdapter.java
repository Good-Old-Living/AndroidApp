package com.paaril.app.ui.adapter;

import java.util.List;

import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class EntityListViewAdapter extends ArrayAdapter<DomainEntity> {

  private LayoutInflater layoutInflater;
  private int resource;

  private String entityName;
  private String entityFilter;

  public EntityListViewAdapter(Context context,
                               int resource,
                               String entityName,
                               String entityFilter) {
    super(context,
          resource);

    this.resource = resource;
    this.entityName = entityName;
    this.entityFilter = entityFilter;

    layoutInflater = LayoutInflater.from(context);
    
    load();

  }

  public void load() {
    List<DomainEntity> entityList = AppServiceRepository.getInstance()
                                                        .getWebServer()
                                                        .getDomainEntities(entityName,
                                                                           entityFilter);

    onLoad(entityList);
    clear();
    addAll(entityList);
  }

  public void onLoad(List<DomainEntity> entityList) {
    
  }
  
  @Override
  public View getView(int position,
                      View convertView,
                      ViewGroup parent) {
    View view = layoutInflater.inflate(resource,
                                       parent,
                                       false);
    DomainEntity entity = getItem(position);
    setEntity(view,
              entity);

    return view;

  }

  public void setEntity(View view,
                        DomainEntity shoppingCartLineItem) {

  }
}
