package com.paaril.app.ui.adapter;

import com.paaril.app.DomainEntity;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class EntityRecyclerViewHolder extends RecyclerView.ViewHolder {

  protected AppCompatActivity parentActivity;
  protected View view;

  public EntityRecyclerViewHolder(View view) {
    super(view);
    this.view = view;
  }

  public void setParentActivity(AppCompatActivity parentActivity) {
    this.parentActivity = parentActivity;
  }

  public void setEntity(DomainEntity entity) {

  }

}
