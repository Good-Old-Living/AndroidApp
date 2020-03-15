package com.paaril.app.ui.listener;

import android.content.DialogInterface;
import android.view.View;

public abstract class AppDialogOnClickListener extends AppEventListener
    implements DialogInterface.OnClickListener {

  private View contextView;

  public AppDialogOnClickListener(View contextView) {
    this.contextView = contextView;
  }

  public final void onClick(DialogInterface dialog,
                            int which) {

    try {

      onClickImpl(dialog,
                  which);

    } catch (Exception e) {
      handleException(contextView,
                      e);
    }

  }

  public abstract void onClickImpl(DialogInterface dialog,
                                   int which);

}
