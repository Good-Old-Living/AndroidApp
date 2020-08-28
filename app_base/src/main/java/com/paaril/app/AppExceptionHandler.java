package com.paaril.app;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.paaril.app.ui.UIProgressDialog;
import com.paaril.app.util.IOHelper;
import com.paaril.app.util.ObjectHelper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;

import androidx.appcompat.app.AlertDialog;

public class AppExceptionHandler {

  public static void handle(Context context,
                            Throwable exception) {

    exception.printStackTrace();
    //String message = exception.getCause() == null ? exception.getMessage() : exception.getCause().getMessage();
    handle(context,
           exception.getMessage(),
           exception);

  }

  public static void handle(Context context,
                            String message) {
    handle(context,
           message,
           null);
  }

  public static void handle(final Context context,
                            String message,
                            final Throwable exception) {

    //Thread.dumpStack();


    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

    if (ObjectHelper.isNullorEmpty(message)) {
      message = "An unknown error has occurred.";
    }

    AlertDialog.Builder builder = alertDialogBuilder.setMessage(message);

    if (exception != null) {

      builder.setPositiveButton("View Complete Error",
                                new DialogInterface.OnClickListener() {
                                  public void onClick(DialogInterface dialog,
                                                      int id) {
                                    dialog.dismiss();
                                    showStackTrace(context,
                                                   exception);
                                  }
                                });
    }
    
    builder.setNegativeButton("Close",
                              new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                  // Not worked!
                                  dialog.dismiss();

                                }
                              });

    UIProgressDialog.dismiss();

    alertDialogBuilder.create().show();
  }

  public static void showStackTrace(Context context,
                                    Throwable exception) {

    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

    String message = toString(exception);
    alertDialogBuilder.setMessage(message).setNegativeButton("Close",
                                                             new DialogInterface.OnClickListener() {
                                                               public void onClick(DialogInterface dialog,
                                                                                   int id) {
                                                                 // Not worked!
                                                                 dialog.dismiss();

                                                               }
                                                             });

    UIProgressDialog.dismiss();

    alertDialogBuilder.create().show();
  }

  private static String toString(Throwable exception) {

    StringWriter stringWriter = null;
    PrintWriter printWriter = null;

    try {
      stringWriter = new StringWriter();
      printWriter = new PrintWriter(stringWriter);
      exception.printStackTrace(printWriter);
    } finally {
      IOHelper.close(printWriter);
      IOHelper.close(stringWriter);
    }

    return stringWriter.toString();
  }

}
