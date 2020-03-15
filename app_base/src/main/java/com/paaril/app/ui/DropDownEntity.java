package com.paaril.app.ui;

import com.paaril.app.DomainEntity;

public class DropDownEntity {

  private DomainEntity entity;
  private String displayAttribute;

  public DropDownEntity(DomainEntity entity,
                        String displayAttribute) {
    this.entity = entity;
    this.displayAttribute = displayAttribute;
  }

  public DomainEntity getDomainEntity() {
    return entity;
  }

  public boolean isSame(DomainEntity entity) {
    return this.entity.isSame(entity);
  }
  
  public String toString() {
    return entity.getValue(displayAttribute);
  }

}
