package com.paaril.app.ui;

import android.app.ProgressDialog;
import android.content.Context;

public class UIProgressDialog {
  private ProgressDialog progressDoalog;
  private static UIProgressDialog instance;

  private UIProgressDialog(Context context) {

    progressDoalog = new ProgressDialog(context);
    progressDoalog.setMessage("Please wait...");
    progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
  }

  public void close() {
    if (progressDoalog != null) {
      progressDoalog.dismiss();
    }

  }

  public static void showProgress(Context context) {

    dismiss();
    instance = new UIProgressDialog(context);
    instance.progressDoalog.show();

  }
 
  public static void dismiss() {
    
    if (instance != null) {
      instance.close();
    }
  }
}
