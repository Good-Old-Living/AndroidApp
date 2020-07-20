package com.paaril.app.ui.listener;

import android.content.DialogInterface;
import android.view.View;

public abstract class AppOnDismissListener extends AppEventListener implements DialogInterface.OnDismissListener {

  private View contextView;

  public AppOnDismissListener(View contextView) {
    this.contextView = contextView;
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    try {

      onDismissImpl(dialog);

    } catch (Exception e) {
      handleException(contextView,
                      e);
    }

  }

  public abstract void onDismissImpl(DialogInterface dialog);
}
