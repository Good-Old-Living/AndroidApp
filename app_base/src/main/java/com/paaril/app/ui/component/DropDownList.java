package com.paaril.app.ui.component;

import java.util.ArrayList;
import java.util.List;

import com.paaril.app.AppServiceRepository;
import com.paaril.app.DomainEntity;
import com.paaril.app.base.R;
import com.paaril.app.ui.DropDownEntity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class DropDownList extends ArrayAdapter<DropDownEntity> {

  public DropDownList(Spinner spinner,
                      DomainEntity selectedEntity,
                      String entity,
                      String filter) {
    super(spinner.getContext(),
          R.layout.view_dropdown_enum_entity);

    final List<DomainEntity> customerAddresses = AppServiceRepository.getInstance()
                                                                     .getWebServer()
                                                                     .getDomainEntities(entity,
                                                                                        filter);
    List<DropDownEntity> ddEntityList = new ArrayList<>(customerAddresses.size());
    for (DomainEntity domainEntity : customerAddresses) {
      ddEntityList.add(new DropDownEntity(domainEntity,
                                          "name"));
    }

    addAll(ddEntityList);

    setDropDownViewResource(R.layout.view_dropdown_enum_entity);

    spinner.setAdapter(this);

    
    if (selectedEntity != null) {
      spinner.setSelection(getPosition(selectedEntity));
    }
  }

  public int getPosition(DomainEntity entity) {

    int size = getCount();
    for (int i = 0; i < size; i++) {

      DropDownEntity dropDownEntity = getItem(i);
      if (dropDownEntity.isSame(entity)) {
        return i;
      }

    }

    throw new RuntimeException("Unknown entity");
  }

  @Override
  public View getView(int position,
                      View convertView,
                      ViewGroup parent) {
    DropDownEntity entity = getItem(position);
    if (convertView == null) {

      convertView = LayoutInflater.from(getContext())
                                  .inflate(R.layout.view_dropdown_enum_entity,
                                           parent,
                                           false);

    }

    ((TextView) convertView).setText(entity.toString());

    return convertView;
  }

}
