package com.paaril.app.ui.listener;

import com.paaril.app.AppExceptionHandler;
import com.paaril.app.ui.UIProgressDialog;

import android.content.Context;
import android.view.View;

public abstract class AppEventListener {

  protected void handleException(View view,
                                 Exception exception) {
    AppExceptionHandler.handle(view.getContext(),
                               exception);
  }

  protected void handleException(Context context,
                                 Exception exception) {
    AppExceptionHandler.handle(context,
                               exception);
  }

  protected void startEventProcessing(Context context) {
    UIProgressDialog.showProgress(context);
  }

  protected void endEventProcessing() {
    UIProgressDialog.dismiss();
  }
}
