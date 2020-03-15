package com.paaril.app.ui.listener;


import android.content.Context;
import android.view.MenuItem;

public abstract class AppOnMenuItemClickListener extends AppEventListener
    implements MenuItem.OnMenuItemClickListener {

  public Context context;

  public AppOnMenuItemClickListener(Context context) {
    this.context = context;
  }

  @Override
  public boolean onMenuItemClick(MenuItem menuItem) {

    try {
      startEventProcessing(context);
      return onMenuItemClickImpl(menuItem);

    } catch (Exception e) {
      handleException(context, e);
    } finally {
      endEventProcessing();
    }

    return false;
  }

  public abstract boolean onMenuItemClickImpl(MenuItem menuItem);

}
