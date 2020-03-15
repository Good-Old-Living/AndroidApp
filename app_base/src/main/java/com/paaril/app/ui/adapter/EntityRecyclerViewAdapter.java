package com.paaril.app.ui.adapter;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.paaril.app.AppException;
import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class EntityRecyclerViewAdapter<T extends EntityRecyclerViewHolder>
    extends RecyclerView.Adapter<T> {

  private LayoutInflater layoutInflater;
  private Class<? extends EntityRecyclerViewHolder> entityRecyclerViewHolderClass;
  private List<DomainEntity> entityList;
  private int viewId;
  private AppCompatActivity activity;

  public EntityRecyclerViewAdapter(Context context,
                                   int viewId,
                                   Class<? extends EntityRecyclerViewHolder> entityRecyclerViewHolderClass,
                                   String entityName,
                                   String entityFilter) {

    this.entityRecyclerViewHolderClass = entityRecyclerViewHolderClass;
    this.viewId = viewId;
    layoutInflater = LayoutInflater.from(context);

    String query = entityName;
    if (entityFilter != null) {
      query += "?" + entityFilter;
    }

    
    String entitiesJson = AppServiceRepository.getInstance()
                                              .getWebServer()
                                              .getEntity(query);
    DomainEntity domainEntity = new DomainEntity(entitiesJson);
    entityList = domainEntity.getList("items");
    
    if (context instanceof AppCompatActivity) {
      activity = (AppCompatActivity) context;
    }
   

  }

  public void setActivity(AppCompatActivity activity) {
    this.activity = activity;
  }

  @Override
  public int getItemCount() {
    return entityList.size();
  }

  @Override
  public void onBindViewHolder(T viewHolder,
                               int position) {
    DomainEntity entity = entityList.get(position);
    viewHolder.setParentActivity(activity);
    viewHolder.setEntity(entity);
  }

  @SuppressWarnings("unchecked")
  @Override
  public T onCreateViewHolder(ViewGroup parent,
                              int viewType) {

    View view = layoutInflater.inflate(viewId,
                                       parent,
                                       false);

    Class<?>[] argTypes = { View.class };
    Object[] argValues = { view };

    try {
      return (T) entityRecyclerViewHolderClass.getConstructor(argTypes)
                                              .newInstance(argValues);
    } catch (IllegalAccessException | IllegalArgumentException
        | InstantiationException | InvocationTargetException
        | NoSuchMethodException | SecurityException e) {
      throw new AppException(e.getMessage(),
                             e);
    }

  }

}
