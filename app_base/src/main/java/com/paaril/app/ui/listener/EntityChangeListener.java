package com.paaril.app.ui.listener;

import com.paaril.app.DomainEntity;

public interface EntityChangeListener {
  
  void onEntityChange(DomainEntity entity, boolean isNew);

}
