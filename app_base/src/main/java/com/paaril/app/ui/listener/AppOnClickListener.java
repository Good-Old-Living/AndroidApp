package com.paaril.app.ui.listener;

import android.view.View;

public abstract class AppOnClickListener extends AppEventListener
    implements View.OnClickListener {

  @Override
  public final void onClick(View view) {

    try {
      startEventProcessing(view.getContext());
      onClickImpl(view);

    } catch (Exception e) {
      handleException(view, e);
    } finally {
      endEventProcessing();
    }

  }

  public abstract void onClickImpl(View view);

}
