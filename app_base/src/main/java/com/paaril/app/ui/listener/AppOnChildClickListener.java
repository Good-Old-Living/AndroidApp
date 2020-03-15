package com.paaril.app.ui.listener;

import android.view.View;
import android.widget.ExpandableListView;

public abstract class AppOnChildClickListener extends AppEventListener
    implements ExpandableListView.OnChildClickListener {

  @Override
  public boolean onChildClick(ExpandableListView parent,
                              View view,
                              int groupPosition,
                              int childPosition,
                              long id) {

    try {

      return onChildClickImpl(parent,
                              view,
                              groupPosition,
                              childPosition,
                              id);

    } catch (Exception e) {
      handleException(view,
                      e);
    }

    return false;
  }

  public abstract boolean onChildClickImpl(ExpandableListView parent,
                                           View view,
                                           int groupPosition,
                                           int childPosition,
                                           long id);

}
