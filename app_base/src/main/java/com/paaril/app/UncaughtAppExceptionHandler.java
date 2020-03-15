package com.paaril.app;

import java.lang.Thread.UncaughtExceptionHandler;

import com.paaril.app.ui.UIProgressDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;


public class UncaughtAppExceptionHandler implements UncaughtExceptionHandler {

  private final Context context;

  public UncaughtAppExceptionHandler(Context context) {
    this.context = context;

  }

  @Override
  public void uncaughtException(Thread thread,
                                final Throwable exception) {

    new Thread() {

      @Override
      public void run() {

        try {

          Looper.prepare();
          exception.printStackTrace();
          AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
          String message = exception.getCause() == null ? exception.getMessage()
              : exception.getCause().getMessage();
          alertDialogBuilder.setMessage(message)
                            // .setPositiveButton("Report to developer about
                            // this
                            // problem.",
                            // new DialogInterface.OnClickListener() {
                            // public void onClick(DialogInterface dialog,
                            // int id) {
                            //
                            // }
                            // })
                            .setNegativeButton("Close",
                                               new DialogInterface.OnClickListener() {
                                                 public void
                                                     onClick(DialogInterface dialog,
                                                             int id) {
                                                   // Not worked!
                                                   dialog.dismiss();



                                                 }
                                               });

          UIProgressDialog.dismiss();

          alertDialogBuilder.create().show();

          Looper.loop();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

    }.start();
  }

}
